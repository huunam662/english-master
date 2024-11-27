package com.example.englishmaster_be.dto.part;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePartDTO {

    String partName;

    String partDiscription;

    String partType;

}
