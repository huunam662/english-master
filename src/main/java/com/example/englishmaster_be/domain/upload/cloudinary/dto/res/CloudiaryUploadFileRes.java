package com.example.englishmaster_be.domain.upload.cloudinary.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class CloudiaryUploadFileRes {

	private String url;

	private String type;

}