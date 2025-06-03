package com.example.englishmaster_be.domain.question.dto.request;

import com.example.englishmaster_be.domain.answer.dto.request.CreateAnswer1Request;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateQuestionChildRequest {

    String questionTitle;

    String questionContent;

    String contentAudio;

    String contentImage;

    Integer numberChoice;

    Integer questionScore;

    List<CreateAnswer1Request> answers;
}
