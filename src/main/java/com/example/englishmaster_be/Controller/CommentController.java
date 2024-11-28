package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.Comment.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private IUserService IUserService;
    @Autowired
    private ITopicService ITopicService;
    @Autowired
    private IPostService IPostService;
    @Autowired
    private ICommentService ICommentService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @GetMapping(value = "/{commentId:.+}/getAllComment")
    public ResponseEntity<ResponseModel> getListCommentToCommentId(@PathVariable UUID commentId){
        ResponseModel responseModel = new ResponseModel();
        try {
            Comment comment = ICommentService.findCommentToId(commentId);
            List<CommentResponse> commentResponseList = new ArrayList<>();

            List<Comment> commentList = ICommentService.findAllByCommentParent(comment);
            if(commentList != null){
                for(Comment commentChild: commentList){
                    commentResponseList.add(new CommentResponse(commentChild, ICommentService.checkCommentParent(commentChild)));
                }
            }

            responseModel.setMessage("Show list Comment child successful");
            responseModel.setResponseData(commentResponseList);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list Comment child fail: " + e.getMessage());
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addCommentToTopic")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createCommentToTopic(@PathVariable UUID topicId, @RequestBody CreateCommentDTO createCommentDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Topic topic = ITopicService.findTopicById(topicId);

            Comment comment = new Comment();
            comment.setUserComment(user);
            comment.setContent(createCommentDTO.getCommentContent());
            comment.setTopic(topic);

            ICommentService.save(comment);

            CommentResponse commentResponse = new CommentResponse(comment, ICommentService.checkCommentParent(comment));
            messagingTemplate.convertAndSend("/Comment/Topic/"+topicId, commentResponse);

            responseModel.setMessage("Create Comment successful");
            responseModel.setResponseData(commentResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create Comment fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/{postId:.+}/addCommentToPost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createCommentToPost(@PathVariable UUID postId, @RequestBody CreateCommentDTO createCommentDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Post post = IPostService.findPostById(postId);

            Comment comment = new Comment();
            comment.setUserComment(user);
            comment.setContent(createCommentDTO.getCommentContent());
            comment.setPost(post);

            ICommentService.save(comment);

            CommentResponse commentResponse = new CommentResponse(comment, ICommentService.checkCommentParent(comment));
            messagingTemplate.convertAndSend("/Comment/Post/"+postId, commentResponse);

            responseModel.setMessage("Create Comment successful");
            responseModel.setResponseData(commentResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create Comment fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @PostMapping(value = "/{commentId:.+}/addCommentToComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createCommentToComment(@PathVariable UUID commentId, @RequestBody CreateCommentDTO createCommentDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Comment commentParent = ICommentService.findCommentToId(commentId);

            Comment comment = new Comment();
            comment.setUserComment(user);
            comment.setContent(createCommentDTO.getCommentContent());
            comment.setCommentParent(commentParent);
            if(commentParent.getTopic() != null){
                comment.setTopic(commentParent.getTopic());
            }
            if(commentParent.getPost() != null){
                comment.setPost(commentParent.getPost());
            }

            ICommentService.save(comment);

            CommentResponse commentResponse = new CommentResponse(comment, ICommentService.checkCommentParent(comment));
            messagingTemplate.convertAndSend("/Comment/commentParent/"+commentId, commentResponse);

            responseModel.setMessage("Create Comment successful");
            responseModel.setResponseData(commentResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create Comment fail: " + e.getMessage());
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PatchMapping(value = "/{commentId:.+}/updateComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateComment(@PathVariable UUID commentId, @RequestBody CreateCommentDTO createCommentDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Comment comment = ICommentService.findCommentToId(commentId);

            if(!comment.getUserComment().getUserId().equals(user.getUserId())){
                responseModel.setMessage("Don't update Comment");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            comment.setContent(createCommentDTO.getCommentContent());
            comment.setUpdateAt(LocalDateTime.now());

            ICommentService.save(comment);

            CommentResponse commentResponse = new CommentResponse(comment, ICommentService.checkCommentParent(comment));
            responseModel.setMessage("Update Comment successful");
            responseModel.setResponseData(commentResponse);
            messagingTemplate.convertAndSend("/Comment/updateComment/"+commentId, commentResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update Comment fail: " + e.getMessage());
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/{commentId:.+}/deleteComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteComment(@PathVariable UUID commentId){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Comment comment = ICommentService.findCommentToId(commentId);

            if(!comment.getUserComment().getUserId().equals(user.getUserId())){
                responseModel.setMessage("Don't update Comment");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            ICommentService.deleteComment(comment);

            responseModel.setMessage("Delete Comment successful");

            messagingTemplate.convertAndSend("/Comment/deleteComment/"+commentId, commentId);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete Comment fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }
}
