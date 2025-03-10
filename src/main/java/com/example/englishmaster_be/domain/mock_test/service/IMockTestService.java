package com.example.englishmaster_be.domain.mock_test.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestFilterRequest;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailEntity;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface IMockTestService {

    MockTestEntity saveMockTest(MockTestRequest mockTestRequest);

    List<MockTestEntity> getTop10MockTest(int index);

    List<MockTestEntity> getTop10MockTestToUser(int index, UserEntity user);

    MockTestEntity findMockTestToId(UUID mockTestId);

    MockTestEntity findMockTestById(UUID mockTestId);

    List<MockTestDetailEntity> getTop10DetailToCorrect(int index, boolean isCorrect , MockTestEntity mockTest);

    int countCorrectAnswer(UUID mockTestId);

    List<MockTestEntity> getAllMockTestByYearMonthAndDay(TopicEntity topic, String year, String month, String day);

    List<MockTestEntity> getAllMockTestToTopic(TopicEntity topic);

    FilterResponse<?> getListMockTestOfAdmin(MockTestFilterRequest filterRequest);

    List<MockTestEntity> getListMockTestToUser(int index, UUID userId);

    List<MockTestDetailEntity> addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId);

    List<MockTestDetailEntity> getListCorrectAnswer(int index, boolean isCorrect, UUID mockTestId);

    void sendEmailToMock(@PathVariable UUID mockTestId);

    MockTestPartResponse getPartToMockTest(UUID mockTestId);

    List<QuestionMockTestResponse> getQuestionOfToMockTest(UUID mockTestId, UUID partId);

    MockTestEntity getInformationMockTest(UUID mockTestId);

}
