package com.example.englishmaster_be.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserLogoutDTO {
    String accessToken;
    String refreshToken;
}
