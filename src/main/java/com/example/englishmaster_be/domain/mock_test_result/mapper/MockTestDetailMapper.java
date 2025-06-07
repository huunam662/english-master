package com.example.englishmaster_be.domain.mock_test_result.mapper;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestDetailResponse;
import com.example.englishmaster_be.domain.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestDetailEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(imports = {MockTestMapper.class}, builder = @Builder(disableBuilder = true))
public interface MockTestDetailMapper {

    MockTestDetailMapper INSTANCE = Mappers.getMapper(MockTestDetailMapper.class);

    @Mapping(target = "questionChild", expression = "java(MockTestMapper.INSTANCE.toMockTestQuestionAnswersResponse(mockTestDetailEntity.getQuestionChild()))")
    @Mapping(target = "answerChoice", expression = "java(MockTestMapper.INSTANCE.toMockTestAnswerResponse(mockTestDetailEntity.getAnswerChoice()))")
    @Mapping(target = "question" , expression = "java(MockTestMapper.INSTANCE.toMockTestQuestionResponse(mockTestDetailEntity.getQuestionChild().getQuestionGroupParent()))")
    MockTestDetailResponse toMockTestDetailResponse(MockTestDetailEntity mockTestDetailEntity);

    List<MockTestDetailResponse> toMockTestDetailResponseList(Collection<MockTestDetailEntity> mockTestDetailEntityList);

}
