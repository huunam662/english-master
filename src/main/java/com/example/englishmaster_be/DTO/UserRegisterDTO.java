package com.example.englishmaster_be.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String name;

    public UserRegisterDTO() {
    }

}
