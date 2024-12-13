package com.example.englishmaster_be.Model.Request;

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
public class UserRegisterRequest {

	@Email(message = "Email is not in correct format.")
	String email;

	@NotBlank(message = "Please enter your password.")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Password must contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character, and no spaces"
	)
	String password;

	@NotBlank(message = "Please confirm your password.")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Password must contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character, and no spaces"
	)
	String confirmPassword;

	@NotBlank(message = "Please enter your name.")
	String name;

}
