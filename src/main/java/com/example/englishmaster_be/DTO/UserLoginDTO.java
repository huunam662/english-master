package com.example.englishmaster_be.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginDTO {

    @Schema(description = "Email of the User", example = "admin@meuenglish.com")
    String email;

    @Schema(description = "Password of the User", example = "admin@123")
    String password;

}
