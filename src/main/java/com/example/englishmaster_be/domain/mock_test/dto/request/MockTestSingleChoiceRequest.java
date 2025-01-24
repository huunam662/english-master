package com.example.englishmaster_be.domain.mock_test.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestSingleChoiceRequest extends MockTestQuestionChildrenRequest{

    UUID answerChoiceId;

}
