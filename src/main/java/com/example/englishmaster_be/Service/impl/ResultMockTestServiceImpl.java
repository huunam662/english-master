package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.MockTest.CreateResultMockTestDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.ResultMockTestResponse;
import com.example.englishmaster_be.Repository.ResultMockTestRepository;
import com.example.englishmaster_be.Service.IMockTestService;
import com.example.englishmaster_be.Service.IPartService;
import com.example.englishmaster_be.Service.IResultMockTestService;
import com.example.englishmaster_be.Service.IUserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResultMockTestServiceImpl implements IResultMockTestService {
    @Autowired
    private IUserService IUserService;

    @Autowired
    private IPartService IPartService;

    @Autowired
    private IMockTestService IMockTestService;

    @Autowired
    private ResultMockTestRepository resultMockTestRepository;


    @Override
    public ResultMockTestResponse createResultMockTest(CreateResultMockTestDTO resultMockTest) {
        ResultMockTest mockTest = new ResultMockTest(resultMockTest);
        User user = IUserService.currentUser();

        mockTest.setPart(IPartService.getPartToId(resultMockTest.getPartId()));
        mockTest.setMockTest(IMockTestService.findMockTestToId(resultMockTest.getMockTestId()));

        mockTest.setUserCreate(user);
        mockTest.setUserUpdate(user);

        resultMockTestRepository.save(mockTest);

        return new ResultMockTestResponse(mockTest);
    }

    @Override
    public List<ResultMockTestResponse> getAllResultMockTests() {
        List<ResultMockTest> resultMockTests = resultMockTestRepository.findAll();
        List<ResultMockTestResponse> resultMockTestResponses = new ArrayList<>();
        for (ResultMockTest resultMockTest : resultMockTests) {
            resultMockTestResponses.add(new ResultMockTestResponse(resultMockTest));
        }
        return resultMockTestResponses;
    }

    @Override
    public List<ResultMockTestResponse> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId) {
        QResultMockTest qResultMockTest = QResultMockTest.resultMockTest;


        BooleanBuilder builder = new BooleanBuilder();

        if (partId != null) {
            Part part = IPartService.getPartToId(partId);
            builder.and(qResultMockTest.part.partId.eq(partId));
        }
        if (mockTestId != null) {
            MockTest mockTest = IMockTestService.findMockTestToId(mockTestId);
            builder.and(qResultMockTest.mockTest.mockTestId.eq(mockTestId));
        }

        Predicate predicate = builder.getValue();

        return ((List<ResultMockTest>) resultMockTestRepository.findAll(predicate))
                .stream()
                .map(ResultMockTestResponse::new)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteResultMockTestById(UUID id) {
        resultMockTestRepository.deleteById(id);
    }
}
