package com.example.englishmaster_be.domain.mock_test.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.dto.response.FilterResponse;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestDetailResponse;
import com.example.englishmaster_be.domain.mock_test.service.IMockTestService;
import com.example.englishmaster_be.mapper.DetailMockTestMapper;
import com.example.englishmaster_be.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestFilterRequest;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.model.detail_mock_test.DetailMockTestEntity;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
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
    @DefaultMessage("Create mock test successfully")
    public MockTestResponse createMockTest(@RequestBody MockTestRequest saveMockTestRequest) {

        MockTestEntity mockTest = mockTestService.saveMockTest(saveMockTestRequest);

        return MockTestMapper.INSTANCE.toMockTestResponse(mockTest);
    }

    @GetMapping(value = "/getMockTestById")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Find mock test successfully")
    public MockTestResponse getMockTest(@RequestParam UUID id) {

        MockTestEntity mockTest = mockTestService.findMockTestById(id);

        return MockTestMapper.INSTANCE.toMockTestResponse(mockTest);
    }

    @GetMapping(value = "/listMockTest")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Get top 10 mock test successfully")
    public List<MockTestResponse> listTop10MockTest(@RequestParam int index) {

        List<MockTestEntity> listMockTest = mockTestService.getTop10MockTest(index);

        return MockTestMapper.INSTANCE.toMockTestResponseList(listMockTest);
    }

    @GetMapping(value = "/listMockTestAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("List mock test successfully")
    public FilterResponse<?> listMockTestOfAdmin(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection
    ) {

        MockTestFilterRequest mockTestFilterRequest = MockTestFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return mockTestService.getListMockTestOfAdmin(mockTestFilterRequest);
    }

    @GetMapping(value = "/{userId:.+}/listTestToUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Get top 10 mock test of UserEntity successfully")
    public List<MockTestResponse> listMockTestToUser(
            @RequestParam int index,
            @PathVariable UUID userId
    ) {

        List<MockTestEntity> mockTestEntityList = mockTestService.getListMockTestToUser(index, userId);

        return MockTestMapper.INSTANCE.toMockTestResponseList(mockTestEntityList);
    }

    @PostMapping(value = "/{mockTestId:.+}/submitResult")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Create detail mock test successfully")
    public List<MockTestDetailResponse> addAnswerToMockTest(@PathVariable UUID mockTestId, @RequestBody List<UUID> listAnswerId) {

        List<DetailMockTestEntity> detailMockTestEntityList = mockTestService.addAnswerToMockTest(mockTestId, listAnswerId);

        return DetailMockTestMapper.INSTANCE.toDetailMockTestResponseList(detailMockTestEntityList);
    }


    @GetMapping(value = "/{mockTestId:.+}/listCorrectAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<MockTestDetailResponse> listCorrectAnswer(@RequestParam int index, @RequestParam boolean isCorrect, @PathVariable UUID mockTestId) {

        List<DetailMockTestEntity> detailMockTestEntityList = mockTestService.getListCorrectAnswer(index, isCorrect, mockTestId);

        return DetailMockTestMapper.INSTANCE.toDetailMockTestResponseList(detailMockTestEntityList);
    }

    @GetMapping(value = "/{mockTestId:.+}/sendEmail")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Send email successfully")
    public void sendEmailToMock(@PathVariable UUID mockTestId) {

        mockTestService.sendEmailToMock(mockTestId);
    }

    @GetMapping(value = "/{mockTestId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show part to mock test successfully")
    public MockTestPartResponse getPartToMockTest(@PathVariable UUID mockTestId) {

        return mockTestService.getPartToMockTest(mockTestId);
    }

    @GetMapping(value = "/{mockTestId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show question of part to mock test successfully")
    public List<QuestionMockTestResponse> getQuestionOfToMockTest(@PathVariable UUID mockTestId, @RequestParam UUID partId) {

        return mockTestService.getQuestionOfToMockTest(mockTestId, partId);
    }

}
