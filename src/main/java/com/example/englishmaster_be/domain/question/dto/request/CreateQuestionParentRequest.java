package com.example.englishmaster_be.domain.question.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateQuestionParentRequest {

    String questionTitle;

    String questionContent;

    String contentAudio;

    String contentImage;

    List<CreateQuestionChildRequest> questionChilds;

}
