package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.entity.TypeEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
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

}
