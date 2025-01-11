package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionBasicResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
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

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "isQuestionParent", defaultValue = "false")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "questionsChildren", expression = "java(toExcelResponseListExcludeChild(questionEntity.getQuestionGroupChildren()))")
    ExcelQuestionResponse toExcelQuestionResponse(QuestionEntity questionEntity);

    default List<ExcelQuestionResponse> toExcelResponseListExcludeChild(List<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return null;

        return questionEntityList.stream().map(
                question -> {
                    if(question.getQuestionGroupChildren() != null){
                        question.getQuestionGroupChildren().forEach(
                                questionChild -> questionChild.setQuestionGroupChildren(null)
                        );
                    }

                    return toExcelQuestionResponse(question);
                }
        ).toList();
    }

    List<ExcelQuestionResponse> toExcelQuestionResponseList(List<QuestionEntity> questionEntityList);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(QuestionRequest questionRequest, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "partName", source = "part.partName")
    @Mapping(target = "questionParent", expression = "java(toExcelQuestionResponse(questionEntity))")
    QuestionPartResponse toQuestionPartResponse(QuestionEntity questionEntity);

    List<QuestionPartResponse> toQuestionPartResponseList(List<QuestionEntity> questionEntityList);
}
