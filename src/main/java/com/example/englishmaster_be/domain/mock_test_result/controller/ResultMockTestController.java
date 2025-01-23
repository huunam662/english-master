package com.example.englishmaster_be.domain.mock_test_result.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.mock_test_result.dto.response.MockTestResultResponse;
import com.example.englishmaster_be.mapper.MockTestResultMapper;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import com.example.englishmaster_be.domain.mock_test_result.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.domain.mock_test_result.service.IResultMockTestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Result Mock Test")
@RestController
@RequestMapping("/result-mock-test")
@PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultMockTestController {

    IResultMockTestService resultMockTestService;

    @PostMapping(value = "/create")
    @DefaultMessage("Create result mock test successfully")
    public MockTestResultResponse createResultMockTest(ResultMockTestRequest resultMockTestRequest) {

        MockTestResultEntity resultMockTest = resultMockTestService.saveResultMockTest(resultMockTestRequest);

        return MockTestResultMapper.INSTANCE.toMockTestResultResponse(resultMockTest);
    }

    @GetMapping("/getAllResult")
    @DefaultMessage("Get all result mock test successfully")
    public List<MockTestResultResponse> getAllResult() {

        List<MockTestResultEntity> resultMockTestEntityList = resultMockTestService.getAllResultMockTests();

        return MockTestResultMapper.INSTANCE.toMockTestResultResponseList(resultMockTestEntityList);
    }

    @GetMapping("/getResultMockTestByPartAndMockTest")
    @DefaultMessage("Get result mock test successfully")
    public List<MockTestResultResponse> getResultMockTest(
            @RequestParam(required = false) UUID partId,
            @RequestParam(required = false) UUID mockTestId
    ) {

        List<MockTestResultEntity> resultMockTestEntityList = resultMockTestService.getResultMockTestsByPartIdAndMockTestId(partId, mockTestId);

        return MockTestResultMapper.INSTANCE.toMockTestResultResponseList(resultMockTestEntityList);
    }

    @DeleteMapping("/delete")
    @DefaultMessage("Delete result mock test successfully")
    public void deleteResultMockTest(@RequestParam UUID uuid) {

        resultMockTestService.deleteResultMockTestById(uuid);
    }
}
