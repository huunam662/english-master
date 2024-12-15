package com.example.englishmaster_be.domain.exam.dto.response;


import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamResultResponse {

    TopicResponse topic;

    List<MockTestResponse> listMockTest;

}
