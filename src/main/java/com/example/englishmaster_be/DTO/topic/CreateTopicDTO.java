package com.example.englishmaster_be.DTO.topic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTopicDTO {

	UUID topicPack;

	String topicName;

	String topicDescription;

	String topicType;

	String workTime;

	int numberQuestion;

	MultipartFile topicImage;

	List<UUID> listPart;

	@Parameter(
			example = "2023-09-06T14:30:00",
			schema = @Schema(
					type = "string",
					format = "date-time",
					pattern = "yyyy-MM-dd'T'HH:mm:ss"
			)
	)
    LocalDateTime startTime;

	@Parameter(
			example = "2023-09-06T14:30:00",
			schema = @Schema(
					type = "string",
					format = "date-time",
					pattern = "yyyy-MM-dd'T'HH:mm:ss"
			)
	)
    LocalDateTime endTime;

}
