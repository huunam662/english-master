package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission;

import com.example.englishmaster_be.domain.exam.part.dto.res.PartTopicRes;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSubmissionResultRes {

    PartTopicRes part;

    List<SpeakingSubmissionRes> speakingSubmissions;
}
