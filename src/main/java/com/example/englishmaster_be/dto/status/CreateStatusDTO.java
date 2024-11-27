package com.example.englishmaster_be.dto.status;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStatusDTO {

    UUID typeId;

    String statusName;

    boolean flag;

}
