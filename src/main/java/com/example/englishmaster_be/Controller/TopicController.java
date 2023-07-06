package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Topic.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.TopicResponse;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createTopic(@ModelAttribute CreateTopicDTO createtopicDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String filename = fileStorageService.save(createtopicDTO.getTopicImage());

            Topic topic = new Topic(createtopicDTO.getTopicName(), filename, createtopicDTO.getTopicDiscription(), createtopicDTO.getTopicType(),
                    createtopicDTO.getWorkTime(), createtopicDTO.getStartTime(), createtopicDTO.getEndTime(), createtopicDTO.isEnable());

            User user = userService.findUser(userDetails);

            topic.setUserCreate(user);
            topic.setUserUpdate(user);

            topicService.createTopic(topic);

            TopicResponse topicResponse = new TopicResponse(topic);

            responseModel.setMessage("Create topic successfully");

            responseModel.setResponseData(topicResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/listTopic")
    public ResponseEntity<ResponseModel> getTop6Topic(@RequestParam int indexp) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Topic> topicList = topicService.getTop6Topic(indexp);

            List<TopicResponse> topicResponseList = new ArrayList<>();

            for(Topic topic : topicList ){
                TopicResponse topicResponse = new TopicResponse(topic);
                topicResponseList.add(topicResponse);
            }

            responseModel.setMessage("Show 6 topic successfully");

            responseModel.setResponseData(topicResponseList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show 6 topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
