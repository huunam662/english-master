package com.example.englishmaster_be.domain.user.user.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserBasicRes {

    private UUID userId;

    private String name;

}
