package com.example.englishmaster_be.domain.user.auth.dto.res;


import com.example.englishmaster_be.common.constant.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class UserAuthProfileRes {

    private String name;
    private String email;
    private String avatar;
    private Role role;

}
