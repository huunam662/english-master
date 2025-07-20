package com.example.englishmaster_be.domain.exam.answer.mapper;

import com.example.englishmaster_be.domain.exam.answer.dto.req.CreateAnswerReq;
import com.example.englishmaster_be.domain.exam.answer.dto.req.AnswerBasicReq;
import com.example.englishmaster_be.domain.exam.answer.dto.req.AnswerReq;
import com.example.englishmaster_be.domain.exam.answer.dto.req.EditAnswerReq;
import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerCorrectRes;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
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
    AnswerRes toAnswerResponse(AnswerEntity answer);

    List<AnswerRes> toAnswerResponseList(Collection<AnswerEntity> answerList);

    AnswerBasicReq toAnswerBasicRequest(AnswerRes answerResponse);

    List<AnswerBasicReq> toAnswerBasicRequestList(Collection<AnswerRes> answerResponseList);

    @Mapping(target = "answerId", ignore = true)
    void flowToAnswerEntity(AnswerReq answerRequest, @MappingTarget AnswerEntity answer);

    void flowToAnswerEntity(AnswerBasicReq answerBasicRequest, @MappingTarget AnswerEntity answer);

    @Mapping(target = "scoreAnswer", source = "question.questionScore")
    AnswerCorrectRes toCheckCorrectAnswerResponse(AnswerEntity answer);

    AnswerEntity toAnswerEntity(CreateAnswerReq answer1Request);

    default Set<AnswerEntity> toAnswerOfChildSet(QuestionEntity questionChild, List<CreateAnswerReq> answer1RequestSet, UserEntity userUpdate){

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

    AnswerEntity toAnswerEntity(EditAnswerReq answer1Request);

}
