package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsResponse {

    UUID newsId;

    String newsTitle;

    String newsContent;

    String newsImage;

    String createAt;

    String updateAt;

    boolean isEnable;

    public NewsResponse(News news) {

        if(Objects.isNull(news)) return;

        this.newsId = news.getNewsId();
        this.newsContent = news.getContent();
        this.newsTitle = news.getTitle();
        this.newsImage = news.getImage();
        this.isEnable = news.isEnable();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(news.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(news.getCreateAt()));
        if(Objects.nonNull(news.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(news.getUpdateAt()));

    }

}
