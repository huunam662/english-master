package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Response.UserBlankAnswerResponse;
import com.example.englishmaster_be.entity.UserBlankAnswerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserBlankAnswerMapper {

    UserBlankAnswerMapper INSTANCE = Mappers.getMapper(UserBlankAnswerMapper.class);

    @Mapping(target = "question", expression = "java(QuestionMapper.INSTANCE.toQuestionBasicResponse(userBlankAnswerEntity.getQuestion()))")
    UserBlankAnswerResponse toUserBlankAnswerResponse(UserBlankAnswerEntity userBlankAnswerEntity);

}
