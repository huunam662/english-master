package com.example.englishmaster_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserChangeProfileReq {

    @Schema(description = "Name for the user", example = "Nguyen Van A")
    String name;

    @Schema(description = "Address for the user", example = "123 Street")
    String address;

    @Schema(description = "Phone number for the user", example = "0123456789")
    String phone;

    String avatar;

}
