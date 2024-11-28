package com.example.englishmaster_be.DTO.Topic;

import com.example.englishmaster_be.DTO.Question.CreateQuestionByExcelFileDTO;
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
