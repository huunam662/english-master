package com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_submission;

import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.StatusSpeakingSubmission;
import com.example.englishmaster_be.domain.question.dto.response.QuestionSpeakingResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error.SpeakingErrorResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSubmissionResponse {

    UUID speakingSubmissionId;

    String audioUrl;

    String speakingText;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LevelSpeakerType levelSpeaker;

    String feedback;

    Float reachedPercent;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    StatusSpeakingSubmission statusSubmission;

    QuestionSpeakingResponse question;

    SpeakingErrorResponse speakingErrors;

}
