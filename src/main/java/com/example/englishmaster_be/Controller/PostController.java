package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Post.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    private ICommentService ICommentService;
    @Autowired
    private IPostService IPostService;
    @Autowired
    private IUserService IUserService;

    @Autowired
    private JPAQueryFactory queryFactory;

    @GetMapping(value = "/listPost")
    public ResponseEntity<ResponseModel> listPost(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
                                                  @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                  @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection){
        ResponseModel responseModel = new ResponseModel();
        try {

            OrderSpecifier<?> orderSpecifier;


            if(Sort.Direction.DESC.equals(sortDirection)){
                orderSpecifier = QPost.post.updateAt.desc();
            }else {
                orderSpecifier = QPost.post.updateAt.asc();
            }

            JPAQuery<Post> query = queryFactory.selectFrom(QPost.post)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);


            List<Post> postList = query.fetch();

            List<PostResponse> postResponseList = new ArrayList<>();

            for (Post post : postList) {
                PostResponse postResponse = new PostResponse(post);
                postResponseList.add(postResponse);
            }

            responseModel.setMessage("List post successful");
            responseModel.setResponseData(postResponseList);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("List post fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/createPost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createPost(@RequestBody CreatePostDTO createPostDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Post post = new Post();
            post.setUserPost(user);
            post.setContent(createPostDTO.getContent());

            IPostService.save(post);

            PostResponse postResponse = new PostResponse(post);
            responseModel.setMessage("Create post successful");
            responseModel.setResponseData(postResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Create post fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PatchMapping(value = "/{postId:.+}/updatePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updatePost(@PathVariable UUID postId, @RequestBody CreatePostDTO updatePostDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Post post = IPostService.findPostById(postId);

            if(!post.getUserPost().getUserId().equals(user.getUserId())){
                responseModel.setMessage("Don't update post");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            post.setContent(updatePostDTO.getContent());
            post.setUpdateAt(LocalDateTime.now());

            IPostService.save(post);

            PostResponse postResponse = new PostResponse(post);
            responseModel.setMessage("Update post successful");
            responseModel.setResponseData(postResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Update post fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{postId:.+}/getAllCommentToPost")
    public ResponseEntity<ResponseModel> getListCommentToPostId(@PathVariable UUID postId){
        ResponseModel responseModel = new ResponseModel();
        try {
            Post post = IPostService.findPostById(postId);
            List<Comment> commentList = new ArrayList<>();;

            List<CommentResponse> commentResponseList = new ArrayList<>();

            if(post.getComments() != null && !post.getComments().stream().toList().isEmpty()){
                commentList = post.getComments().stream().sorted(Comparator.comparing(Comment::getCreateAt).reversed()).toList();;
                for (Comment comment : commentList) {
                    if (comment.getCommentParent() == null) {
                        commentResponseList.add(new CommentResponse(comment, ICommentService.checkCommentParent(comment)));
                    }
                }
            }

            responseModel.setMessage("Show list comment successful");
            responseModel.setResponseData(commentResponseList);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Show list comment fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{postId:.+}/deletePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deletePost(@PathVariable UUID postId){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            Post post = IPostService.findPostById(postId);
            if(!post.getUserPost().getUserId().equals(user.getUserId())){
                responseModel.setMessage("Don't delete post");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }
            IPostService.delete(post);
            responseModel.setMessage("Delete post successful");
            responseModel.setStatus("success");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Delete post fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
