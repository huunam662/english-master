package com.example.englishmaster_be.DTO.Part;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePartDTO {

	String partName;

	String partDiscription;

	String partType;

}
