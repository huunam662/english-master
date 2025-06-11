package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionPartResponse {

    TopicBasicResponse topic;

    PartBasicResponse part;

    List<QuestionResponse> questionParents;

}
