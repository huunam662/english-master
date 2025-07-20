package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission;

import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.StatusSpeakingSubmission;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error.SpeakingErrorRes;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionSpeakingRes;
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
public class SpeakingSubmissionRes {

    UUID speakingSubmissionId;

    String audioUrl;

    String speakingText;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LevelSpeakerType levelSpeaker;

    String feedback;

    Float reachedPercent;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    StatusSpeakingSubmission statusSubmission;

    QuestionSpeakingRes question;

    SpeakingErrorRes speakingErrors;

}
