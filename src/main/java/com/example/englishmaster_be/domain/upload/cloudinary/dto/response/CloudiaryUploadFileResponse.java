package com.example.englishmaster_be.domain.upload.cloudinary.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudiaryUploadFileResponse {

	String url;

	String type;

}