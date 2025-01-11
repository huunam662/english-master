package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
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

    @Mapping(target = "topic", expression = "java(TopicMapper.INSTANCE.toTopicBasicResponse(topicEntity))")
    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartBasicResponse(partEntity))")
    @Mapping(target = "questionParents", expression = "java(toExcelQuestionResponseList(questionParents))")
    QuestionPartResponse toQuestionPartResponse(List<QuestionEntity> questionParents, PartEntity partEntity, TopicEntity topicEntity);

    @AfterMapping
    default void setTotalQuestion(@MappingTarget QuestionPartResponse response, List<QuestionEntity> questionParents) {
        if (response.getPart() == null) {
            response.setPart(new PartBasicResponse());
        }
        response.getPart().setTotalQuestion(calculateTotalQuestionOf(questionParents));
    }

    default int calculateTotalQuestionOf(List<QuestionEntity> questionParents) {

        if(questionParents == null) return 0;

        int totalQuestion = questionParents.size();

        int totalQuestionChild = questionParents.stream().map(
                questionEntity -> {
                    if(questionEntity.getQuestionGroupChildren() == null) return 0;

                    return questionEntity.getQuestionGroupChildren().size();
                }
        ).reduce(0, Integer::sum);

        return totalQuestion + totalQuestionChild;
    }

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(QuestionRequest questionRequest, @MappingTarget QuestionEntity questionEntity);

    List<QuestionPartResponse> toQuestionPartResponseList(List<QuestionEntity> questionEntityList);
}
