package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.MockTest.SaveResultMockTestDTO;
import com.example.englishmaster_be.Model.Response.ResultMockTestResponse;
import com.example.englishmaster_be.Model.ResultMockTest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface IResultMockTestService {

    ResultMockTestResponse createResultMockTest(SaveResultMockTestDTO resultMockTest);

    List<ResultMockTestResponse> getAllResultMockTests();

    List<ResultMockTestResponse> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId);

    void deleteResultMockTestById(UUID id);

    ResultMockTest getResultMockTestById(UUID id);

}
