package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMatchingResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.util.QuestionUtil;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true))
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "questionGroupChildren" , expression = "java(questionDtoToQuestionEntities(questionDto.getListQuestionChild()))")
    QuestionEntity toQuestionEntity(QuestionRequest questionDto);
    default List<QuestionEntity> questionDtoToQuestionEntities(List<QuestionRequest> questionDto) {
        if(questionDto == null){
            return Collections.emptyList();
        }
        return questionDto.stream().map(this::toQuestionEntity).collect(Collectors.toList());
    }

    QuestionEntity toQuestionEntity(QuestionGroupRequest createGroupQuestionDTO);
    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "numberOfQuestionsChild", expression = "java(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0)")
    @Mapping(target = "questionsChildren" , expression = "java(toQuestionResponseList(questionEntity.getQuestionGroupChildren()))")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity);
    
    List<QuestionResponse> toQuestionResponseList(List<QuestionEntity> questionEntityList);

    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "questionsChildren", ignore = true)
    @Mapping(target = "partId", expression = "java(questionEntity.getPart() != null ? questionEntity.getPart().getPartId() : null)")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    QuestionMatchingResponse toQuestionMatchingResponse(QuestionEntity questionEntity, TopicEntity topicEntity, PartEntity partEntity);

    default List<QuestionMatchingResponse> toQuestionMatchingResponseList(List<QuestionEntity> questionEntityList, TopicEntity topicEntity, PartEntity partEntity){

        if(questionEntityList == null) return null;

        return questionEntityList.stream().map(
                questionEntity -> toQuestionMatchingResponse(questionEntity, topicEntity, partEntity)
        ).toList();
    }

    @Mapping(target = "partId", expression = "java(questionEntity.getPart() != null ? questionEntity.getPart().getPartId() : null)")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "questionsChildren", expression = "java(toQuestionResponseList(questionEntity.getQuestionGroupChildren(), topicEntity, partEntity, isAdmin))")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity, TopicEntity topicEntity, PartEntity partEntity, Boolean isAdmin);

    default List<QuestionResponse> toQuestionResponseList(List<QuestionEntity> questionEntityList, TopicEntity topicEntity, PartEntity partEntity, Boolean isAdmin){

        return QuestionUtil.parseQuestionResponseList(questionEntityList, topicEntity, partEntity, isAdmin);
    }

    @Mapping(target = "topic", expression = "java(TopicMapper.INSTANCE.toTopicBasicResponse(topicEntity))")
    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartBasicResponse(partEntity))")
    @Mapping(target = "questionParents", expression = "java(toQuestionResponseList(questionParents, topicEntity, partEntity, isAdmin))")
    QuestionPartResponse toQuestionPartResponse(List<QuestionEntity> questionParents, PartEntity partEntity, TopicEntity topicEntity, Boolean isAdmin);

    default List<QuestionPartResponse> toQuestionPartResponseList(List<QuestionEntity> questionEntityList, List<PartEntity> partEntityList, TopicEntity topicEntity, Boolean isAdmin) {

        return QuestionUtil.parseQuestionPartResponseList(questionEntityList, partEntityList, topicEntity, isAdmin);
    }

    @AfterMapping
    default void setTotalQuestion(@MappingTarget QuestionPartResponse response, List<QuestionEntity> questionParents) {

        if (response.getPart() == null)
            response.setPart(new PartBasicResponse());

        response.getPart().setTotalQuestion(
                questionParents != null
                        ? QuestionUtil.totalQuestionChildOf(questionParents)
                        : 0
        );
    }

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(QuestionRequest questionRequest, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

}
