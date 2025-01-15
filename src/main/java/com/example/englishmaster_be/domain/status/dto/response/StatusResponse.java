package com.example.englishmaster_be.domain.status.dto.response;

import com.example.englishmaster_be.domain.type.dto.response.TypeResponse;
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

    String statusName;

    Boolean flag;

    TypeResponse type;

}
