package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.common.constaint.StatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusResponse {

    UUID statusId;

    @Enumerated(EnumType.STRING)
    StatusEnum statusName;

    Boolean flag;

    TypeResponse type;

}
