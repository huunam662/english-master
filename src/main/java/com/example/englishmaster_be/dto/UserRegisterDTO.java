package com.example.englishmaster_be.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDTO {

	@Email(message = "Email is not in correct format.")
	private String email;

	@NotBlank(message = "Please enter your password.")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
			message = "Password must contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character, and no spaces")
	private String password;

	@NotBlank(message = "Please confirm your password.")
	private String confirmPassword;

	@NotBlank(message = "Please enter your name.")
	private String name;
	@AssertTrue(message = "Password and confirm password do not match.")
	private boolean isPasswordMatching() {
		if (password == null || confirmPassword == null) {
			return false;
		}
		return password.equals(confirmPassword);
	}
}
