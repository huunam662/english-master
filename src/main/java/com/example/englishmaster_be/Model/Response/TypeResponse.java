package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Type;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
        this.typeId = type.getTypeId();
        this.typeName = type.getTypeName();
        this.nameSlug = type.getNameSlug();
    }
}
