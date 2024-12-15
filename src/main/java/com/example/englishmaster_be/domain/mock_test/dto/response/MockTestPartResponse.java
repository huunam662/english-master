package com.example.englishmaster_be.domain.mock_test.dto.response;


import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestPartResponse {

     UUID topicId;

     String topicName;

     String topicTime;

     List<PartResponse> parts;

}
