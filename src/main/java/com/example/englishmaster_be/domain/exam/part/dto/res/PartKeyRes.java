package com.example.englishmaster_be.domain.exam.part.dto.res;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PartKeyRes {

    private UUID partId;

    public PartKeyRes(UUID partId) {
        this.partId = partId;
    }
}
