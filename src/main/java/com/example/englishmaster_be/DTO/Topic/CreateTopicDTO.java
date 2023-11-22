package com.example.englishmaster_be.DTO.Topic;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public MultipartFile getTopicImage() {
		return topicImage;
	}

	public void setTopicImage(MultipartFile topicImage) {
		this.topicImage = topicImage;
	}

	public String getTopicDescription() {
		return topicDescription;
	}

	public void setTopicDescription(String topicDescription) {
		this.topicDescription = topicDescription;
	}

	public String getTopicType() {
		return topicType;
	}

	public void setTopicType(String topicType) {
		this.topicType = topicType;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public UUID getTopicPack() {
		return topicPack;
	}

	public void setTopicPack(UUID topicPack) {
		this.topicPack = topicPack;
	}

	public int getNumberQuestion() {
		return numberQuestion;
	}

	public void setNumberQuestion(int numberQuestion) {
		this.numberQuestion = numberQuestion;
	}

	public List<UUID> getListPart() {
		return listPart;
	}

	public void setListPart(List<UUID> listPart) {
		this.listPart = listPart;
	}
}
