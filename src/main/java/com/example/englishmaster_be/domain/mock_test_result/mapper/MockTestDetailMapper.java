package com.example.englishmaster_be.domain.mock_test_result.mapper;

import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestDetailResponse;
import com.example.englishmaster_be.domain.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestDetailEntity;
import com.example.englishmaster_be.domain.question.mapper.QuestionMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        imports = {MockTestMapper.class, QuestionMapper.class, AnswerMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface MockTestDetailMapper {

    MockTestDetailMapper INSTANCE = Mappers.getMapper(MockTestDetailMapper.class);

    @Mapping(target = "questionChild", expression = "java(QuestionMapper.INSTANCE.toQuestionAnswersResponse(mockTestDetailEntity.getQuestionChild()))")
    @Mapping(target = "answerChoice", expression = "java(AnswerMapper.INSTANCE.toAnswerBasicResponse(mockTestDetailEntity.getAnswerChoice()))")
    @Mapping(target = "question" , expression = "java(QuestionMapper.INSTANCE.toQuestionReadingListeningResponse(mockTestDetailEntity.getQuestionChild().getQuestionGroupParent()))")
    MockTestDetailResponse toMockTestDetailResponse(MockTestDetailEntity mockTestDetailEntity);

    List<MockTestDetailResponse> toMockTestDetailResponseList(Collection<MockTestDetailEntity> mockTestDetailEntityList);

}
