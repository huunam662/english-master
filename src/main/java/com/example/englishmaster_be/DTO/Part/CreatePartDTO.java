package com.example.englishmaster_be.DTO.Part;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreatePartDTO {
    private String partName;
    private String partDiscription;
    private String partType;
    private String contentType;
    private MultipartFile contentData;

    public CreatePartDTO() {
    }
}
