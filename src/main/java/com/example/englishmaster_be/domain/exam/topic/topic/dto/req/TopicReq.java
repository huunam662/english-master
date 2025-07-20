package com.example.englishmaster_be.domain.exam.topic.topic.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicReq {

	UUID packId;

	String topicName;

	String topicDescription;

	String topicType;

	@Schema(example = "01:20:30")
	String workTime;

	String topicImage;

	String topicAudio;

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
