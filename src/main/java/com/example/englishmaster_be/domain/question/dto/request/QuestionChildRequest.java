package com.example.englishmaster_be.domain.question.dto.request;

import com.example.englishmaster_be.domain.answer.dto.request.Answer1Request;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionChildRequest {

    String questionTitle;

    String questionContent;

    String contentAudio;

    String contentImage;

    Integer numberChoice;

    Integer questionScore;

    List<Answer1Request> answers;
}
