package com.example.englishmaster_be.domain.mock_test.mock_test.service;

import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestInforRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestKeyRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.req.MockTestReq;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestToUserView;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;

import java.util.List;
import java.util.UUID;

public interface IMockTestService {

    MockTestKeyRes saveMockTest(MockTestReq mockTestRequest);

    MockTestEntity getMockTestById(UUID mockTestId);

    List<MockTestEntity> getAllMockTestByYearMonthAndDay(TopicEntity topic, String year, String month, String day);

    List<MockTestEntity> getAllMockTestToTopic(TopicEntity topic);

    List<IMockTestToUserView> getListMockTestToUser();

    MockTestKeyRes addAnswerToMockTest(UUID mockTestId, List<UUID> listAnswerId);

    void sendEmailToMock(UUID mockTestId);

    MockTestInforRes getInformationMockTest(UUID mockTestId);

}
