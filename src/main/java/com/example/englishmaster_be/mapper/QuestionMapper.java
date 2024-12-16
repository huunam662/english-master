package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionBasicResponse;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionEntity toQuestionEntity(QuestionRequest questionDto);

    QuestionEntity toQuestionEntity(QuestionGroupRequest saveGroupQuestionDTO);

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "contentList", source = "contentCollection")
    @Mapping(target = "listAnswer", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(question.getAnswers()))")
    @Mapping(target = "questionGroupParent", expression = "java(toQuestionBasicResponse(question.getQuestionGroupParent()))")
    @Mapping(target = "questionGroupChildren", expression = "java(toQuestionBasicResponseList(question.getQuestionGroupChildren()))")
    QuestionResponse toQuestionResponse(QuestionEntity question);

    List<QuestionResponse> toQuestionGroupChildrenResponseList(List<QuestionEntity> questionGroupChildren);

    @Mapping(target = "partId", source = "part.partId")
    QuestionBasicResponse toQuestionBasicResponse(QuestionEntity question);

    List<QuestionBasicResponse> toQuestionBasicResponseList(List<QuestionEntity> questionList);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(QuestionRequest questionRequest, @MappingTarget QuestionEntity questionEntity);
}
