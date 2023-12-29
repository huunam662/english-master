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
    private ICommentService ICommentService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @PostMapping(value = "/{topicId:.+}/addComment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createComment(@PathVariable UUID topicId, @RequestBody CreateCommentDTO createCommentDTO){
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
            messagingTemplate.convertAndSend("/comment/post/"+topicId, commentResponse);

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




}
