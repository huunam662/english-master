package com.example.englishmaster_be.domain.auth.dto.request;


import com.example.englishmaster_be.common.annotation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PasswordMatch(
        passwordFieldName = "newPassword",
        confirmPasswordFieldName = "confirmNewPassword",
        message = "Phải trùng khớp với mật khẩu mới đã nhập"
)
public class UserChangePwForgotRequest {


    @NotBlank(message = "Mã xác thực không được bỏ trống")
    @Schema(description = "Mã xác thực nhận từ mail", example = "475434")
    String otpCode;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Mật khẩu phải chứa ít nhất 8 và tối đa 20 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 chữ số, 1 kí tự đặc biệt"
    )
    @Schema(description = "Mật khẩu mới", example = "Nguyenvana@123456")
    String newPassword;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Mật khẩu phải chứa ít nhất 8 và tối đa 20 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 chữ số, 1 kí tự đặc biệt"
    )
    @Schema(description = "Xác nhận mật khẩu mới", example = "Nguyenvana@123456")
    String confirmNewPassword;

}
