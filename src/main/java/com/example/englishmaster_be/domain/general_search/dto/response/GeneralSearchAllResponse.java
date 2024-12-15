package com.example.englishmaster_be.domain.general_search.dto.response;

import com.example.englishmaster_be.domain.news.dto.response.NewsResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordResponse;
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
