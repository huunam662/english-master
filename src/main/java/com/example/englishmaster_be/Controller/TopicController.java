package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Topic.*;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.TopicResponse;
import com.example.englishmaster_be.Repository.TopicRepository;
import com.example.englishmaster_be.Service.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private IFileStorageService IFileStorageService;
    @Autowired
    private ITopicService ITopicService;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private IQuestionService IQuestionService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createTopic(@ModelAttribute CreateTopicDTO createtopicDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            String filename = IFileStorageService.nameFile(createtopicDTO.getTopicImage());

            Topic topic = new Topic(createtopicDTO.getTopicName(), filename, createtopicDTO.getTopicDiscription(), createtopicDTO.getTopicType(),
                    createtopicDTO.getWorkTime(), createtopicDTO.getStartTime(), createtopicDTO.getEndTime(), createtopicDTO.isEnable());


            topic.setUserCreate(user);
            topic.setUserUpdate(user);

            ITopicService.createTopic(topic);
            IFileStorageService.save(createtopicDTO.getTopicImage(), filename);

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

    @PutMapping(value = "/{topicId:.+}/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> updateTopic(@PathVariable UUID topicId, @RequestBody UpdateTopicDTO updateTopicDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);

            ITopicService.updateTopic(topic, updateTopicDTO);

            TopicResponse topicResponse = new TopicResponse(topic);

            responseModel.setMessage("Update topic successfully");
            responseModel.setResponseData(topicResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Update topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PutMapping(value = "/{topicId:.+}/uploadImage", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> uploadFileImage(@PathVariable UUID topicId, @ModelAttribute UploadFileDTO uploadFileDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            Topic topic = ITopicService.findTopicById(topicId);

            String filename = IFileStorageService.nameFile(uploadFileDTO.getContentData());
            IFileStorageService.delete(topic.getTopicImage());

            topic.setTopicImage(filename);
            topic.setUserUpdate(user);
            topic.setUpdateAt(LocalDateTime.now());

            TopicResponse topicResponse = new TopicResponse(topic);

            responseModel.setMessage("Update topic successfully");
            responseModel.setResponseData(topicResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Update topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> deleteTopic(@PathVariable UUID topicId){
        ResponseModel responseModel = new ResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);

            ITopicService.deleteTopic(topic);
            IFileStorageService.delete(topic.getTopicImage());

            responseModel.setMessage("Delete topic successfully");
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Delete topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listTopic")
    public ResponseEntity<ResponseModel> getTop6Topic(@RequestParam int indexp) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Topic> topicList = ITopicService.getTop6Topic(indexp);

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

    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> addPartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId){
        ResponseModel responseModel = new ResponseModel();
        try{
            ITopicService.addPartToTopic(topicId, partId);

            responseModel.setMessage("Add part to topic successful");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Add part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> deletePartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId){
        ResponseModel responseModel = new ResponseModel();
        try{
            boolean check = ITopicService.deletePartToTopic(topicId, partId);

            if(check){
                responseModel.setMessage("Delete part to topic successful");
                responseModel.setStatus("success");
            }else{
                responseModel.setMessage("Delete part to topic fail: Topic don't have part");
                responseModel.setStatus("fail");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);

        }catch (Exception e) {
            responseModel.setMessage("Delete part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addQuestion")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> addQuestionToTopic(@PathVariable UUID topicId, @RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Question question = IQuestionService.findQuestionById(questionId);
            Topic topic = ITopicService.findTopicById(topicId);
            Part part = question.getPart();

            User user = IUserService.currentUser();


            if(ITopicService.existPartInTopic(topic, part)){
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());
                ITopicService.addQuestionToTopic(topic, question);
                responseModel.setMessage("Add question to topic successfully");
            }else{
                responseModel.setMessage("Part of question don't have in topic");
            }

            JSONObject response = new JSONObject();
            response.put("Topic Id", topicId);
            response.put("Question Id", questionId);


            responseModel.setResponseData(response);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Add question to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> deleteQuestionToTopic(@PathVariable  UUID topicId, @RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            Topic topic = ITopicService.findTopicById(topicId);

            User user = IUserService.currentUser();

            if(ITopicService.existQuestionInTopic(topic, question)){
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());

                ITopicService.deleteQuestionToTopic(topic, question);
                responseModel.setMessage("Delete question to topic successfully");
            }else {
                responseModel.setMessage("Question don't have in topic");
            }

            JSONObject response = new JSONObject();
            response.put("Topic Id", topicId);
            response.put("Question Id", questionId);

            responseModel.setResponseData(response);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Delete question to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getPartToTopic(@PathVariable UUID topicId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Part> partList = ITopicService.getPartToTopic(topicId);

            JSONArray responseArray = new JSONArray();

            for(Part part : partList ){
                JSONObject response = new JSONObject();
                response.put("partId", part.getPartId());
                response.put("partName", part.getPartName());
                response.put("partType", part.getPartType());
                responseArray.add(response);
            }

            responseModel.setMessage("Show part to topic successfully");

            responseModel.setResponseData(responseArray);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getQuestionOfToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Question> questionList = ITopicService.getQuestionOfPartToTopic(topicId, partId);
            JSONArray responseArray = new JSONArray();

            for(Question question : questionList ){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("questionId", question.getQuestionId());
                responseArray.add(jsonObject);
            }

            responseModel.setMessage("Show question of part to topic successfully");
            responseModel.setResponseData(responseArray);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show question of part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
