package com.example.englishmaster_be.domain.mock_test_result.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.mapper.MockTestResultMapper;
import com.example.englishmaster_be.domain.mock_test_result.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultRepository;
import com.example.englishmaster_be.domain.mock_test.service.IMockTestService;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.mock_test_result.QMockTestResultEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultMockTestService implements IResultMockTestService {


    MockTestResultRepository resultMockTestRepository;

    IUserService userService;

    IPartService partService;

    IMockTestService mockTestService;


    @Transactional
    @Override
    public MockTestResultEntity saveResultMockTest(ResultMockTestRequest resultMockTest) {

        UserEntity user = userService.currentUser();

        PartEntity part = partService.getPartToId(resultMockTest.getPartId());

        MockTestEntity mockTest = mockTestService.findMockTestToId(resultMockTest.getMockTestId());

        MockTestResultEntity resultMockTestEntity;

        if(resultMockTest.getResultMockTestId() != null)
            resultMockTestEntity = getResultMockTestById(resultMockTest.getResultMockTestId());

        else resultMockTestEntity = MockTestResultEntity.builder()
                .createAt(LocalDateTime.now())
                .userCreate(user)
                .build();

        resultMockTestEntity.setUpdateAt(LocalDateTime.now());
        resultMockTestEntity.setUserUpdate(user);
        resultMockTestEntity.setPart(part);
        resultMockTestEntity.setMockTest(mockTest);

        MockTestResultMapper.INSTANCE.flowToResultMockTest(resultMockTest, resultMockTestEntity);

        return resultMockTestRepository.save(resultMockTestEntity);
    }

    @Override
    public List<MockTestResultEntity> getAllResultMockTests() {

        return resultMockTestRepository.findAll();
    }

    @Override
    public List<MockTestResultEntity> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId) {

        BooleanBuilder builder = new BooleanBuilder().and(QMockTestResultEntity.mockTestResultEntity.part.isNotNull());

        if (partId != null)
            builder.and(QMockTestResultEntity.mockTestResultEntity.part.partId.eq(partId));

        if (mockTestId != null)
            builder.and(QMockTestResultEntity.mockTestResultEntity.mockTest.mockTestId.eq(mockTestId));

        Predicate predicate = builder.getValue();

        Iterable<MockTestResultEntity> resultMockTestIterable = resultMockTestRepository.findAll(predicate);

        return StreamSupport.stream(resultMockTestIterable.spliterator(), false).toList();
    }

    @Override
    public MockTestResultEntity getResultMockTestById(UUID id) {
        return resultMockTestRepository.findById(id).orElseThrow(
                () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Result mock test not found")
        );
    }

    @Override
    public void deleteResultMockTestById(UUID id) {

        MockTestResultEntity resultMockTest = getResultMockTestById(id);

        resultMockTestRepository.delete(resultMockTest);
    }

    @Override
    public List<MockTestResultEntity> getAllMockTestResults(UUID mockTestId) {

        Assert.notNull(mockTestId, "Mock test id is required.");

        return resultMockTestRepository.findResultJoinPartQuestionAnswer(mockTestId);
    }
}
