package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Mapper.PostMapper;
import com.example.englishmaster_be.Model.Request.Post.PostFilterRequest;
import com.example.englishmaster_be.Model.Request.Post.PostRequest;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.entity.CommentEntity;
import com.example.englishmaster_be.entity.PostEntity;
import com.example.englishmaster_be.entity.QPostEntity;
import com.example.englishmaster_be.Model.Response.PostResponse;
import com.example.englishmaster_be.entity.UserEntity;
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



    @Override
    public PostEntity getPostById(UUID postId) {

        return postRepository.findByPostId(postId)
                .orElseThrow(
                        () -> new BadRequestException("PostEntity not found")
                );
    }

    @Override
    public FilterResponse<?> getListPost(PostFilterRequest filterRequest) {

        FilterResponse<PostResponse> filterResponse = FilterResponse.<PostResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        long totalElements = Optional.ofNullable(queryFactory.select(QPostEntity.postEntity.count()).from(QPostEntity.postEntity).fetchOne()).orElse(0L);
        long totalPages = (long)Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if(Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QPostEntity.postEntity.updateAt.desc();
        else
            orderSpecifier = QPostEntity.postEntity.updateAt.asc();

        JPAQuery<PostEntity> query = queryFactory.selectFrom(QPostEntity.postEntity)
                                            .orderBy(orderSpecifier)
                                            .offset(filterResponse.getOffset())
                                            .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                PostMapper.INSTANCE.toPostResponseList(query.fetch())
        );

        return filterResponse;
    }


    @Transactional
    @Override
    public PostEntity savePost(PostRequest postRequest) {

        UserEntity user = userService.currentUser();

        PostEntity post;

        if(postRequest.getPostId() != null) {

            post = getPostById(postRequest.getPostId());

            if(!post.getUserPost().getUserId().equals(user.getUserId()))
                throw new BadRequestException("Don't update PostEntity");

        }
        else post = PostEntity.builder()
                .userPost(user)
                .createAt(LocalDateTime.now())
                .build();

        post.setContent(postRequest.getContent());
        post.setUpdateAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Override
    public List<CommentEntity> getListCommentToPostId(UUID postId) {

        PostEntity post = getPostById(postId);

        if(post.getComments() == null) return new ArrayList<>();

        return post.getComments().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(CommentEntity::getCreateAt).reversed())
                .toList();

    }


    @Transactional
    @Override
    public void deletePost(UUID postId) {

        UserEntity user = userService.currentUser();

        PostEntity post = getPostById(postId);

        if(!post.getUserPost().getUserId().equals(user.getUserId()))
            throw new BadRequestException("Don't delete PostEntity");

        postRepository.delete(post);

    }
}
