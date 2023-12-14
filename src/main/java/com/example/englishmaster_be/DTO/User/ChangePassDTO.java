package com.example.englishmaster_be.DTO.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassDTO {
    private String oldPass;
    private String newPass;
    private String confirmPass;
}
