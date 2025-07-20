package com.example.englishmaster_be.domain.exam.pack.pack.dto.res;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class PackRes {

    private UUID packId;
    private String packName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
