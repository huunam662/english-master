package com.example.englishmaster_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginRequest {

    @Schema(description = "Email of the UserEntity", example = "admin@meuenglish.com")
    String email;

    @Schema(description = "Password of the UserEntity", example = "admin@123")
    String password;

}
