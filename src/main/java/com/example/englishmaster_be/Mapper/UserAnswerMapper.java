package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Response.UserAnswerResponse;
import com.example.englishmaster_be.entity.UserAnswerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAnswerMapper {


    UserAnswerMapper INSTANCE = Mappers.getMapper(UserAnswerMapper.class);

    @Mapping(target = "question", expression = "java(QuestionMapper.INSTANCE.toQuestionBasicResponse(userAnswerEntity.getQuestion()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(userAnswerEntity.getAnswers()))")
    UserAnswerResponse toUserAnswerResponse(UserAnswerEntity userAnswerEntity);

}
