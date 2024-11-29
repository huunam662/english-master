package com.example.englishmaster_be.DTO.Feedback;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFeedbackDTO extends CreateFeedbackDTO{

    @Schema(hidden = true)
    UUID FeedbackID;

}
