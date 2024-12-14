package com.example.englishmaster_be.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse extends QuestionBasicResponse{

    @JsonInclude(JsonInclude.Include.NON_NULL)
    QuestionBasicResponse questionGroupParent;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<QuestionBasicResponse> questionGroupChildren;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AnswerResponse> listAnswer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ContentResponse> contentList;

}
