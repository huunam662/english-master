package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.DetailMockTest;
import com.example.englishmaster_be.Model.MockTest;
import com.example.englishmaster_be.Model.User;

import java.util.List;
import java.util.UUID;

public interface IMockTestService {
    void createMockTest(MockTest mockTest);

    void createDetailMockTest(DetailMockTest detailMockTest);
    List<MockTest> getTop10MockTest(int index);

    List<MockTest> getTop10MockTestToUser(int index, User user);

    MockTest findMockTestToId(UUID mockTestId);

    List<DetailMockTest> getTop10DetailToCorrect(int index, boolean isCorrect ,MockTest mockTest);
}
