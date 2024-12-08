package com.example.englishmaster_be.DTO.Part;


import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SavePartDTO {

	String partName;

	String partDescription;

	String partType;

}
