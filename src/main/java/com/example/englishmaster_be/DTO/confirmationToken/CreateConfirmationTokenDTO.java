package com.example.englishmaster_be.DTO.confirmationToken;

import com.example.englishmaster_be.Model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateConfirmationTokenDTO {

    User user;

}
