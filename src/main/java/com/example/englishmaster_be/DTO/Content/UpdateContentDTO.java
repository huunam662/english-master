package com.example.englishmaster_be.DTO.Content;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateContentDTO extends CreateContentDTO{


    UUID questionId;

    @Schema(hidden = true)
    MultipartFile file;

}
