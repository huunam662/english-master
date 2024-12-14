package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.entity.DetailMockTestEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailMockTestResponse {

    UUID detailMockTestId;

    UUID answerId;

    String answerContent;

    Boolean correctAnswer;

    Integer scoreAnswer;

}
