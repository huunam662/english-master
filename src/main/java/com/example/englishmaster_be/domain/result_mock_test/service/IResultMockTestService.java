package com.example.englishmaster_be.domain.result_mock_test.service;

import com.example.englishmaster_be.domain.result_mock_test.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity;

import java.util.List;
import java.util.UUID;

public interface IResultMockTestService {

    ResultMockTestEntity saveResultMockTest(ResultMockTestRequest resultMockTest);

    List<ResultMockTestEntity> getAllResultMockTests();

    List<ResultMockTestEntity> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId);

    void deleteResultMockTestById(UUID id);

    ResultMockTestEntity getResultMockTestById(UUID id);

}
