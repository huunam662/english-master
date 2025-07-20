package com.example.englishmaster_be.domain.exam.pack.type.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
public class CreatePackTypeReq {

    @NotNull(message = "Name of pack type is required.")
    @NotBlank(message = "Name of pack type is required.")
    private String name;

}
