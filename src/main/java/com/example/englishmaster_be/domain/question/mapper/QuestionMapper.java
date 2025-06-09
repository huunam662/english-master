package com.example.englishmaster_be.domain.question.mapper;

import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.question.dto.request.*;
import com.example.englishmaster_be.domain.question.dto.response.*;
import com.example.englishmaster_be.domain.topic.mapper.TopicMapper;
import com.example.englishmaster_be.domain.question.util.QuestionUtil;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(
        imports = {AnswerMapper.class, TopicMapper.class, PartMapper.class},
        builder = @Builder(disableBuilder = true)
)
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

    @Mapping(target = "numberOfQuestionsChild", expression = "java(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0)")
    @Mapping(target = "questionsChildren" , expression = "java(toQuestionChildResponseList(questionEntity.getQuestionGroupChildren()))")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity);

    List<QuestionResponse> toQuestionResponseList(Collection<QuestionEntity> questionEntityList);

    @Mapping(target = "questionsChildren", expression = "java(toQuestionChildResponseList(questionEntity.getQuestionGroupChildren(), partEntity))")
    @Mapping(target = "partId", source = "questionEntity.partId")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity, PartEntity partEntity);

    default List<QuestionResponse> toQuestionResponseList(Collection<QuestionEntity> questionEntityList, PartEntity partEntity){

        return QuestionUtil.parseQuestionResponseList(questionEntityList, partEntity);
    }


    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "questionsChildren", ignore = true)
    QuestionChildResponse toQuestionChildResponse(QuestionEntity questionEntity);

    default List<QuestionChildResponse> toQuestionChildResponseList(Collection<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return Collections.emptyList();

        return questionEntityList.stream().map(
                this::toQuestionChildResponse
        ).toList();
    }

    @Mapping(target = "answers", expression = "java(!questionEntity.getIsQuestionParent() ? AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()) : null)")
    @Mapping(target = "questionsChildren", ignore = true)
    @Mapping(target = "partId", source = "questionEntity.partId")
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
                        part -> {

                            if(topic.getTopicType().getTopicTypeName().equalsIgnoreCase("speaking")){
                                part.setQuestions(
                                        part.getQuestions().stream()
                                        .peek(
                                                questionParent -> questionParent.setQuestionGroupChildren(
                                                        questionParent.getQuestionGroupChildren().stream()
                                                        .sorted(Comparator.comparing(QuestionEntity::getQuestionScore))
                                                        .collect(Collectors.toCollection(LinkedHashSet::new))
                                                )
                                        )
                                        .sorted(Comparator.comparing(QuestionEntity::getQuestionScore))
                                        .collect(Collectors.toCollection(LinkedHashSet::new))
                                );
                            }

                            return toQuestionPartResponse(part.getQuestions(), topic, part);
                        }
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

    QuestionEntity toQuestionParent(CreateQuestionParentRequest request);


    QuestionEntity toQuestionChild(CreateQuestionChildRequest request);

    QuestionEntity toQuestionEntity(EditQuestionParentRequest questionParentRequest);

    QuestionEntity toQuestionEntity(EditQuestionChildRequest questionChildRequest);
}
