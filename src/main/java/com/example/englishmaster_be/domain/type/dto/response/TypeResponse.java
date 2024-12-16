package com.example.englishmaster_be.domain.type.dto.response;

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

}
