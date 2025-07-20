package com.example.englishmaster_be.domain.exam.part.dto.req;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PartReq {

	@Hidden
	private UUID partId;

	private String partName;

	private String partDescription;

	private String partType;

	private String file;

}
