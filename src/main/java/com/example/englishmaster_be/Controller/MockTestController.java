package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.MockTest.CreateMockTestDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mockTest")
public class MockTestController {
    @Autowired
    private IUserService IUserService;
    @Autowired
    private ITopicService ITopicService;
    @Autowired
    private IMockTestService IMockTestService;
    @Autowired
    private IAnswerService IAnswerService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createMockTest(@RequestBody CreateMockTestDTO createMockTestDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            MockTest mockTest = new MockTest(createMockTestDTO);

            mockTest.setTopic(ITopicService.findTopicById(createMockTestDTO.getTopic_id()));
            mockTest.setUser(user);
            mockTest.setUserCreate(user);
            mockTest.setUserUpdate(user);

            IMockTestService.createMockTest(mockTest);

            MockTestResponse mockTestResponse = new MockTestResponse(mockTest);

            responseModel.setMessage("Create mock test successfully");

            responseModel.setResponseData(mockTestResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listMockTest")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> listTop10MockTest(@RequestParam int index){
        ResponseModel responseModel = new ResponseModel();
        try {
            List<MockTest> listMockTest = IMockTestService.getTop10MockTest(index);

            List<MockTestResponse> mockTestResponseList = new ArrayList<>();
            for(MockTest mockTest : listMockTest){
                MockTestResponse mockTestResponse = new MockTestResponse(mockTest);
                mockTestResponseList.add(mockTestResponse);
            }


            responseModel.setMessage("Get top 10 mock test successfully");

            responseModel.setResponseData(mockTestResponseList);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Get top 10 mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listTestToUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> listMockTestToUser(@RequestParam int index, @RequestParam UUID userId){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.findUserById(userId);

            List<MockTest> listMockTest = IMockTestService.getTop10MockTestToUser(index, user);

            List<MockTestResponse> mockTestResponseList = new ArrayList<>();
            for(MockTest mockTest : listMockTest){
                MockTestResponse mockTestResponse = new MockTestResponse(mockTest);
                mockTestResponseList.add(mockTestResponse);
            }


            responseModel.setMessage("Get top 10 mock test of user successfully");
            responseModel.setResponseData(mockTestResponseList);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Get top 10 mock test of user fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/addAnswerToMockTest")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> addAnswerToMockTest(@RequestParam UUID mockTestId, UUID answerId  ){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);
            Answer answer = IAnswerService.findAnswerToId(answerId);

            DetailMockTest detailMockTest = new DetailMockTest(mockTest, answer);
            detailMockTest.setUserCreate(user);
            detailMockTest.setUserUpdate(user);

            IMockTestService.createDetailMockTest(detailMockTest);

            DetailMockTestResponse detailMockTestResponse = new DetailMockTestResponse(detailMockTest);

            responseModel.setMessage("Create detail mock test successfully");
            responseModel.setResponseData(detailMockTestResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create detail mock test fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listCorrectAnswer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> listCorrectAnswer(@RequestParam int index, boolean isCorrect ,UUID mockTestId){
        ResponseModel responseModel = new ResponseModel();
        try {
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);

            List<DetailMockTest> detailMockTestList = IMockTestService.getTop10DetailToCorrect(index, isCorrect, mockTest);

            List<DetailMockTestResponse> detailMockTestResponseList = new ArrayList<>();
            for(DetailMockTest detailMockTest : detailMockTestList){
                DetailMockTestResponse detailMockTestResponse = new DetailMockTestResponse(detailMockTest);
                detailMockTestResponseList.add(detailMockTestResponse);
            }

            if(isCorrect){
                responseModel.setMessage("Get top 10 answer correct successfully");
            }else {
                responseModel.setMessage("Get top 10 answer wrong successfully");
            }

            responseModel.setResponseData(detailMockTestResponseList);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Get top 10 mock test of user fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
