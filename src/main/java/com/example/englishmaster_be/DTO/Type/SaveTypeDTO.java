package com.example.englishmaster_be.DTO.Type;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveTypeDTO {

    String nameSlug;

    String typeName;

}
