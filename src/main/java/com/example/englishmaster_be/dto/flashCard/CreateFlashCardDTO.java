package com.example.englishmaster_be.dto.flashCard;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFlashCardDTO {

    String flashCardTitle;

    String flashCardDescription;

    MultipartFile flashCardImage;

}
