package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Type;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeResponse {

    UUID typeId;

    String typeName;

    String nameSlug;

    public TypeResponse(Type type) {

        if(Objects.isNull(type)) return;

        this.typeId = type.getTypeId();
        this.typeName = type.getTypeName();
        this.nameSlug = type.getNameSlug();
    }
}
