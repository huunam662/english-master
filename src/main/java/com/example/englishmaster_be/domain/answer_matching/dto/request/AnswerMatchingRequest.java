package com.example.englishmaster_be.domain.answer_matching.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerMatchingRequest {

    String contentLeft;

    String contentRight;
}
