package com.example.englishmaster_be.domain.exam.pack.type.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UpdatePackTypeReq extends CreatePackTypeReq {

    @NotNull(message = "Pack type id is required.")
    UUID packTypeId;

}
