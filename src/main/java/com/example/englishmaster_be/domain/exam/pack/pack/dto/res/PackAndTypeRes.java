package com.example.englishmaster_be.domain.exam.pack.pack.dto.res;

import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PackAndTypeRes extends PackRes{

    private PackTypeRes packType;

}
