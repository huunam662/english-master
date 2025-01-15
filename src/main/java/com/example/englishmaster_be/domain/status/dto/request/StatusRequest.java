package com.example.englishmaster_be.domain.status.dto.request;

import io.swagger.v3.oas.annotations.Hidden;
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
public class StatusRequest {

    @Hidden
    UUID statusId;

    UUID typeId;

    String statusName;

    Boolean flag;

}
