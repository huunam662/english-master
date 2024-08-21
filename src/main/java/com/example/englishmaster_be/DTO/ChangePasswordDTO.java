package com.example.englishmaster_be.DTO;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordDTO {
    private String code;
    private String newPass;
    private String confirmPass;
}
