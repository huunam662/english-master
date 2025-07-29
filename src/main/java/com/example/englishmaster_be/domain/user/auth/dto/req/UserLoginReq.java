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
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Schema(example = "admin@123")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Mật khẩu phải chứa ít nhất 8 và tối đa 20 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 chữ số, 1 kí tự đặc biệt"
    )
    private String password;

}
