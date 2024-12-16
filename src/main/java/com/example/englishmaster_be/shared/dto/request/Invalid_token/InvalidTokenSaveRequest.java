package com.example.englishmaster_be.shared.dto.request.Invalid_token;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidTokenSaveRequest {

    String token;

    Date expireTime;
}
