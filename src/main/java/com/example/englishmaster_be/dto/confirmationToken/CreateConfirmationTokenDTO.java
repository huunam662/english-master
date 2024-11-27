package com.example.englishmaster_be.dto.confirmationToken;

import com.example.englishmaster_be.model.User;
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
