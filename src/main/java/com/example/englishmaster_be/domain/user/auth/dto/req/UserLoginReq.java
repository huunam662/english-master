package com.example.englishmaster_be.domain.user.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class UserLoginReq {

    @Schema(description = "Email of the UserEntity", example = "admin@meuenglish.com")
    private String email;

    @Schema(description = "Password of the UserEntity", example = "admin@123")
    private String password;

}
