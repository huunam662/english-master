package com.example.englishmaster_be.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {
    private String email;
    private String password;

    public UserLoginDTO() {
    }

}
