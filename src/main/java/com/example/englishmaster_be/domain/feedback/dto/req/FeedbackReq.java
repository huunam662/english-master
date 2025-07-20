package com.example.englishmaster_be.domain.feedback.dto.req;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class FeedbackReq {

    @Hidden
    private UUID feedbackId;

    private String name;

    private String description;

    private String content;

}
