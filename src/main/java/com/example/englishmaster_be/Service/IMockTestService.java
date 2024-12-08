package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.DTO.MockTest.MockTestFilterRequest;
import com.example.englishmaster_be.DTO.MockTest.SaveMockTestDTO;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface IMockTestService {

    MockTestResponse saveMockTest(SaveMockTestDTO saveMockTestDTO);

    List<MockTest> getTop10MockTest(int index);

    List<MockTest> getTop10MockTestToUser(int index, User user);

    MockTest findMockTestToId(UUID mockTestId);

    MockTest findMockTestById(UUID mockTestId);

    List<DetailMockTest> getTop10DetailToCorrect(int index, boolean isCorrect ,MockTest mockTest);

    int countCorrectAnswer(UUID mockTestId);

    List<MockTest> getAllMockTestByYearMonthAndDay(Topic topic, String year, String month, String day);

    List<MockTest> getAllMockTestToTopic(Topic topic);

    FilterResponse<?> getListMockTestOfAdmin(MockTestFilterRequest filterRequest);

    List<MockTestResponse> getListMockTestToUser(int index, UUID userId);

    List<DetailMockTestResponse> addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId);

    List<DetailMockTestResponse> getListCorrectAnswer(int index, boolean isCorrect, UUID mockTestId);

    void sendEmailToMock(@PathVariable UUID mockTestId);

    PartMockTestResponse getPartToMockTest(UUID mockTestId);

    List<QuestionMockTestResponse> getQuestionOfToMockTest(UUID mockTestId, UUID partId);

}
