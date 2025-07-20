package com.example.englishmaster_be.domain.exam.pack.pack.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PackPageRes {

    PackFullRes pack;
    Long countTopics;

}
