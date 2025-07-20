package com.example.englishmaster_be.domain.exam.pack.type.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PackTypePageRes {
    private PackTypeFullRes packType;
    private Long countPacks;
}
