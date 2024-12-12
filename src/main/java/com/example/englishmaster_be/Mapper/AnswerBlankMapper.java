package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.entity.AnswerBlankEntity;
import com.example.englishmaster_be.Model.Response.AnswerBlankResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AnswerBlankMapper {

    AnswerBlankMapper INSTANCE = Mappers.getMapper(AnswerBlankMapper.class);

    @Mapping(target = "question", ignore = true)
    AnswerBlankResponse toAnswerBlankResponseWithExcludeQuestion(AnswerBlankEntity answerBlank);

    @Mapping(target = "question", expression = "java(QuestionMapper.INSTANCE.toQuestionResponse(answerBlank.getQuestion()))")
    AnswerBlankResponse toAnswerBlankResponse(AnswerBlankEntity answerBlank);

    default List<AnswerBlankResponse> toAnswerBlankResponseList(List<AnswerBlankEntity> answerBlankList){

        if (answerBlankList == null) return null;

        return answerBlankList.stream()
                .map(this::toAnswerBlankResponse)
                .toList();
    }

}
