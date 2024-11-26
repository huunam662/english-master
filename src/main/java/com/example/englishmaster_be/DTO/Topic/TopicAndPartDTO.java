package com.example.englishmaster_be.DTO.Topic;


import java.util.UUID;

public class TopicAndPartDTO {
    private UUID topicId;
    private UUID partId;
	public UUID getTopicId() {
		return topicId;
	}
	public void setTopicId(UUID topicId) {
		this.topicId = topicId;
	}
	public UUID getPartId() {
		return partId;
	}
	public void setPartId(UUID partId) {
		this.partId = partId;
	}
    
    
}
