package com.example.englishmaster_be.DTO.Topic;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class CreateTopicDTO {
    private String topicName;
    private MultipartFile topicImage;
    private String topicDiscription;
    private String topicType;
    private String workTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean enable;

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

	public String getTopicDiscription() {
		return topicDiscription;
	}

	public void setTopicDiscription(String topicDiscription) {
		this.topicDiscription = topicDiscription;
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

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
    
    
}
