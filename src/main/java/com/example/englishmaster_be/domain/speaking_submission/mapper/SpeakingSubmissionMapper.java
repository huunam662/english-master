package com.example.englishmaster_be.domain.speaking_submission.mapper;

import com.example.englishmaster_be.domain.question.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_submission.SpeakingSubmissionResponse;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {QuestionMapper.class}, builder = @Builder(disableBuilder = true))
public interface SpeakingSubmissionMapper {

    SpeakingSubmissionMapper INSTANCE = Mappers.getMapper(SpeakingSubmissionMapper.class);

    @Mapping(target = "speakingSubmissionId", source = "id")
    @Mapping(target = "statusSubmission", source = "status")
    @Mapping(target = "question", expression = "java(QuestionMapper.INSTANCE.toQuestionSpeakingResponse(speakingSubmission.getQuestion()))")
    @Mapping(target = "speakingErrors", ignore = true)
    SpeakingSubmissionResponse toSpeakingSubmissionResponse(SpeakingSubmissionEntity speakingSubmission);

}
