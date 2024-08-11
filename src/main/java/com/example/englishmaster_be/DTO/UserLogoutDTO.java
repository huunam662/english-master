package com.example.englishmaster_be.DTO;

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
