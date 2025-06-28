package com.example.englishmaster_be.shared.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResponse {

    String url;

    String type;

    public FileResponse(String url, String type) {
        this.url = url;
        this.type = type;
    }
}
