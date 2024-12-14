package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.mapper.ResultMockTestMapper;
import com.example.englishmaster_be.model.request.MockTest.ResultMockTestRequest;
import com.example.englishmaster_be.repository.ResultMockTestRepository;
import com.example.englishmaster_be.service.IMockTestService;
import com.example.englishmaster_be.service.IPartService;
import com.example.englishmaster_be.service.IResultMockTestService;
import com.example.englishmaster_be.service.IUserService;
import com.example.englishmaster_be.entity.MockTestEntity;
import com.example.englishmaster_be.entity.PartEntity;
import com.example.englishmaster_be.entity.ResultMockTestEntity;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.entity.QResultMockTestEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultMockTestServiceImpl implements IResultMockTestService {


    ResultMockTestRepository resultMockTestRepository;

    IUserService userService;

    IPartService partService;

    IMockTestService mockTestService;


    @Transactional
    @Override
    public ResultMockTestEntity saveResultMockTest(ResultMockTestRequest resultMockTest) {

        UserEntity user = userService.currentUser();

        PartEntity part = partService.getPartToId(resultMockTest.getPartId());

        MockTestEntity mockTest = mockTestService.findMockTestToId(resultMockTest.getMockTestId());

        ResultMockTestEntity resultMockTestEntity;

        if(resultMockTest.getResultMockTestId() != null)
            resultMockTestEntity = getResultMockTestById(resultMockTest.getResultMockTestId());

        else resultMockTestEntity = ResultMockTestEntity.builder()
                .createAt(LocalDateTime.now())
                .userCreate(user)
                .build();

        resultMockTestEntity.setUpdateAt(LocalDateTime.now());
        resultMockTestEntity.setUserUpdate(user);
        resultMockTestEntity.setPart(part);
        resultMockTestEntity.setMockTest(mockTest);

        ResultMockTestMapper.INSTANCE.flowToResultMockTest(resultMockTest, resultMockTestEntity);

        return resultMockTestRepository.save(resultMockTestEntity);
    }

    @Override
    public List<ResultMockTestEntity> getAllResultMockTests() {

        return resultMockTestRepository.findAll();
    }

    @Override
    public List<ResultMockTestEntity> getResultMockTestsByPartIdAndMockTestId(UUID partId, UUID mockTestId) {

        BooleanBuilder builder = new BooleanBuilder().and(QResultMockTestEntity.resultMockTestEntity.part.isNotNull());

        if (partId != null)
            builder.and(QResultMockTestEntity.resultMockTestEntity.part.partId.eq(partId));

        if (mockTestId != null)
            builder.and(QResultMockTestEntity.resultMockTestEntity.mockTest.mockTestId.eq(mockTestId));

        Predicate predicate = builder.getValue();

        Iterable<ResultMockTestEntity> resultMockTestIterable = resultMockTestRepository.findAll(predicate);

        return StreamSupport.stream(resultMockTestIterable.spliterator(), false).toList();
    }

    @Override
    public ResultMockTestEntity getResultMockTestById(UUID id) {
        return resultMockTestRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Result mock test not found")
        );
    }

    @Override
    public void deleteResultMockTestById(UUID id) {

        ResultMockTestEntity resultMockTest = getResultMockTestById(id);

        resultMockTestRepository.delete(resultMockTest);
    }
}
