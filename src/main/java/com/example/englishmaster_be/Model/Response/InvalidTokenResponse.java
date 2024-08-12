package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.InvalidToken;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidTokenResponse {
    String token;
    LocalDateTime expireTime;

    public InvalidTokenResponse(InvalidToken invalidToken) {
        this.token = invalidToken.getToken();
        this.expireTime = invalidToken.getExpireTime();
    }
}
