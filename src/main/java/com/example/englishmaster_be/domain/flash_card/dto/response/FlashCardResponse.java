package com.example.englishmaster_be.domain.flash_card.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardResponse {

    UUID flashCardId;

    String flashCardTitle;

    String flashCardImage;

    String flashCardDescription;

    Integer countOfWords;

}
