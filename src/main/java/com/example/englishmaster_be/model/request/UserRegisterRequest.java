package com.example.englishmaster_be.model.request;

import com.example.englishmaster_be.common.annotation.PasswordMatch;
import jakarta.validation.constraints.Email;
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
		passwordFieldName = "password",
		confirmPasswordFieldName = "confirmPassword",
		message = "Phải trùng khớp với mật khẩu đã nhập"
)
public class UserRegisterRequest {

	@Email(message = "Email không đúng định dạng")
	String email;

	@NotBlank(message = "Mật khẩu không được bỏ trống")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Mật khẩu phải chứa ít nhất 8 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 kí tự đặc biệt"
	)
	String password;

	@NotBlank(message = "Mật khẩu không được bỏ trống")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Mật khẩu phải chứa ít nhất 8 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 kí tự đặc biệt"
	)
	String confirmPassword;

	@NotBlank(message = "Tên không được bỏ trống")
	String name;

}
