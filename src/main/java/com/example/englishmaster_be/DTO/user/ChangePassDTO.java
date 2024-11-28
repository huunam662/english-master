package com.example.englishmaster_be.DTO.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePassDTO {

    String oldPass;

    String newPass;

    String confirmPass;

}
