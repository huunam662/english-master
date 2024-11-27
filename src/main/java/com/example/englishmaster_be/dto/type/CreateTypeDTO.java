package com.example.englishmaster_be.dto.type;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTypeDTO {

    String nameSlug;

    String typeName;

}
