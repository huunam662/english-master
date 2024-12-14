package com.example.englishmaster_be.Model.Request.FlashCard;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardWordRequest {

    @Hidden
    UUID flashCardId;

    @Hidden
    UUID flashCardWordId;

    String word;

    String define;

    String type;

    String spelling;

    String example;

    String note;

    MultipartFile image;

}
