package com.example.englishmaster_be.domain.pack_type.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePackTypeRequest {

    @NotNull(message = "Name of pack type is required.")
    @NotBlank(message = "Name of pack type is required.")
    String name;

}
