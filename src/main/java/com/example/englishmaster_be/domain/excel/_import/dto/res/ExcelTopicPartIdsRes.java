package com.example.englishmaster_be.domain.excel._import.dto.res;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ExcelTopicPartIdsRes extends ExcelPartIdsRes {

    UUID topicId;

}
