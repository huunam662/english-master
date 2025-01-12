package com.example.englishmaster_be.domain.topic.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.TimeZoneSerializer;
import io.swagger.annotations.Example;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicRequest {

	@Hidden
	UUID topicId;

	UUID packId;

	String topicName;

	String topicDescription;

	String topicType;

	@Schema(example = "01:20:30")
	String workTime;

	Integer numberQuestion;

	String topicImage;

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
