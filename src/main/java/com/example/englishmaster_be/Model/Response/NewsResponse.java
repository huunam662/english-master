package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class NewsResponse {
    private UUID newsId;
    private String newsTitle;
    private String newsContent;
    private String newsImage;
    private boolean isEnable;
    private String createAt;
    private String updateAt;

    public NewsResponse(News news) {
        this.newsId = news.getNewsId();
        this.newsContent = news.getContent();
        this.newsTitle = news.getTitle();

        this.newsImage = news.getImage();
        this.isEnable = news.isEnable();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        this.createAt = sdf.format(Timestamp.valueOf(news.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(news.getUpdateAt()));

    }

}
