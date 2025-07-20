package com.example.englishmaster_be.domain.user.auth.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserLogoutReq {

    private UUID refreshToken;
    private String accessToken;
}
