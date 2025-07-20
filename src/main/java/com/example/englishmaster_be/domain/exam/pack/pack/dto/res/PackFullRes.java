package com.example.englishmaster_be.domain.exam.pack.pack.dto.res;

import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PackFullRes extends PackRes {

    private UserRes userCreate;
    private UserRes userUpdate;
    private PackTypeRes packType;

}
