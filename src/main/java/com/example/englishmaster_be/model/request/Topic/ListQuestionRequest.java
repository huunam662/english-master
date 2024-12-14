package com.example.englishmaster_be.model.request.Topic;

import com.example.englishmaster_be.model.request.Question.QuestionRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListQuestionRequest {

    List<QuestionRequest> listQuestion;

}
