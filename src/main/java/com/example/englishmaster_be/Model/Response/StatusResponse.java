package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Common.enums.StatusEnum;
import com.example.englishmaster_be.entity.StatusEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
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
