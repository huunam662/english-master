package com.example.englishmaster_be.Model.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScoreAnswerResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AnswerMatchingBasicResponse> answers;

    Integer scoreAnswer = 0;

}
