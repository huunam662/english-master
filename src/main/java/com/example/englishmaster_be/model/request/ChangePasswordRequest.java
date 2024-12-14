package com.example.englishmaster_be.model.request;


import com.example.englishmaster_be.common.annotation.PasswordMatch;
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
public class ChangePasswordRequest {

    @NotBlank(message = "Mã xác thực không được bỏ trống")
    String code;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Mật khẩu phải chứa ít nhất 8 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 kí tự đặc biệt"
    )
    String oldPassword;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Mật khẩu phải chứa ít nhất 8 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 kí tự đặc biệt"
    )
    String newPassword;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Mật khẩu phải chứa ít nhất 8 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 kí tự đặc biệt"
    )
    String confirmNewPassword;

}
