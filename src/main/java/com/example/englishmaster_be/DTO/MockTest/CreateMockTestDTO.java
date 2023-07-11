package com.example.englishmaster_be.DTO.MockTest;


import java.sql.Time;
import java.util.UUID;


public class CreateMockTestDTO {
    private int score;
    private Time time;
    private UUID topic_id;

    public CreateMockTestDTO() {
    }

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public UUID getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(UUID topic_id) {
		this.topic_id = topic_id;
	}
    
    
}
