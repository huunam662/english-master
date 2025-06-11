package com.example.englishmaster_be.domain.excel.dto.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
public class ExcelPartIdsResponse {

    List<UUID> partIds;

}
