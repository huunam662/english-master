package com.example.englishmaster_be.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {
    private String code;
    private String newPass;
    private String confirmPass;

    public ChangePasswordDTO() {
    }

}
