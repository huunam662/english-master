package com.example.englishmaster_be.dto.content;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateContentDTO {
    private UUID contentId;
    private String contentType;
    private String contentData;
    private UUID topicId;
    private String code;
}
