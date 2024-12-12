package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.Answer.AnswerBasicRequest;
import com.example.englishmaster_be.Model.Request.Answer.AnswerRequest;
import com.example.englishmaster_be.entity.AnswerEntity;
import com.example.englishmaster_be.Model.Response.AnswerResponse;
import com.example.englishmaster_be.Model.Response.CheckCorrectAnswerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import java.util.List;


@Mapper
public interface AnswerMapper {

    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    @Mapping(target = "questionId", source = "question.questionId")
    AnswerResponse toAnswerResponse(AnswerEntity answer);

    List<AnswerResponse> toAnswerResponseList(List<AnswerEntity> answerList);

    @Mapping(target = "answerId", ignore = true)
    void flowToAnswerEntity(AnswerRequest answerRequest, @MappingTarget AnswerEntity answer);

    void flowToAnswerEntity(AnswerBasicRequest answerBasicRequest, @MappingTarget AnswerEntity answer);

    @Mapping(target = "scoreAnswer", source = "question.questionScore")
    CheckCorrectAnswerResponse toCheckCorrectAnswerResponse(AnswerEntity answer);

}
