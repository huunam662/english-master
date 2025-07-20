package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission;

import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error.BotFeedbackErrorRes;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotFeedbackRes {

    String feedback;

    Float reachedPercent;

    BotFeedbackErrorRes errors;

}
