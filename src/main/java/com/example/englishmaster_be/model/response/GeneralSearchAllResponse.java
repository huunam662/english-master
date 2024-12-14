package com.example.englishmaster_be.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneralSearchAllResponse {

    List<TopicResponse> topicList;

    List<FlashCardWordResponse> flashCardWordList;

    List<NewsResponse> newsList;

}
