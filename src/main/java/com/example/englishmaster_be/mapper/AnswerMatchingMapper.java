package com.example.englishmaster_be.mapper;


import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingEntity;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingResponse;
import com.example.englishmaster_be.model.user_answer_matching.UserAnswerMatchingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AnswerMatchingMapper {

    AnswerMatchingMapper INSTANCE = Mappers.getMapper(AnswerMatchingMapper.class);

    @Mapping(target = "question", expression = "java(QuestionMapper.INSTANCE.toQuestionResponse(answerMatching.getQuestion()))")
    AnswerMatchingResponse toAnswerMatchingResponse(AnswerMatchingEntity answerMatching);

    AnswerMatchingBasicResponse toAnswerMatchingBasicResponse(AnswerMatchingEntity answerMatchingEntity);

    AnswerMatchingBasicResponse toAnswerMatchingBasicResponse(UserAnswerMatchingEntity userAnswerMatchingEntity);

    List<AnswerMatchingBasicResponse> toAnswerMatchingBasicResponseList(List<AnswerMatchingEntity> answerMatchingEntityList);


}
