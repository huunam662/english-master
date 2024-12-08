package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.MockTest.MockTestFilterRequest;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.MockTest.SaveMockTestDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.MockTestRepository;
import com.example.englishmaster_be.Repository.ResultMockTestRepository;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Mock test")
@RestController
@RequestMapping("/api/mockTest")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestController {

    IMockTestService mockTestService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create mock test successfully")
    public MockTestResponse createMockTest(@RequestBody SaveMockTestDTO saveMockTestDTO) {

        return mockTestService.saveMockTest(saveMockTestDTO);
    }

    @GetMapping(value = "/getMockTestById")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Find MockTest successfully")
    public MockTestResponse getMockTest(@RequestParam UUID id) {

        MockTest mockTest = mockTestService.findMockTestById(id);

        return new MockTestResponse(mockTest);
    }

    @GetMapping(value = "/listMockTest")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Get top 10 mock test successfully")
    public List<MockTestResponse> listTop10MockTest(@RequestParam int index) {

        List<MockTest> listMockTest = mockTestService.getTop10MockTest(index);

        return listMockTest.stream().map(MockTestResponse::new).toList();
    }

    @GetMapping(value = "/listMockTestAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("List mock test successfully")
    public FilterResponse<?> listMockTestOfAdmin(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
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
    @MessageResponse("Get top 10 mock test of User successfully")
    public List<MockTestResponse> listMockTestToUser(
            @RequestParam int index,
            @PathVariable UUID userId
    ) {

        return mockTestService.getListMockTestToUser(index, userId);
    }

    @PostMapping(value = "/{mockTestId:.+}/submitResult")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create detail mock test successfully")
    public List<DetailMockTestResponse> addAnswerToMockTest(@PathVariable UUID mockTestId, @RequestBody List<UUID> listAnswerId) {

        return mockTestService.addAnswerToMockTest(mockTestId, listAnswerId);
    }


    @GetMapping(value = "/{mockTestId:.+}/listCorrectAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<DetailMockTestResponse> listCorrectAnswer(@RequestParam int index, @RequestParam boolean isCorrect, @PathVariable UUID mockTestId) {

        return mockTestService.getListCorrectAnswer(index, isCorrect, mockTestId);
    }

    @GetMapping(value = "/{mockTestId:.+}/sendEmail")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Send email successfully")
    public void sendEmailToMock(@PathVariable UUID mockTestId) {

        mockTestService.sendEmailToMock(mockTestId);
    }

    @GetMapping(value = "/{mockTestId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show Part to mock test successfully")
    public PartMockTestResponse getPartToMockTest(@PathVariable UUID mockTestId) {

        return mockTestService.getPartToMockTest(mockTestId);
    }

    @GetMapping(value = "/{mockTestId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show Question of Part to mock test successfully")
    public List<QuestionMockTestResponse> getQuestionOfToMockTest(@PathVariable UUID mockTestId, @RequestParam UUID partId) {

        return mockTestService.getQuestionOfToMockTest(mockTestId, partId);
    }

}
