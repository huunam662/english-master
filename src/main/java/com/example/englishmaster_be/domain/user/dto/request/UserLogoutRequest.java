package com.example.englishmaster_be.domain.user.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLogoutRequest {

    String accessToken;

    String refreshToken;

}
