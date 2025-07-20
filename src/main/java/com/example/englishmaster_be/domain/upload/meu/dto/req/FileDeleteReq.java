package com.example.englishmaster_be.domain.upload.meu.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class FileDeleteReq {

    private String filepath;

    public FileDeleteReq(String filepath) {
        this.filepath = filepath;
    }
}
