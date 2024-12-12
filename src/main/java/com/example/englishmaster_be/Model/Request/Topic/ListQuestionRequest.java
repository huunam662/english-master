package com.example.englishmaster_be.Model.Request.Topic;

import com.example.englishmaster_be.Model.Request.Question.QuestionRequest;
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
