package com.example.englishmaster_be.model.request;

import com.example.englishmaster_be.common.annotation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "Email người dùng", example = "nguyenvana123@gmail.com")
	String email;

	@NotBlank(message = "Mật khẩu không được bỏ trống")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Mật khẩu phải chứa ít nhất 8 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 kí tự đặc biệt"
	)
	@Schema(description = "Mật khẩu nguời dùng", example = "Nguyenvana@123")
	String password;

	@NotBlank(message = "Mật khẩu không được bỏ trống")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Mật khẩu phải chứa ít nhất 8 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 kí tự đặc biệt"
	)
	@Schema(description = "Xác nhận mật khẩu nguời dùng", example = "Nguyenvana@123")
	String confirmPassword;

	@NotBlank(message = "Tên không được bỏ trống")
	@Schema(description = "Tên nguời dùng", example = "Nguyễn Văn A")
	String name;

}
