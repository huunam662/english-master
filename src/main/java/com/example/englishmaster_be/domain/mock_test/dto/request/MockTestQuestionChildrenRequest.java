package com.example.englishmaster_be.domain.mock_test.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MockTestSingleChoiceRequest.class),
        @JsonSubTypes.Type(value = MockTestFillInBlankRequest.class),
        @JsonSubTypes.Type(value = MockTestWordsMatchingRequest.class)
})
public class MockTestQuestionChildrenRequest {

    UUID questionChildrenId;

}
