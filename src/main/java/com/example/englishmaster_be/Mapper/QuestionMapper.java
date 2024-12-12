package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.Question.GroupQuestionRequest;
import com.example.englishmaster_be.Model.Request.Question.QuestionRequest;
import com.example.englishmaster_be.Model.Response.QuestionBasicResponse;
import com.example.englishmaster_be.Model.Response.excel.QuestionByExcelFileResponse;
import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.Model.Response.QuestionResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionEntity toQuestionEntity(QuestionRequest questionDto);

    QuestionEntity toQuestionEntity(GroupQuestionRequest saveGroupQuestionDTO);

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
    void flowToQuestionEntity(QuestionByExcelFileResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(QuestionRequest questionRequest, @MappingTarget QuestionEntity questionEntity);
}
