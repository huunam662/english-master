package com.example.englishmaster_be.domain.question.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditQuestionParentRequest {

    UUID questionParentId;

    String questionTitle;

    String questionContent;

    String contentAudio;

    String contentImage;

    List<EditQuestionChildRequest> questionChilds;

}
