package com.example.englishmaster_be.domain.speaking_submission.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GrammarFluencyFbResponse {

    String word;

    String feedback;

    String wordRecommend;

}
