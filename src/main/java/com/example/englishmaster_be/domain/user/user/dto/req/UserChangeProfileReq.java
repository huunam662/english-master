package com.example.englishmaster_be.domain.user.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class UserChangeProfileReq {

    @Schema(description = "Name for the user", example = "Nguyen Van A")
    private String name;

    @Schema(description = "Address for the user", example = "123 Street")
    private String address;

    @Schema(description = "Phone number for the user", example = "0123456789")
    private String phone;

    private String avatar;

}
