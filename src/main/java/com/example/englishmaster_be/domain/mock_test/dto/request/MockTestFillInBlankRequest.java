package com.example.englishmaster_be.domain.mock_test.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestFillInBlankRequest extends MockTestQuestionChildrenRequest{

    String[] answersFill;

}
