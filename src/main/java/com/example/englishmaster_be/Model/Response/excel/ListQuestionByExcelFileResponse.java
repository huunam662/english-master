package com.example.englishmaster_be.Model.Response.excel;

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

    @Override
    public String toString() {
        return "CreateListQuestionByExcelFileDTO{" +
                "questions=" + questions +
                '}';
    }
}
