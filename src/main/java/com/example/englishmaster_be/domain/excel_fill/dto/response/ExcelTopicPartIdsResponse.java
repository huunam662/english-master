package com.example.englishmaster_be.domain.excel_fill.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ExcelTopicPartIdsResponse extends ExcelPartIdsResponse{

    UUID topicId;

}
