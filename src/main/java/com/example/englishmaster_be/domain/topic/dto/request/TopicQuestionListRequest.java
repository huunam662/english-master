package com.example.englishmaster_be.domain.topic.dto.request;

import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicQuestionListRequest {

    List<QuestionRequest> listQuestion;

}
