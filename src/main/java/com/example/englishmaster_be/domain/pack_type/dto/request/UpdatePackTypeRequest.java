package com.example.englishmaster_be.domain.pack_type.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePackTypeRequest extends CreatePackTypeRequest{

    @NotNull(message = "Pack type id is required.")
    UUID packTypeId;

}
