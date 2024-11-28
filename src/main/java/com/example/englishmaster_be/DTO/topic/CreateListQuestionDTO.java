package com.example.englishmaster_be.DTO.topic;

import com.example.englishmaster_be.DTO.question.CreateQuestionDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateListQuestionDTO {

    List<CreateQuestionDTO> listQuestion;

}
