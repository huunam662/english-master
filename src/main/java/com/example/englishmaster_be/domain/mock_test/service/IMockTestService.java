package com.example.englishmaster_be.domain.mock_test.service;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestFilterRequest;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.IMockTestToUserResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMockTestResponse;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestDetailEntity;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;

import java.util.List;
import java.util.UUID;

public interface IMockTestService {

    MockTestKeyResponse saveMockTest(MockTestRequest mockTestRequest);

    MockTestEntity findMockTestToId(UUID mockTestId);

    MockTestEntity getMockTestById(UUID mockTestId);

    List<MockTestEntity> getAllMockTestByYearMonthAndDay(TopicEntity topic, String year, String month, String day);

    List<MockTestEntity> getAllMockTestToTopic(TopicEntity topic);

    List<IMockTestToUserResponse> getListMockTestToUser();

    MockTestKeyResponse addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId);

    void sendEmailToMock(UUID mockTestId);

    List<QuestionMockTestResponse> getQuestionOfToMockTest(UUID mockTestId, UUID partId);

    MockTestEntity getInformationMockTest(UUID mockTestId);

}
