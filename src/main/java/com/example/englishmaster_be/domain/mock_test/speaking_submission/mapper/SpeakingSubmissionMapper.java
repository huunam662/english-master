package com.example.englishmaster_be.domain.mock_test.speaking_submission.mapper;

import com.example.englishmaster_be.domain.exam.question.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission.SpeakingSubmissionRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;
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
    SpeakingSubmissionRes toSpeakingSubmissionResponse(SpeakingSubmissionEntity speakingSubmission);

}
