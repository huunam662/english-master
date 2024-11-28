package com.example.englishmaster_be.DTO;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisterDTO {

	@Email(message = "Email is not in correct format.")
	String email;

	@NotBlank(message = "Please enter your password.")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Password must contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character, and no spaces"
	)
	String password;

	@NotBlank(message = "Please confirm your password.")
	String confirmPassword;

	@NotBlank(message = "Please enter your name.")
	String name;

	@AssertTrue(message = "Password and confirm password do not match.")
	boolean isPasswordMatching() {
		return Objects.nonNull(password)
				&& Objects.nonNull(confirmPassword)
				&& password.equals(confirmPassword);
	}
}
