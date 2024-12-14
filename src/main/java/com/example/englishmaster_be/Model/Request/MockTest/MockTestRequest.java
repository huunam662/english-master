package com.example.englishmaster_be.Model.Request.MockTest;


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
public class MockTestRequest {

    Integer score;

	Time time;

	UUID topic_id;

}
