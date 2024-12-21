package com.example.englishmaster_be.domain.file_storage.dto.response;

import com.example.englishmaster_be.common.constant.ResourceTypeLoadEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceResponse {

    String fileName;

    String contentType;

    Long contentLength;

    ResourceTypeLoadEnum typeLoad;

    Resource resource;

}
