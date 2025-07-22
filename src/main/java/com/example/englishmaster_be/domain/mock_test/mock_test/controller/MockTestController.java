package com.example.englishmaster_be.domain.mock_test.mock_test.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestInforRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestKeyRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestPageRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestPageView;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestToUserView;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.req.MockTestReq;
import com.example.englishmaster_be.domain.mock_test.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.service.IMockTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Tag(name = "Mock test")
@RestController
@RequestMapping("/mockTest")
public class MockTestController {

    private final IMockTestService mockTestService;

    public MockTestController(IMockTestService mockTestService) {
        this.mockTestService = mockTestService;
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Create mock test from topic exam.",
            description = "Create mock test from topic exam."
    )
    public MockTestKeyRes createMockTest(@RequestBody MockTestReq saveMockTestRequest) {
        return mockTestService.saveMockTest(saveMockTestRequest);
    }

    @GetMapping(value = "/getMockTestById")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public MockTestRes getMockTest(@RequestParam("id") UUID id) {
        MockTestEntity mockTest = mockTestService.getMockTestById(id);
        return MockTestMapper.INSTANCE.toMockTestResponse(mockTest);
    }


    @GetMapping(value = "/listTestToUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<IMockTestToUserView> listMockTestToUser() {
        return mockTestService.getListMockTestToUser();
    }

    @PostMapping(value = "/{mockTestId:.+}/submitResult")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Submit mock test answers for type reading or listening or reading & listening.",
            description = "Submit mock test answers for type reading or listening or reading & listening."
    )
    public MockTestKeyRes addAnswerToMockTest(@PathVariable("mockTestId") UUID mockTestId, @RequestBody List<UUID> listAnswerId) {
        return mockTestService.addAnswerToMockTest(mockTestId, listAnswerId);
    }


    @GetMapping(value = "/{mockTestId:.+}/sendEmail")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void sendEmailToMock(@PathVariable("mockTestId") UUID mockTestId) {
        mockTestService.sendEmailToMock(mockTestId);
    }

    @GetMapping(value = "/{mockTestId:.+}/mocketinfor")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get mock test information.",
            description = "Get mock test information."
    )
    public MockTestInforRes getMockTestInfor(@PathVariable("mockTestId") UUID mockTestId) {
        return mockTestService.getInformationMockTest(mockTestId);
    }

    @GetMapping("/page")
    @Operation(
            summary = "Get mock test page.",
            description = "Get mock test page."
    )
    public PageInfoRes<MockTestPageRes> getMockTestPage(@ModelAttribute @Valid PageOptionsReq optionsReq) {
        Page<IMockTestPageView> mockTestPageViews = mockTestService.getMockTestPage(optionsReq);
        List<MockTestPageRes> mockTestPageResList = MockTestMapper.INSTANCE.toMockTestPageResList(mockTestPageViews.getContent());
        Page<MockTestPageRes> pageRes = new PageImpl<>(mockTestPageResList, mockTestPageViews.getPageable(), mockTestPageViews.getTotalElements());
        return new PageInfoRes<>(pageRes);
    }
}
