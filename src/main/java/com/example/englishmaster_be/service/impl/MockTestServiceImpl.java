package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.exception.custom.CustomException;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.model.response.MockTestResponse;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.IMockTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.englishmaster_be.exception.enums.Error;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class MockTestServiceImpl implements IMockTestService {

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private DetailMockTestRepository detailMockTestRepository;

    @Override
    public ResponseEntity<ResponseModel> findMockTestById(UUID mockTestId) {
        ResponseModel responseModel = new ResponseModel();

        MockTest mockTest = mockTestRepository.findByMockTestId(mockTestId)
                .orElseThrow(() -> new CustomException(Error.MOCK_TEST_NOT_FOUND));

        MockTestResponse mockTestResponse = new MockTestResponse(mockTest);
        responseModel.setMessage("Find mockTest successfully");
        responseModel.setResponseData(mockTestResponse);

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

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
        return mockTestRepository.findByMockTestId(mockTestId).orElseThrow(() -> new CustomException(Error.MOCK_TEST_NOT_FOUND));
    }

    @Override
    public List<DetailMockTest> getTop10DetailToCorrect(int index, boolean isCorrect, MockTest mockTest) {
        Logger logger = LoggerFactory.getLogger(MockTestServiceImpl.class);

        Page<DetailMockTest> detailMockTestPage = detailMockTestRepository.findAllByMockTest(mockTest, PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));

        logger.info("repository returned: {}", detailMockTestPage.getContent());
        List<DetailMockTest> detailMockTests = detailMockTestPage.getContent();
        Iterator<DetailMockTest> iterator = detailMockTests.iterator();

        // Dùng Iterator để tránh ConcurrentModificationException
        while (iterator.hasNext()) {
            DetailMockTest detailMockTest = iterator.next();
            if (detailMockTest.getAnswer().isCorrectAnswer() != isCorrect) {
                iterator.remove();
            }
        }

        // Kiểm tra nếu danh sách trống hoặc chỉ có 1 phần tử và cần trả về null
        if (detailMockTests.isEmpty()) {
            return null;
        }

        return detailMockTests;
    }


    @Override
    public int countCorrectAnswer(UUID mockTestId) {
        int count = 0;
        MockTest mockTest = findMockTestToId(mockTestId);
        for (DetailMockTest detailMockTest : mockTest.getDetailMockTests()) {
            if (detailMockTest.getAnswer().isCorrectAnswer()) {
                count = count + 1;
            }
        }
        return count;
    }

    @Override
    public List<MockTest> getAllMockTestByYearMonthAndDay(Topic topic, String year, String month, String day) {
        if (day == null && month != null) {
            return mockTestRepository.findAllByYearMonth(year, month, topic);
        }
        if (month == null) {
            return mockTestRepository.findAllByYear(year, topic);
        }
        return mockTestRepository.findAllByYearMonthAndDay(year, month, day, topic);
    }

    @Override
    public List<MockTest> getAllMockTestToTopic(Topic topic) {
        return mockTestRepository.findAllByTopic(topic);
    }


}
