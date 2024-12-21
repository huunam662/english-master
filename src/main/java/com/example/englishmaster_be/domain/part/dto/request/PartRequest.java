package com.example.englishmaster_be.domain.part.dto.request;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartRequest {

	@Hidden
	UUID partId;

	String partName;

	String partDescription;

	String partType;

	String file;

}
