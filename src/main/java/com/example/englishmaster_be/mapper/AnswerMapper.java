package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerCorrectResponse;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
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
    AnswerCorrectResponse toCheckCorrectAnswerResponse(AnswerEntity answer);

}
