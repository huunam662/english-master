package com.example.englishmaster_be.Controller;

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
import java.util.Comparator;
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

            responseModel.setMessage("Show list comment child successful");
            responseModel.setResponseData(commentResponseList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Show list comment child fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addCommentToTopic")
    @PreAuthorize("hasRole('USER')")
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
            messagingTemplate.convertAndSend("/comment/topic/"+topicId, commentResponse);

            responseModel.setMessage("Create comment successful");
            responseModel.setResponseData(commentResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Create comment fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/{postId:.+}/addCommentToPost")
    @PreAuthorize("hasRole('USER')")
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
            messagingTemplate.convertAndSend("/comment/post/"+postId, commentResponse);

            responseModel.setMessage("Create comment successful");
            responseModel.setResponseData(commentResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Create comment fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    @PostMapping(value = "/{commentId:.+}/addCommentToComment")
    @PreAuthorize("hasRole('USER')")
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
            messagingTemplate.convertAndSend("/comment/commentParent/"+commentId, commentResponse);

            responseModel.setMessage("Create comment successful");
            responseModel.setResponseData(commentResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Create comment fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PatchMapping(value = "/{commentId:.+}/updateComment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> updateComment(@PathVariable UUID commentId, @RequestBody CreateCommentDTO createCommentDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Comment comment = ICommentService.findCommentToId(commentId);

            if(!comment.getUserComment().getUserId().equals(user.getUserId())){
                responseModel.setMessage("Don't update comment");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            comment.setContent(createCommentDTO.getCommentContent());
            comment.setUpdateAt(LocalDateTime.now());

            ICommentService.save(comment);

            CommentResponse commentResponse = new CommentResponse(comment, ICommentService.checkCommentParent(comment));
            responseModel.setMessage("Update comment successful");
            responseModel.setResponseData(commentResponse);
            responseModel.setStatus("success");
            messagingTemplate.convertAndSend("/comment/updateComment/"+commentId, commentResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Update comment fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{commentId:.+}/deleteComment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> deleteComment(@PathVariable UUID commentId){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            Comment comment = ICommentService.findCommentToId(commentId);

            if(!comment.getUserComment().getUserId().equals(user.getUserId())){
                responseModel.setMessage("Don't update comment");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            ICommentService.deleteComment(comment);

            responseModel.setMessage("Delete comment successful");
            responseModel.setStatus("success");

            messagingTemplate.convertAndSend("/comment/deleteComment/"+commentId, commentId);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Delete comment fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
