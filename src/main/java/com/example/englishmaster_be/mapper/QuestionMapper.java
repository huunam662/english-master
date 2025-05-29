package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.answer.dto.request.Answer1Request;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.domain.answer.dto.response.CreateAnswerResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionChildRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionParentRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.*;
import com.example.englishmaster_be.util.QuestionUtil;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true))
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "questionGroupChildren" , expression = "java(questionDtoToQuestionEntities(questionDto.getListQuestionChild()))")
    QuestionEntity toQuestionEntity(QuestionRequest questionDto);
    default Set<QuestionEntity> questionDtoToQuestionEntities(Collection<QuestionRequest> questionDto) {
        if(questionDto == null){
            return Collections.emptySet();
        }
        return questionDto.stream().map(this::toQuestionEntity).collect(Collectors.toSet());
    }

    QuestionEntity toQuestionEntity(QuestionGroupRequest createGroupQuestionDTO);

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "numberOfQuestionsChild", expression = "java(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0)")
    @Mapping(target = "questionsChildren" , expression = "java(toQuestionChildResponseList(questionEntity.getQuestionGroupChildren()))")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity);

    List<QuestionResponse> toQuestionResponseList(Collection<QuestionEntity> questionEntityList);

    @Mapping(target = "partId", expression = "java(questionEntity.getPart() != null ? questionEntity.getPart().getPartId() : null)")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "questionsChildren", expression = "java(toQuestionChildResponseList(questionEntity.getQuestionGroupChildren(), partEntity))")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity, PartEntity partEntity);

    default List<QuestionResponse> toQuestionResponseList(Collection<QuestionEntity> questionEntityList, PartEntity partEntity){

        return QuestionUtil.parseQuestionResponseList(questionEntityList, partEntity);
    }

    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "questionsChildren", ignore = true)
    @Mapping(target = "partId", expression = "java(questionEntity.getPart() != null ? questionEntity.getPart().getPartId() : null)")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    QuestionMatchingResponse toQuestionMatchingResponse(QuestionEntity questionEntity, TopicEntity topicEntity, PartEntity partEntity);

    default List<QuestionMatchingResponse> toQuestionMatchingResponseList(Collection<QuestionEntity> questionEntityList, TopicEntity topicEntity, PartEntity partEntity){

        if(questionEntityList == null) return null;

        return questionEntityList.stream().map(
                questionEntity -> toQuestionMatchingResponse(questionEntity, topicEntity, partEntity)
        ).toList();
    }

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    QuestionChildResponse toQuestionChildResponse(QuestionEntity questionEntity);

    default List<QuestionChildResponse> toQuestionChildResponseList(Collection<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return Collections.emptyList();

        return questionEntityList.stream().map(
                this::toQuestionChildResponse
        ).toList();
    }

    @Mapping(target = "partId", expression = "java(questionEntity.getPart() != null ? questionEntity.getPart().getPartId() : null)")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(!questionEntity.getIsQuestionParent() ? AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()) : null)")
    QuestionChildResponse toQuestionChildResponse(QuestionEntity questionEntity, PartEntity partEntity);

    default List<QuestionChildResponse> toQuestionChildResponseList(Collection<QuestionEntity> questionEntityList, PartEntity partEntity){

        if (questionEntityList == null) return Collections.emptyList();

        return questionEntityList.stream().map(
                question -> toQuestionChildResponse(question, partEntity)
        ).toList();
    }

    @Mapping(target = "topic", expression = "java(TopicMapper.INSTANCE.toTopicBasicResponse(topicEntity))")
    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartBasicResponse(partEntity))")
    @Mapping(target = "questionParents", expression = "java(toQuestionResponseList(questionParents, partEntity))")
    QuestionPartResponse toQuestionPartResponse(Collection<QuestionEntity> questionParents, TopicEntity topicEntity, PartEntity partEntity);

    default List<QuestionPartResponse> toQuestionPartResponseList(TopicEntity topic){

        if (topic == null) return Collections.emptyList();

        if(topic.getParts() == null || topic.getParts().isEmpty())
            return Collections.emptyList();

        return topic.getParts().stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .map(
                        part -> toQuestionPartResponse(part.getQuestions(), topic, part)
                ).collect(Collectors.toList());
    }

    default List<QuestionPartResponse> toQuestionPartResponseList(Collection<QuestionEntity> questionList, TopicEntity topic){

        if (questionList == null || questionList.isEmpty())
            return Collections.emptyList();

        List<PartEntity> parts = questionList.stream().map(
                QuestionEntity::getPart
        ).distinct().toList();

        return parts.stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .map(
                        part -> {
                            List<QuestionEntity> questionOfPart = questionList.stream().filter(
                                    question -> Objects.nonNull(question) && question.getPart().equals(part)
                            ).toList();

                            questionList.removeAll(questionOfPart);

                            return toQuestionPartResponse(questionOfPart, topic, part);
                        }
                ).toList();
    }

    @AfterMapping
    default void setTotalQuestion(@MappingTarget QuestionPartResponse response, Collection<QuestionEntity> questionParents) {

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

    QuestionEntity toQuestionEntity(QuestionParentRequest request);

    QuestionEntity toQuestionEntity(QuestionChildRequest request);
}
