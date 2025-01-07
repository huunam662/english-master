package com.example.englishmaster_be.domain.excel_fill.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelQuestionContentResponse {

    String questionContent;

    String audioPath;

    String imagePath;

    int totalScore;

}
