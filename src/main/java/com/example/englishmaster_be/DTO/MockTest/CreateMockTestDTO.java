package com.example.englishmaster_be.DTO.MockTest;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.UUID;

@Getter
@Setter
public class CreateMockTestDTO {
    private int score;
    private Time time;
    private UUID topic_id;

    public CreateMockTestDTO() {
    }
}
