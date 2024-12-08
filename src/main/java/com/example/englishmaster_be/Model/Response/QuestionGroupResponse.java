package com.example.englishmaster_be.Model.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionGroupResponse {

    UUID questionId;

    UUID partId;

    UUID questionGroupId;

}
