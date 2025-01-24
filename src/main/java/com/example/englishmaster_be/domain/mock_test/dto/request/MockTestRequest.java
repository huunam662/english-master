package com.example.englishmaster_be.domain.mock_test.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestRequest {

   UUID topicId;

   @Schema(example = "01:30:00")
   String workTimeTopic;

   @Schema(example = "00:30:00")
   String workTimeFinal;

   List<MockTestPartRequest> parts;

}
