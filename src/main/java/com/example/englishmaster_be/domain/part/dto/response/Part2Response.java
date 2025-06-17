package com.example.englishmaster_be.domain.part.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Part2Response {

     UUID partId;

     String partName;

     String partType;

}
