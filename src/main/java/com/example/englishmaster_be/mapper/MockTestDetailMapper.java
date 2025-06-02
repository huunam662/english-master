package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestDetailResponse;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface MockTestDetailMapper {

    MockTestDetailMapper INSTANCE = Mappers.getMapper(MockTestDetailMapper.class);

    @Mapping(target = "questionChild", expression = "java(MockTestMapper.INSTANCE.toMockTestQuestionResponse(mockTestDetailEntity.getQuestionChild()))")
    @Mapping(target = "answerChoice", expression = "java(MockTestMapper.INSTANCE.toMockTestAnswerResponse(mockTestDetailEntity.getAnswerChoice()))")
    @Mapping(target = "question" , expression = "java(MockTestMapper.INSTANCE.toMockTestQuestionResponse(mockTestDetailEntity.getQuestionChild().getQuestionGroupParent()))")
    MockTestDetailResponse toMockTestDetailResponse(MockTestDetailEntity mockTestDetailEntity);

    List<MockTestDetailResponse> toMockTestDetailResponseList(Collection<MockTestDetailEntity> mockTestDetailEntityList);

}
