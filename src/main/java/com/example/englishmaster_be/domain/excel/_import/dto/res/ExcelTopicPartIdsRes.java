package com.example.englishmaster_be.domain.excel._import.dto.res;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ExcelTopicPartIdsRes extends ExcelPartIdsRes {

    private UUID topicId;

}
