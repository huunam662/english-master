package com.example.englishmaster_be.DTO.FlashCard;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveFlashCardDTO {

    String flashCardTitle;

    String flashCardDescription;

    MultipartFile flashCardImage;

}
