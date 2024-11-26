package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.model.response.*;
import com.example.englishmaster_be.service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@SuppressWarnings("unchecked")
public class AdminController {

    @Autowired
    private IUserService IUserService;

    @Autowired
    private ITopicService ITopicService;
    @Autowired
    private IMockTestService IMockTestService;
    @Autowired
    private IPackService IPackService;

    @Autowired
    private JPAQueryFactory queryFactory;

    @GetMapping(value = "/getAllUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                    @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
                                                    @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                    @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
                                                    @RequestParam(value = "enable", required = false) String isEnable) {
        ResponseModel responseModel = new ResponseModel();
        try {

            JSONObject responseObject = new JSONObject();

            OrderSpecifier<?> orderSpecifier;


            if (Sort.Direction.DESC.equals(sortDirection)) {
                orderSpecifier = QUser.user.updateAt.desc();
            } else {
                orderSpecifier = QUser.user.updateAt.asc();
            }

            JPAQuery<User> query = queryFactory.selectFrom(QUser.user)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);

            query.where(QUser.user.role.roleName.notEqualsIgnoreCase("ROLE_ADMIN"));

            if (isEnable != null) {
                query.where(QUser.user.isEnabled.eq(isEnable.equalsIgnoreCase("enable")));
            }


            long totalRecords = query.fetchCount();
            long totalPages = (long) Math.ceil((double) totalRecords / size);
            responseObject.put("totalRecords", totalRecords);
            responseObject.put("totalPages", totalPages);

            List<User> userList = query.fetch();

            List<UserResponse> userResponseList = new ArrayList<>();

            for (User userItem : userList) {
                UserResponse userResponse = new UserResponse(userItem);
                userResponseList.add(userResponse);
            }

            responseObject.put("listUser", userResponseList);

            responseModel.setMessage("List user successful");
            responseModel.setResponseData(responseObject);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();

            errorResponseModel.setMessage("List user fail: " + e.getMessage());
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

    @PatchMapping(value = "/{userId:.+}/enableUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> enableUser(@PathVariable UUID userId, @RequestParam boolean enable) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.findUserById(userId);

            user.setEnabled(enable);
            IUserService.save(user);

            if (enable) {
                responseModel.setMessage("Enable account of user successful");
            } else {
                responseModel.setMessage("Disable account of user successful");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();

            if (enable) {
                errorResponseModel.setMessage("Enable account of user fail" + e.getMessage());
            } else {
                errorResponseModel.setMessage("Disable account of user fail" + e.getMessage());
            }
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

    @DeleteMapping(value = "/{userId:.+}/deleteUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteUser(@PathVariable UUID userId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.findUserById(userId);

            IUserService.delete(user);

            responseModel.setMessage("Delete account of user successful");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();

            errorResponseModel.setMessage("Delete account of user fail" + e.getMessage());

            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }

    @GetMapping(value = "/countMockTestOfTopic")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<ResponseModel> countMockTestOfTopic(@RequestParam(value = "date", required = false) String date,
                                                              @RequestParam(value = "pack") UUID packId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Pack pack = IPackService.findPackById(packId);

            List<JSONObject> responseArray = new ArrayList<>();

            List<Topic> listTopic = ITopicService.getAllTopicToPack(pack);
            for (Topic topic : listTopic) {
                List<MockTest> mockTests;
                if (date != null) {
                    String[] str = date.split("-");
                    String day = null, year, month = null;
                    year = str[0];

                    if (str.length > 1) {
                        month = str[1];
                        if (str.length > 2) {
                            day = str[2];
                        }
                    }

                    mockTests = IMockTestService.getAllMockTestByYearMonthAndDay(topic, year, month, day);
                } else {
                    mockTests = IMockTestService.getAllMockTestToTopic(topic);
                }

                JSONObject response = new JSONObject();
                response.put("topicName", topic.getTopicName());
                response.put("countMockTest", mockTests.size());
                responseArray.add(response);
            }


            responseModel.setMessage("List topic and count mock test successful");
            responseModel.setResponseData(responseArray);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
            errorResponseModel.setMessage("List topic and count mock test fail: " + e.getMessage());
            errorResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseModel);
        }
    }
}
