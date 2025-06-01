package com.example.englishmaster_be.domain.part.dto.request;

import com.example.englishmaster_be.domain.question.dto.request.QuestionParentRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartQuestionsAnswersRequest {

    @NotNull(message = "Part name is required.")
    @NotBlank(message = "Part name is required.")
    String partName;

    @NotNull(message = "Part type is required.")
    @NotBlank(message = "Part type is required.")
    String partType;

    List<QuestionParentRequest> questionParents;

}
