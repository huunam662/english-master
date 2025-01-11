package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionPartResponse {

    UUID partId;

    String partName;

    ExcelQuestionResponse questionParent;

}
