package com.example.englishmaster_be.domain.exam.question.mapper;

import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.exam.question.dto.req.CreateQuestionChildReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.CreateQuestionParentReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.EditQuestionChildReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.EditQuestionParentReq;
import com.example.englishmaster_be.domain.exam.question.dto.res.*;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.mapper.TopicMapper;
import com.example.englishmaster_be.domain.exam.question.util.QuestionUtil;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import java.util.*;

@Mapper(
        imports = {AnswerMapper.class, TopicMapper.class, PartMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "numberOfQuestionsChild", expression = "java(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0)")
    @Mapping(target = "questionsChildren", expression = "java(toQuestionChildResponseList(questionEntity.getQuestionGroupChildren()))")
    @Mapping(target = "topicId", source = "part.topicId")
    QuestionRes toQuestionResponse(QuestionEntity questionEntity);

    default List<QuestionRes> toQuestionResponseList(Collection<QuestionEntity> questionEntityList){

        return QuestionUtil.parseQuestionResponseList(questionEntityList);
    }

    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "topicId", source = "part.topicId")
    @Mapping(target = "questionsChildren", ignore = true)
    QuestionChildRes toQuestionChildResponse(QuestionEntity questionEntity);

    default List<QuestionChildRes> toQuestionChildResponseList(Collection<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return Collections.emptyList();

        return questionEntityList.stream().map(
                this::toQuestionChildResponse
        ).toList();
    }

    @Mapping(target = "topic", expression = "java(TopicMapper.INSTANCE.toTopicBasicResponse(topicEntity))")
    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartAndTotalQuestionResponse(partEntity))")
    @Mapping(target = "questionParents", expression = "java(toQuestionResponseList(questionParents))")
    QuestionPartRes toQuestionPartResponse(Collection<QuestionEntity> questionParents, TopicEntity topicEntity, PartEntity partEntity);

    default List<QuestionPartRes> toQuestionPartResponseList(TopicEntity topic){

        if (topic == null) return Collections.emptyList();

        if(topic.getParts() == null || topic.getParts().isEmpty())
            return Collections.emptyList();

        return topic.getParts().stream()
                .sorted(Comparator.comparing(PartEntity::getPartName, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(part -> {
                    String topicType = topic.getTopicType().getTopicTypeName();
                    boolean isSpeakingOrWriting = topicType.equalsIgnoreCase("speaking") || topicType.equalsIgnoreCase("writing");
                    int totalQuestionOfPart;
                    if(isSpeakingOrWriting) {
                        part.getQuestions().forEach(questionParent -> questionParent.setQuestionGroupChildren(new LinkedHashSet<>()));
                        totalQuestionOfPart = part.getQuestions().size();
                    }
                    else {
                        totalQuestionOfPart = QuestionUtil.totalQuestionChildOf(part.getQuestions());
                    }
                    QuestionPartRes questionPartResponse = toQuestionPartResponse(part.getQuestions(), topic, part);
                    questionPartResponse.getPart().setTotalQuestion(totalQuestionOfPart);
                    return questionPartResponse;
                }
                ).toList();
    }

    QuestionEntity toQuestionParent(CreateQuestionParentReq request);

    QuestionEntity toQuestionChild(CreateQuestionChildReq request);

    QuestionEntity toQuestionEntity(EditQuestionParentReq questionParentRequest);

    QuestionEntity toQuestionEntity(EditQuestionChildReq questionChildRequest);

    QuestionSpeakingRes toQuestionSpeakingResponse(QuestionEntity question);

    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionChild.getAnswers()))")
    QuestionAnswersRes toQuestionAnswersResponse(QuestionEntity questionChild);

    QuestionReadingListeningRes toQuestionReadingListeningResponse(QuestionEntity questionEntity);

    List<QuestionReadingListeningRes> toQuestionReadingListeningResponseList(Collection<QuestionEntity> questionEntityList);

}
