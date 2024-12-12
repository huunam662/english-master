package com.example.englishmaster_be.Model.Request.Answer;

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
