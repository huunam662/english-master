package com.example.englishmaster_be.service;

import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.model.response.MockTestResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface IMockTestService {
    void createMockTest(MockTest mockTest);

    void createDetailMockTest(DetailMockTest detailMockTest);
    List<MockTest> getTop10MockTest(int index);

    List<MockTest> getTop10MockTestToUser(int index, User user);

    MockTest findMockTestToId(UUID mockTestId);

    ResponseEntity<ResponseModel> findMockTestById(UUID mockTestId);

    List<DetailMockTest> getTop10DetailToCorrect(int index, boolean isCorrect ,MockTest mockTest);

    int countCorrectAnswer(UUID mockTestId);

    List<MockTest> getAllMockTestByYearMonthAndDay(Topic topic, String year, String month, String day);
    List<MockTest> getAllMockTestToTopic(Topic topic);
}
