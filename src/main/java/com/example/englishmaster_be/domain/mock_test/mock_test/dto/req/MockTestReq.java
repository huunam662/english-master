package com.example.englishmaster_be.domain.mock_test.mock_test.dto.req;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MockTestReq {

   private UUID topicId;

   @Schema(example = "01:30:00")
   private String workTimeTopic;

   @Schema(example = "00:30:00")
   private String workTimeFinal;

}
