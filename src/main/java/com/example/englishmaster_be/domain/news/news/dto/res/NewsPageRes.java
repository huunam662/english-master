package com.example.englishmaster_be.domain.news.news.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewsPageRes {
    private NewsFullRes news;
    private Long countComments;
}
