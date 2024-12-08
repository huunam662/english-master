package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.DTO.Post.PostFilterRequest;
import com.example.englishmaster_be.DTO.Post.SavePostDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.Comment;
import com.example.englishmaster_be.Model.Post;
import com.example.englishmaster_be.Model.QPost;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import com.example.englishmaster_be.Model.Response.PostResponse;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements IPostService {

    JPAQueryFactory queryFactory;

    PostRepository postRepository;

    IUserService userService;

    ICommentService commentService;


    @Override
    public Post findPostById(UUID postId) {

        return postRepository.findByPostId(postId)
                .orElseThrow(
                        () -> new BadRequestException("Post not found")
                );
    }

    @Override
    public FilterResponse<?> getListPost(PostFilterRequest filterRequest) {

        FilterResponse<PostResponse> filterResponse = FilterResponse.<PostResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        long totalElements = Optional.ofNullable(queryFactory.select(QPost.post.count()).from(QPost.post).fetchOne()).orElse(0L);
        long totalPages = (long)Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        OrderSpecifier<?> orderSpecifier;

        if(Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QPost.post.updateAt.desc();
        else
            orderSpecifier = QPost.post.updateAt.asc();

        JPAQuery<Post> query = queryFactory.selectFrom(QPost.post)
                                            .orderBy(orderSpecifier)
                                            .offset(filterResponse.getOffset())
                                            .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(PostResponse::new).toList()
        );

        return filterResponse;
    }


    @Transactional
    @Override
    public PostResponse savePost(UUID updatePostId, SavePostDTO savePostDTO) {

        User user = userService.currentUser();

        Post post;

        if(updatePostId != null) {
            post = findPostById(updatePostId);

            if(!post.getUserPost().getUserId().equals(user.getUserId()))
                throw new BadRequestException("Don't update Post");

        }
        else post = Post.builder()
                .userPost(user)
                .createAt(LocalDateTime.now())
                .build();

        post.setContent(savePostDTO.getContent());
        post.setUpdateAt(LocalDateTime.now());

        post = postRepository.save(post);

        return new PostResponse(post);
    }

    @Override
    public List<CommentResponse> getListCommentToPostId(UUID postId) {

        Post post = findPostById(postId);

        if(post.getComments() == null) return new ArrayList<>();

        return post.getComments().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed())
                .map(comment -> new CommentResponse(comment, commentService.checkCommentParent(comment)))
                .toList();

    }


    @Transactional
    @Override
    public void deletePost(UUID postId) {

        User user = userService.currentUser();

        Post post = findPostById(postId);

        if(!post.getUserPost().getUserId().equals(user.getUserId()))
            throw new BadRequestException("Don't delete Post");


        postRepository.delete(post);

    }
}
