package com.example.englishmaster_be.Mapper;


import com.example.englishmaster_be.Model.Response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.entity.AnswerMatchingEntity;
import com.example.englishmaster_be.Model.Response.AnswerMatchingResponse;
import com.example.englishmaster_be.entity.UserAnswerMatchingEntity;
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
