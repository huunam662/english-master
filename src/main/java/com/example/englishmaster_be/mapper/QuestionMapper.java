package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
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
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity);

    List<QuestionResponse> toQuestionResponseList(List<QuestionEntity> questionEntityList);

    @Mapping(target = "partId", expression = "java(questionEntity.getPart() != null ? questionEntity.getPart().getPartId() : null)")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "questionsChildren",
            expression = "java(" +
                                "isAdmin " +
                                "? toQuestionResponseIncludeAnswerCorrectList(questionEntity.getQuestionGroupChildren(), topicEntity, isAdmin)" +
                                ": toQuestionResponseList(questionEntity.getQuestionGroupChildren(), topicEntity, isAdmin)" +
                        ")"
    )
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity, TopicEntity topicEntity, Boolean isAdmin);

    default List<QuestionResponse> toQuestionResponseList(List<QuestionEntity> questionEntityList, TopicEntity topicEntity, Boolean isAdmin) {

        if(questionEntityList == null) return null;

        return questionEntityList.stream().map(
                questionEntity -> {

                    if(questionEntity.getQuestionGroupChildren() != null)
                        questionEntity.getQuestionGroupChildren().forEach(
                                questionGroupChildEntity -> {
                                    if(questionGroupChildEntity.getQuestionGroupChildren() != null)
                                        questionGroupChildEntity.getQuestionGroupChildren().forEach(
                                                questionGroupChild -> questionGroupChild.setQuestionGroupChildren(null)
                                        );
                                }
                        );

                    return toQuestionResponse(questionEntity, topicEntity, isAdmin);
                }
        ).toList();
    }

    default List<QuestionResponse> toQuestionResponseIncludeAnswerCorrectList(List<QuestionEntity> questionEntityList, TopicEntity topicEntity, Boolean isAdmin) {

        if(questionEntityList == null) return null;

        return questionEntityList.stream().map(
                questionEntity -> {

                    if(questionEntity.getQuestionGroupChildren() != null)
                        questionEntity.getQuestionGroupChildren().forEach(
                                questionGroupChildEntity -> {
                                    if(questionGroupChildEntity.getQuestionGroupChildren() != null)
                                        questionGroupChildEntity.getQuestionGroupChildren().forEach(
                                                questionGroupChild -> questionGroupChild.setQuestionGroupChildren(null)
                                        );
                                }
                        );

                    QuestionResponse questionResponse = toQuestionResponse(questionEntity, topicEntity, isAdmin);

                    if(questionEntity.getAnswers() == null) return questionResponse;

                    questionEntity.getAnswers().stream().filter(
                            answerEntity -> answerEntity.getCorrectAnswer().equals(Boolean.TRUE)
                    ).findFirst().ifPresent(
                            answerEntity -> questionResponse.setAnswerCorrectId(answerEntity.getAnswerId())
                    );

                    return questionResponse;
                }
        ).toList();
    }

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "isQuestionParent", defaultValue = "false")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "questionsChildren", expression = "java(toExcelResponseExcludeChildList(questionEntity.getQuestionGroupChildren()))")
    ExcelQuestionResponse toExcelQuestionResponse(QuestionEntity questionEntity);

    default List<ExcelQuestionResponse> toExcelResponseExcludeChildList(List<QuestionEntity> questionEntityList){

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
    @Mapping(target = "questionParents", expression = "java(toQuestionResponseList(questionParents, topicEntity, isAdmin))")
    QuestionPartResponse toQuestionPartResponse(List<QuestionEntity> questionParents, PartEntity partEntity, TopicEntity topicEntity, Boolean isAdmin);

    default List<QuestionPartResponse> toQuestionPartResponseList(List<PartEntity> partEntityList, TopicEntity topicEntity, Boolean isAdmin) {

        if(partEntityList == null || topicEntity == null) return null;

        return partEntityList.stream().map(
                partEntity -> {

                    List<QuestionEntity> questionParents = topicEntity.getQuestions().stream().filter(
                            questionEntity -> questionEntity.getPart().equals(partEntity)
                    ).toList();

                    return QuestionMapper.INSTANCE.toQuestionPartResponse(questionParents, partEntity, topicEntity, isAdmin);
                }
        ).toList();

    };

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

}
