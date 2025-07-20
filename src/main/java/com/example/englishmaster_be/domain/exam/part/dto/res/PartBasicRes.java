package com.example.englishmaster_be.domain.exam.part.dto.res;


import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PartBasicRes {

     private UUID partId;
     private String partName;
     private String partType;

}
