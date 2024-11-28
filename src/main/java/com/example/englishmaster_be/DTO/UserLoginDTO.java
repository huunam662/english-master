package com.example.englishmaster_be.DTO;


import io.swagger.v3.oas.annotations.media.Schema;

public class UserLoginDTO {

	@Schema(description = "Email of the user", example = "admin@meuenglish.com")
    private String email;
	@Schema(description = "Password of the user", example = "admin@123")
    private String password;

    public UserLoginDTO() {
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    
}
