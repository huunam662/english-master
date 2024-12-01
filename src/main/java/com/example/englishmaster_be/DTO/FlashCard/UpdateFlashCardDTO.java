package com.example.englishmaster_be.DTO.FlashCard;

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
public class UpdateFlashCardDTO extends CreateFlashCardDTO {

    @Schema(hidden = true)
    UUID flashCardId;

}
