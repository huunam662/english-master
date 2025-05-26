package com.example.englishmaster_be.domain.auth.dto.response;


import com.example.englishmaster_be.common.constant.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAuthProfileResponse {

    String name;

    String email;

    String avatar;

    Role role;

}
