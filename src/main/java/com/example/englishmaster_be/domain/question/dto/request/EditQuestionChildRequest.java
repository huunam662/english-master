package com.example.englishmaster_be.domain.question.dto.request;

import com.example.englishmaster_be.domain.answer.dto.request.CreateAnswer1Request;
import com.example.englishmaster_be.domain.answer.dto.request.EditAnswer1Request;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditQuestionChildRequest {

    UUID questionChildId;

    String questionTitle;

    String questionContent;

    String contentAudio;

    String contentImage;

    Integer numberChoice;

    Integer questionScore;

    List<EditAnswer1Request> answers;
}
