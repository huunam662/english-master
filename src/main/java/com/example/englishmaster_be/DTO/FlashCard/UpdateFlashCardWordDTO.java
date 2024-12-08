package com.example.englishmaster_be.DTO.FlashCard;

import io.swagger.v3.oas.annotations.Hidden;
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
public class UpdateFlashCardWordDTO extends SaveFlashCardWordDTO {

    @Hidden
    UUID flashCardWordId;
}
