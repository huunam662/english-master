package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.answer.dto.request.CreateAnswer1Request;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerCorrectResponse;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(builder = @Builder(disableBuilder = true))
public interface AnswerMapper {

    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    @Mapping(target = "questionId", source = "question.questionId")
    AnswerResponse toAnswerResponse(AnswerEntity answer);

    List<AnswerResponse> toAnswerResponseList(Collection<AnswerEntity> answerList);

    AnswerBasicRequest toAnswerBasicRequest(AnswerResponse answerResponse);

    List<AnswerBasicRequest> toAnswerRequestList(Collection<AnswerResponse> answerResponseList);

    @Mapping(target = "answerId", ignore = true)
    void flowToAnswerEntity(AnswerRequest answerRequest, @MappingTarget AnswerEntity answer);

    void flowToAnswerEntity(AnswerBasicRequest answerBasicRequest, @MappingTarget AnswerEntity answer);

    @Mapping(target = "scoreAnswer", source = "question.questionScore")
    AnswerCorrectResponse toCheckCorrectAnswerResponse(AnswerEntity answer);

    AnswerEntity toAnswerEntity(CreateAnswer1Request answer1Request);

    default Set<AnswerEntity> toAnswerOfChildSet(QuestionEntity questionChild, List<CreateAnswer1Request> answer1RequestSet, UserEntity userUpdate){

        if(questionChild == null) return Collections.emptySet();

        if(answer1RequestSet == null) return Collections.emptySet();

        return answer1RequestSet.stream().map(
                answer1Request -> {
                    AnswerEntity answer = toAnswerEntity(answer1Request);
                    answer.setQuestion(questionChild);
                    answer.setUserCreate(userUpdate);
                    answer.setUserUpdate(userUpdate);
                    return answer;
                }
        ).collect(Collectors.toSet());
    }
}
