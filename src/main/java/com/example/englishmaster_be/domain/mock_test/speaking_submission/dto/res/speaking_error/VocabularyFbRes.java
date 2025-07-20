package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyFbRes {

    String wordRecommend;

    String pronunciation;

}
