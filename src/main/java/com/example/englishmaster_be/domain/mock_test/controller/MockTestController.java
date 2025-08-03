package com.example.englishmaster_be.domain.mock_test.controller;

import com.example.englishmaster_be.domain.mock_test.dto.response.*;
import com.example.englishmaster_be.domain.mock_test.service.IMockTestService;
import com.example.englishmaster_be.domain.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Mock test")
@RestController
@RequestMapping("/mockTest")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestController {

    IMockTestService mockTestService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public MockTestKeyResponse createMockTest(@RequestBody MockTestRequest saveMockTestRequest) {

        return mockTestService.saveMockTest(saveMockTestRequest);
    }

    @GetMapping(value = "/getMockTestById")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public MockTestResponse getMockTest(@RequestParam("id") UUID id) {

        MockTestEntity mockTest = mockTestService.getMockTestById(id);

        return MockTestMapper.INSTANCE.toMockTestResponse(mockTest);
    }


    @GetMapping(value = "/listTestToUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<IMockTestToUserResponse> listMockTestToUser() {
        List<IMockTestToUserResponse> result = mockTestService.getListMockTestToUser();
        return result;
    }

    @PostMapping(value = "/{mockTestId:.+}/submitResult")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public MockTestKeyResponse addAnswerToMockTest(@PathVariable("mockTestId") UUID mockTestId, @RequestBody List<UUID> listAnswerId) {

        return mockTestService.addAnswerToMockTest(mockTestId, listAnswerId);
    }


    @GetMapping(value = "/{mockTestId:.+}/sendEmail")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void sendEmailToMock(@PathVariable("mockTestId") UUID mockTestId) {

        mockTestService.sendEmailToMock(mockTestId);
    }

    @GetMapping(value = "/{mockTestId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<QuestionMockTestResponse> getQuestionOfToMockTest(@PathVariable("mockTestId") UUID mockTestId, @RequestParam("partId") UUID partId) {

        return mockTestService.getQuestionOfToMockTest(mockTestId, partId);
    }

    @GetMapping(value = "/{mockTestId:.+}/mocketinfor")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public MockTestInforResponse getMockTestInfor(@PathVariable("mockTestId") UUID mockTestId) {

        MockTestEntity mockTestEntity = mockTestService.getInformationMockTest(mockTestId);

        return MockTestMapper.INSTANCE.toMockTestInforResponse(mockTestEntity);
    }
}
