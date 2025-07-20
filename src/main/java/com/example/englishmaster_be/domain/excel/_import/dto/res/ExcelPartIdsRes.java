package com.example.englishmaster_be.domain.excel._import.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ExcelPartIdsRes {

    private List<UUID> partIds;

    public ExcelPartIdsRes(List<UUID> partIds) {
        this.partIds = partIds;
    }
}
