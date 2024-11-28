package com.example.englishmaster_be.DTO.flashCard;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFlashCardWordDTO {

    String word;

    String define;

    String type;

    String spelling;

    String example;

    String note;

    MultipartFile image;

}
