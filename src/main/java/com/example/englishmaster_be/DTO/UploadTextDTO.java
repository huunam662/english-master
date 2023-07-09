package com.example.englishmaster_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UploadTextDTO {
    private UUID id;
    private String contentType;
    private String contentData;
}
