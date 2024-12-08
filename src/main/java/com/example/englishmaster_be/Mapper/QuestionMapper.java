package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.Question.SaveGroupQuestionDTO;
import com.example.englishmaster_be.DTO.Question.SaveQuestionDTO;
import com.example.englishmaster_be.Model.Answer;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.Response.AnswerResponse;
import com.example.englishmaster_be.Model.Response.QuestionGroupResponse;
import com.example.englishmaster_be.Model.Response.QuestionResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

@Mapper
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    Question toQuestionEntity(SaveQuestionDTO questionDto);

    Question toQuestionEntity(SaveGroupQuestionDTO saveGroupQuestionDTO);


    @Mapping(target = "questionGroup", ignore = true)
    @Mapping(source = "part.partId", target = "partId")
    @Mapping(source = "contentCollection", target = "contentList")
    @Mapping(source = "answers", target = "listAnswer", qualifiedByName = {"mapListAnswersToListAnswerResponse"})
    QuestionResponse toQuestionResponse(Question question);

    @Named("mapListAnswersToListAnswerResponse")
    default List<AnswerResponse> mapListAnswersToListAnswerResponse(List<Answer> answers) {
        return answers.stream()
                .filter(Objects::nonNull)
                .map(answer -> new AnswerResponse(answer))
                .toList();
    }

    @IterableMapping(elementTargetType = QuestionResponse.class)
    List<QuestionResponse> toQuestionResponseList(List<Question> questionList);

    @Mapping(source = "part.partId", target = "partId")
    @Mapping(source = "questionGroup.questionId", target = "questionGroupId")
    QuestionGroupResponse toQuestionGroupResponse(Question question);

    @IterableMapping(elementTargetType = QuestionGroupResponse.class)
    List<QuestionGroupResponse> toQuestionGroupResponseList(List<Question> questionList);
}
