package com.example.englishmaster_be.service;

import com.example.englishmaster_be.dto.mockTest.CreateResultMockTestDTO;
import com.example.englishmaster_be.model.response.ResultMockTestResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface IResultMockTestService {
    ResultMockTestResponse createResultMockTest(CreateResultMockTestDTO resultMockTest);

    List<ResultMockTestResponse> getAllResultMockTests();

    List<ResultMockTestResponse> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId);

    void deleteResultMockTestById(UUID id);



}
