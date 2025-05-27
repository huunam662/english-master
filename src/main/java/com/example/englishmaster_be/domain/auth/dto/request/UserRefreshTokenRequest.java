package com.example.englishmaster_be.domain.auth.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRefreshTokenRequest {

    UUID requestRefresh;

    String requestToken;

}
