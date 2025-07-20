package com.example.englishmaster_be.domain.user.auth.dto.res;


import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserAuthRes {

    private String accessToken;
    private UUID refreshToken;
    private UserAuthProfileRes userAuth;

}
