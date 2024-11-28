package com.example.englishmaster_be.DTO.MockTest;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMockTestDTO {

    int score;

	Time time;

	UUID topic_id;

}
