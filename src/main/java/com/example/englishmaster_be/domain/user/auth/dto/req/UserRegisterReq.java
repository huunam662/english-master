package com.example.englishmaster_be.domain.user.auth.dto.req;

import com.example.englishmaster_be.common.annotation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@PasswordMatch(
		passwordFieldName = "password",
		confirmPasswordFieldName = "confirmPassword",
		message = "Phải trùng khớp với mật khẩu đã nhập"
)
public class UserRegisterReq {

	@Email(message = "Email không đúng định dạng")
	@Schema(description = "Email người dùng", example = "nguyenvana123@gmail.com")
	private String email;

	@NotBlank(message = "Mật khẩu không được bỏ trống")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Mật khẩu phải chứa ít nhất 8 và tối đa 20 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 chữ số, 1 kí tự đặc biệt"
	)
	@Schema(description = "Mật khẩu nguời dùng", example = "Nguyenvana@123")
	private String password;

	@NotBlank(message = "Mật khẩu không được bỏ trống")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Mật khẩu phải chứa ít nhất 8 và tối đa 20 kí tự không bao gồm khoảng trắng và ít nhất 1 kí tự hoa, 1 kí tự thường, 1 chữ số, 1 kí tự đặc biệt"
	)
	@Schema(description = "Xác nhận mật khẩu nguời dùng", example = "Nguyenvana@123")
	private String confirmPassword;

	@NotBlank(message = "Tên không được bỏ trống")
	@Schema(description = "Tên nguời dùng", example = "Nguyễn Văn A")
	private String name;

}
