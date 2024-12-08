package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.MockTest.SaveResultMockTestDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.Response.ResultMockTestResponse;
import com.example.englishmaster_be.Repository.ResultMockTestRepository;
import com.example.englishmaster_be.Service.IMockTestService;
import com.example.englishmaster_be.Service.IPartService;
import com.example.englishmaster_be.Service.IResultMockTestService;
import com.example.englishmaster_be.Service.IUserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultMockTestServiceImpl implements IResultMockTestService {


    ResultMockTestRepository resultMockTestRepository;

    IUserService userService;

    IPartService partService;

    IMockTestService mockTestService;


    @Transactional
    @Override
    public ResultMockTestResponse createResultMockTest(SaveResultMockTestDTO resultMockTest) {

        try{

            ResultMockTest mockTest = new ResultMockTest(resultMockTest);

            User user = userService.currentUser();

            mockTest.setPart(partService.getPartToId(resultMockTest.getPartId()));
            mockTest.setMockTest(mockTestService.findMockTestToId(resultMockTest.getMockTestId()));

            mockTest.setUserCreate(user);
            mockTest.setUserUpdate(user);

            mockTest = resultMockTestRepository.save(mockTest);

            return new ResultMockTestResponse(mockTest);
        }
        catch (Exception e){
            throw new RuntimeException("Create result mock test failed");
        }

    }

    @Override
    public List<ResultMockTestResponse> getAllResultMockTests() {
        try {

            List<ResultMockTest> resultMockTests = resultMockTestRepository.findAll();

            return resultMockTests.stream().map(
                    resultMockTest -> new ResultMockTestResponse(resultMockTest)
            ).toList();
        }
        catch (Exception e){
            throw new RuntimeException("Get list result mock tests failed");
        }
    }

    @Override
    public List<ResultMockTestResponse> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId) {

        try {

            QResultMockTest qResultMockTest = QResultMockTest.resultMockTest;

            BooleanBuilder builder = new BooleanBuilder();

            if (partId != null)
                builder.and(qResultMockTest.part.partId.eq(partId));

            if (mockTestId != null)
                builder.and(qResultMockTest.mockTest.mockTestId.eq(mockTestId));

            Predicate predicate = builder.getValue();

            return ((List<ResultMockTest>) resultMockTestRepository.findAll(predicate))
                    .stream()
                    .map(ResultMockTestResponse::new)
                    .collect(Collectors.toList());
        }
        catch (Exception e){
            throw new RuntimeException("Get result mock test failed");
        }

    }

    @Override
    public ResultMockTest getResultMockTestById(UUID id) {
        return resultMockTestRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Result mock test not found")
        );
    }

    @Override
    public void deleteResultMockTestById(UUID id) {

        ResultMockTest resultMockTest = getResultMockTestById(id);

        resultMockTestRepository.delete(resultMockTest);
    }
}
