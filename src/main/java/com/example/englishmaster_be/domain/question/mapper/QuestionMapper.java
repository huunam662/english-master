package com.example.englishmaster_be.domain.question.mapper;

import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.excel.dto.response.ExcelQuestionResponse;
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

    @Mapping(target = "questionGroupChildren", expression = "java(questionDtoToQuestionSet(questionDto.getListQuestionChild()))")
    QuestionEntity toQuestionEntity(QuestionRequest questionDto);

    default Set<QuestionEntity> questionDtoToQuestionSet(Collection<QuestionRequest> questionDto) {
        if(questionDto == null){
            return Collections.emptySet();
        }
        return questionDto.stream().map(this::toQuestionEntity).collect(Collectors.toSet());
    }

    QuestionEntity toQuestionEntity(QuestionGroupRequest createGroupQuestionDTO);

    @Mapping(target = "numberOfQuestionsChild", expression = "java(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0)")
    @Mapping(target = "questionsChildren", expression = "java(toQuestionChildResponseList(questionEntity.getQuestionGroupChildren()))")
    @Mapping(target = "topicId", source = "part.topicId")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity);

    List<QuestionResponse> toQuestionResponseList(Collection<QuestionEntity> questionEntityList);

    @Mapping(target = "questionsChildren", expression = "java(toQuestionChildResponseList(questionEntity.getQuestionGroupChildren(), partEntity))")
    @Mapping(target = "topicId", source = "questionEntity.part.topicId")
    @Mapping(target = "partId", source = "questionEntity.partId")
    QuestionResponse toQuestionResponse(QuestionEntity questionEntity, PartEntity partEntity);

    default List<QuestionResponse> toQuestionResponseList(Collection<QuestionEntity> questionEntityList, PartEntity partEntity){

        return QuestionUtil.parseQuestionResponseList(questionEntityList, partEntity);
    }


    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "topicId", source = "part.topicId")
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
    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartAndTotalQuestionResponse(partEntity))")
    @Mapping(target = "questionParents", expression = "java(toQuestionResponseList(questionParents, partEntity))")
    QuestionPartResponse toQuestionPartResponse(Collection<QuestionEntity> questionParents, TopicEntity topicEntity, PartEntity partEntity);

    default List<QuestionPartResponse> toQuestionPartResponseList(TopicEntity topic){

        if (topic == null) return Collections.emptyList();

        if(topic.getParts() == null || topic.getParts().isEmpty())
            return Collections.emptyList();

        return topic.getParts().stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .map(part -> {
                    QuestionPartResponse questionPartResponse = toQuestionPartResponse(part.getQuestions(), topic, part);
                    if(topic.getTopicType().getTopicTypeName().equalsIgnoreCase("speaking")){
                        questionPartResponse.getPart().setTotalQuestion(
                                part.getQuestions().size()
                        );
                    }
                    else questionPartResponse.getPart().setTotalQuestion(QuestionUtil.totalQuestionChildOf(part.getQuestions()));

                    return questionPartResponse;
                }
                ).collect(Collectors.toList());
    }

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(QuestionRequest questionRequest, @MappingTarget QuestionEntity questionEntity);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

    QuestionEntity toQuestionParent(CreateQuestionParentRequest request);

    QuestionEntity toQuestionChild(CreateQuestionChildRequest request);

    QuestionEntity toQuestionEntity(EditQuestionParentRequest questionParentRequest);

    QuestionEntity toQuestionEntity(EditQuestionChildRequest questionChildRequest);

    QuestionSpeakingResponse toQuestionSpeakingResponse(QuestionEntity question);

    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionChild.getAnswers()))")
    QuestionAnswersResponse toQuestionAnswersResponse(QuestionEntity questionChild);

    QuestionReadingListeningResponse toQuestionReadingListeningResponse(QuestionEntity questionEntity);

    List<QuestionReadingListeningResponse> toQuestionReadingListeningResponseList(Collection<QuestionEntity> questionEntityList);

}
