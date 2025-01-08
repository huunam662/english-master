package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerResponse;
import com.example.englishmaster_be.model.user_answer.UserAnswerEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface UserAnswerMapper {


    UserAnswerMapper INSTANCE = Mappers.getMapper(UserAnswerMapper.class);

    @Mapping(target = "question", expression = "java(QuestionMapper.INSTANCE.toQuestionBasicResponse(userAnswerEntity.getQuestion()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(userAnswerEntity.getAnswers()))")
    UserAnswerResponse toUserAnswerResponse(UserAnswerEntity userAnswerEntity);

}
