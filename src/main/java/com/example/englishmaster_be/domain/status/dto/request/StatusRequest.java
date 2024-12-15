package com.example.englishmaster_be.domain.status.dto.request;

import com.example.englishmaster_be.common.constant.StatusEnum;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    StatusEnum statusName;

    Boolean flag;

}
