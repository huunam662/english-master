package com.example.englishmaster_be.Model.Response;


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
public class PartMockTestResponse {

     UUID topicId;

     String topicName;

     String topicTime;

     List<PartResponse> parts;

}
