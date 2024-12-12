package com.example.englishmaster_be.Model.Request.User;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

    String oldPass;

    String newPass;

    String confirmPass;

}
