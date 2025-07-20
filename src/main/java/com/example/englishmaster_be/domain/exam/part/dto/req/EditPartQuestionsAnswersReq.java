package com.example.englishmaster_be.domain.exam.part.dto.req;

import com.example.englishmaster_be.domain.exam.question.dto.req.EditQuestionParentReq;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class EditPartQuestionsAnswersReq {

    @NotNull(message = "Part id is required.")
    private UUID partId;

    @NotNull(message = "Part name is required.")
    @NotBlank(message = "Part name is required.")
    private String partName;

    @NotNull(message = "Part type is required.")
    @NotBlank(message = "Part type is required.")
    private String partType;

    private List<EditQuestionParentReq> questionParents;

}
