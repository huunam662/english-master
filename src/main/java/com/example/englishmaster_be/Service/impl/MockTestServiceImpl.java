package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IMockTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MockTestServiceImpl implements IMockTestService {

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private DetailMockTestRepository detailMockTestRepository;

    @Override
    public void createMockTest(MockTest mockTest) {
        mockTestRepository.save(mockTest);
    }

    @Override
    public void createDetailMockTest(DetailMockTest detailMockTest) {
        detailMockTestRepository.save(detailMockTest);
    }

    @Override
    public List<MockTest> getTop10MockTest(int index) {
        Page<MockTest> mockTestPage = mockTestRepository.findAll(PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));
        return mockTestPage.getContent();
    }

    @Override
    public List<MockTest> getTop10MockTestToUser(int index, User user) {
        Page<MockTest> mockTestPage = mockTestRepository.findAllByUser(user, PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));
        return mockTestPage.getContent();
    }

    @Override
    public MockTest findMockTestToId(UUID mockTestId) {
        return mockTestRepository.findByMockTestId(mockTestId);
    }

    @Override
    public List<DetailMockTest> getTop10DetailToCorrect(int index, boolean isCorrect ,MockTest mockTest) {
        Page<DetailMockTest> detailMockTestPage = detailMockTestRepository.findAllByMockTest(mockTest, PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));
        for(DetailMockTest detailMockTest : detailMockTestPage.getContent()){
            if(detailMockTest.getAnswer().isCorrectAnswer() != isCorrect){
                if(detailMockTestPage.getContent().size() == 1){
                    return null;
                }
                detailMockTestPage.getContent().remove(detailMockTest);
            }
        }
        return detailMockTestPage.getContent();
    }


}
