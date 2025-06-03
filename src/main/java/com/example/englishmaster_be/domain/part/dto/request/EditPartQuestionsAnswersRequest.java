package com.example.englishmaster_be.domain.part.dto.request;

import com.example.englishmaster_be.domain.question.dto.request.CreateQuestionParentRequest;
import com.example.englishmaster_be.domain.question.dto.request.EditQuestionParentRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EditPartQuestionsAnswersRequest {

    @NotNull(message = "Part id is required.")
    UUID partId;

    @NotNull(message = "Part name is required.")
    @NotBlank(message = "Part name is required.")
    String partName;

    @NotNull(message = "Part type is required.")
    @NotBlank(message = "Part type is required.")
    String partType;

    List<EditQuestionParentRequest> questionParents;

}
