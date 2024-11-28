package com.example.englishmaster_be.DTO.topic;

import com.example.englishmaster_be.DTO.question.CreateQuestionByExcelFileDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateListQuestionByExcelFileDTO {

    List<CreateQuestionByExcelFileDTO> questions;

    @Override
    public String toString() {
        return "CreateListQuestionByExcelFileDTO{" +
                "questions=" + questions +
                '}';
    }
}
