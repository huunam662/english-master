package com.example.englishmaster_be.Model.Response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerMatchingBasicResponse {

    String contentLeft;

    String contentRight;

}
