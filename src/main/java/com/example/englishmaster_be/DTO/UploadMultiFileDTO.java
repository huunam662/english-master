package com.example.englishmaster_be.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
public class UploadMultiFileDTO {
    private UUID id;
    private MultipartFile[] contentData;
}
