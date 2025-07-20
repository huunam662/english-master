package com.example.englishmaster_be.domain.exam.part.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PartAndTotalQuestionRes {

    private UUID partId;

    private String partName;

    private String partType;

    private String partDescription;

    private Integer totalQuestion;

}
