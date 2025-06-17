package com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_submission;

import com.example.englishmaster_be.domain.part.dto.response.Part1Response;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSubmissionResultResponse {

    Part1Response part;

    List<SpeakingSubmissionResponse> speakingSubmissions;
}
