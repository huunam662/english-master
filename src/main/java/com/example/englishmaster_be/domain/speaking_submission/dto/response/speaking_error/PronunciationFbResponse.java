package com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PronunciationFbResponse {

    String word;

    String feedback;

    String wordRecommend;

    String pronunciation;

}
