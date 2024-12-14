package com.example.englishmaster_be.model.response.excel;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListQuestionByExcelFileResponse {

    List<QuestionByExcelFileResponse> questions;

}
