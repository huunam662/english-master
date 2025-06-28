package com.example.englishmaster_be.domain.pack.dto.response;

import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackPackTypeResponse extends PackResponse{

    PackTypeResponse packType;

}
