package com.example.englishmaster_be.domain.user.auth.dto.req;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserRefreshTokenReq {

    private UUID requestRefresh;

    private String requestToken;

}
