package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMatchingResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.helper.AnswerHelper;
import com.example.englishmaster_be.helper.QuestionHelper;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true))
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionEntity toQuestionEntity(QuestionRequest questionDto);

    QuestionEntity toQuestionEntity(QuestionGroupRequest saveGroupQuestionDTO);

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "numberOfQuestionsChild", expression = "java(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0)")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity);

    List<QuestionResponse> toQuestionResponseList(List<QuestionEntity> questionEntityList);

    default List<QuestionResponse> toQuestionResponseList(List<QuestionEntity> questionEntityList, PartEntity partEntity) {

        List<QuestionEntity> questionsList4Shuffle = new ArrayList<>(questionEntityList);

        if(!partEntity.getPartType().equalsIgnoreCase("Text Completion"))
            Collections.shuffle(questionsList4Shuffle);

        return questionsList4Shuffle.stream()
                .map(
                        this::toQuestionResponse
                )
                .toList();
    }

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

    default List<QuestionResponse> toQuestionResponseList(List<QuestionEntity> questionEntityList, TopicEntity topicEntity, PartEntity partEntity, Boolean isAdmin) {

        if(questionEntityList == null) return null;

        questionEntityList = QuestionHelper.shuffleQuestionsAndAnswers(questionEntityList, partEntity);

        List<String> partTypesWithoutAnswerCorrectId = List.of("Words Fill Completion", "Words Matching");

        boolean withAnswerCorrectId = isAdmin && partTypesWithoutAnswerCorrectId.stream().noneMatch(
                partType -> partType.equalsIgnoreCase(partEntity.getPartType())
        );

        boolean partTypeIsWordsMatching = partEntity.getPartType().equalsIgnoreCase("Words Matching");

        return questionEntityList.stream().map(
                questionEntity -> {

                    QuestionResponse questionResponse = toQuestionResponse(questionEntity, topicEntity, partEntity, isAdmin);

                    if(questionResponse == null) return null;

                    if(withAnswerCorrectId)
                        questionResponse.setAnswerCorrectId(AnswerHelper.getIdCorrectAnswer(questionEntity.getAnswers()));

                    if(partTypeIsWordsMatching){

                        QuestionMatchingResponse questionMatchingResponse = null;

                        if(questionEntity.getQuestionGroupChildren() != null){

                            List<QuestionResponse> contentLeft = toQuestionResponseList(questionEntity.getQuestionGroupChildren(), topicEntity, partEntity, isAdmin);

                            List<QuestionResponse> contentRight = questionEntity.getQuestionGroupChildren().stream().map(
                                    questionContentRight -> {

                                        String questionContent = questionContentRight.getQuestionContent();
                                        String questionResult = questionContentRight.getQuestionResult();

                                        questionContentRight.setQuestionResult(questionContent);
                                        questionContentRight.setQuestionContent(questionResult);

                                        return toQuestionResponse(questionContentRight, topicEntity, partEntity, isAdmin);
                                    }
                            ).toList();

                            questionMatchingResponse = QuestionMatchingResponse.builder()
                                    .contentLeft(contentLeft)
                                    .contentRight(contentRight)
                                    .build();
                        }

                        if(questionMatchingResponse != null)
                            questionResponse.setQuestionsChildren(List.of(questionMatchingResponse));
                    }

                    questionResponse.setNumberOfQuestionsChild(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0);

                    return questionResponse;
                }
        ).toList();
    }


    @Mapping(target = "topic", expression = "java(TopicMapper.INSTANCE.toTopicBasicResponse(topicEntity))")
    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartBasicResponse(partEntity))")
    @Mapping(target = "questionParents", expression = "java(toQuestionResponseList(questionParents, topicEntity, partEntity, isAdmin))")
    QuestionPartResponse toQuestionPartResponse(List<QuestionEntity> questionParents, PartEntity partEntity, TopicEntity topicEntity, Boolean isAdmin);

    default List<QuestionPartResponse> toQuestionPartResponseList(List<QuestionEntity> questionEntityList, List<PartEntity> partEntityList, TopicEntity topicEntity, Boolean isAdmin) {

        if(questionEntityList == null) return null;

        return partEntityList.stream().map(
                partEntity -> {

                    List<QuestionEntity> questionEntityListFilter = questionEntityList.stream().filter(
                            questionEntity -> questionEntity.getPart().equals(partEntity)
                                                            && questionEntity.getTopics().contains(topicEntity)
                    ).toList();

                    QuestionPartResponse questionPartResponse = toQuestionPartResponse(questionEntityListFilter, partEntity, topicEntity, isAdmin);

                    questionEntityList.removeAll(questionEntityListFilter);

                    return questionPartResponse;

                }
        ).toList();
    }

    @AfterMapping
    default void setTotalQuestion(@MappingTarget QuestionPartResponse response, List<QuestionEntity> questionParents) {

        if (response.getPart() == null)
            response.setPart(new PartBasicResponse());

        response.getPart().setTotalQuestion(
                questionParents != null
                        ? QuestionHelper.totalQuestionChildOf(questionParents)
                        : 0
        );
    }

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(QuestionRequest questionRequest, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

}
