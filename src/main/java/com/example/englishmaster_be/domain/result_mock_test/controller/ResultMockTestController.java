package com.example.englishmaster_be.domain.result_mock_test.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity;
import com.example.englishmaster_be.mapper.ResultMockTestMapper;
import com.example.englishmaster_be.domain.result_mock_test.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.domain.result_mock_test.dto.response.ResultMockTestResponse;
import com.example.englishmaster_be.domain.result_mock_test.service.IResultMockTestService;
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
    public ResultMockTestResponse createResultMockTest(ResultMockTestRequest resultMockTestRequest) {

        ResultMockTestEntity resultMockTest = resultMockTestService.saveResultMockTest(resultMockTestRequest);

        return ResultMockTestMapper.INSTANCE.toResultMockTestResponse(resultMockTest);
    }

    @GetMapping("/getAllResult")
    @DefaultMessage("Get all result mock test successfully")
    public List<ResultMockTestResponse> getAllResult() {

        List<ResultMockTestEntity> resultMockTestEntityList = resultMockTestService.getAllResultMockTests();

        return ResultMockTestMapper.INSTANCE.toResultMockTestResponseList(resultMockTestEntityList);
    }

    @GetMapping("/getResultMockTestByPartAndMockTest")
    @DefaultMessage("Get result mock test successfully")
    public List<ResultMockTestResponse> getResultMockTest(
            @RequestParam(required = false) UUID partId,
            @RequestParam(required = false) UUID mockTestId
    ) {

        List<ResultMockTestEntity> resultMockTestEntityList = resultMockTestService.getResultMockTestsByPartIdAndMockTestId(partId, mockTestId);

        return ResultMockTestMapper.INSTANCE.toResultMockTestResponseList(resultMockTestEntityList);
    }

    @DeleteMapping("/delete")
    @DefaultMessage("Delete result mock test successfully")
    public void deleteResultMockTest(@RequestParam UUID uuid) {

        resultMockTestService.deleteResultMockTestById(uuid);
    }
}
