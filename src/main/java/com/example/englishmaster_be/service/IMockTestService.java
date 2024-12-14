package com.example.englishmaster_be.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.model.request.MockTest.MockTestFilterRequest;
import com.example.englishmaster_be.model.request.MockTest.MockTestRequest;
import com.example.englishmaster_be.model.response.PartMockTestResponse;
import com.example.englishmaster_be.model.response.QuestionMockTestResponse;
import com.example.englishmaster_be.entity.DetailMockTestEntity;
import com.example.englishmaster_be.entity.MockTestEntity;
import com.example.englishmaster_be.entity.TopicEntity;
import com.example.englishmaster_be.entity.UserEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface IMockTestService {

    MockTestEntity saveMockTest(MockTestRequest mockTestRequest);

    List<MockTestEntity> getTop10MockTest(int index);

    List<MockTestEntity> getTop10MockTestToUser(int index, UserEntity user);

    MockTestEntity findMockTestToId(UUID mockTestId);

    MockTestEntity findMockTestById(UUID mockTestId);

    List<DetailMockTestEntity> getTop10DetailToCorrect(int index, boolean isCorrect , MockTestEntity mockTest);

    int countCorrectAnswer(UUID mockTestId);

    List<MockTestEntity> getAllMockTestByYearMonthAndDay(TopicEntity topic, String year, String month, String day);

    List<MockTestEntity> getAllMockTestToTopic(TopicEntity topic);

    FilterResponse<?> getListMockTestOfAdmin(MockTestFilterRequest filterRequest);

    List<MockTestEntity> getListMockTestToUser(int index, UUID userId);

    List<DetailMockTestEntity> addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId);

    List<DetailMockTestEntity> getListCorrectAnswer(int index, boolean isCorrect, UUID mockTestId);

    void sendEmailToMock(@PathVariable UUID mockTestId);

    PartMockTestResponse getPartToMockTest(UUID mockTestId);

    List<QuestionMockTestResponse> getQuestionOfToMockTest(UUID mockTestId, UUID partId);

}
