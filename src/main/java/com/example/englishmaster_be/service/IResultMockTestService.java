package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.MockTest.ResultMockTestRequest;
import com.example.englishmaster_be.model.response.ResultMockTestResponse;
import com.example.englishmaster_be.entity.ResultMockTestEntity;

import java.util.List;
import java.util.UUID;

public interface IResultMockTestService {

    ResultMockTestEntity saveResultMockTest(ResultMockTestRequest resultMockTest);

    List<ResultMockTestEntity> getAllResultMockTests();

    List<ResultMockTestEntity> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId);

    void deleteResultMockTestById(UUID id);

    ResultMockTestEntity getResultMockTestById(UUID id);

}
