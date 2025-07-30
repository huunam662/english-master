package com.example.englishmaster_be.domain.user.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class UserLoginReq {

    @Schema(example = "admin@meuenglish.com")
    private String email;

    @Schema(example = "admin@123")
    private String password;

}
