package com.example.englishmaster_be.dto.type;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTypeDTO {
    String nameSlug;
    String typeName;

    public CreateTypeDTO(String nameSlug, String typeName) {
        this.nameSlug = nameSlug;
        this.typeName = typeName;
    }

    public CreateTypeDTO() {
    }
}
