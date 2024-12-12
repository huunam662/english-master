package com.example.englishmaster_be.Model.Request;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordDTO {

    String code;

    String newPass;

    String confirmPass;

}
