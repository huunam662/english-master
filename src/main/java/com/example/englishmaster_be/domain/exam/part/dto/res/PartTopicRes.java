package com.example.englishmaster_be.domain.exam.part.dto.res;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PartTopicRes {

    private UUID partId;

    private String partName;

    private String partType;

    private String partDescription;

    private UUID topicId;
}
