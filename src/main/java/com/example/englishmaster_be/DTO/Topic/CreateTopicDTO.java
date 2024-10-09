package com.example.englishmaster_be.DTO.Topic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class CreateTopicDTO {
    private String topicName;
    private MultipartFile topicImage;
    private String topicDescription;
	private UUID topicPack;
    private String topicType;
    private String workTime;
	private int numberQuestion;
	private List<UUID> listPart;

	@Parameter(
			example = "2023-09-06T14:30:00",
			schema = @Schema(type = "string", format = "date-time", pattern = "yyyy-MM-dd'T'HH:mm:ss")
	)
    private LocalDateTime startTime;

	@Parameter(
			example = "2023-09-06T14:30:00",
			schema = @Schema(type = "string", format = "date-time", pattern = "yyyy-MM-dd'T'HH:mm:ss")
	)
    private LocalDateTime endTime;

    public CreateTopicDTO() {
    }

}
