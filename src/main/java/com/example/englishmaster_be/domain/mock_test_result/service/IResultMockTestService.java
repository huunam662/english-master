package com.example.englishmaster_be.domain.mock_test_result.service;

import com.example.englishmaster_be.domain.mock_test_result.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;

import java.util.List;
import java.util.UUID;

public interface IResultMockTestService {

    MockTestResultEntity saveResultMockTest(ResultMockTestRequest resultMockTest);

    List<MockTestResultEntity> getAllResultMockTests();

    List<MockTestResultEntity> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId);

    void deleteResultMockTestById(UUID id);

    MockTestResultEntity getResultMockTestById(UUID id);



}
