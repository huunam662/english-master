package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.news.dto.request.NewsRequest;
import com.example.englishmaster_be.domain.news.dto.response.NewsResponse;
import com.example.englishmaster_be.model.news.NewsEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class NewsMapperImpl implements NewsMapper {

    @Override
    public NewsResponse toNewsResponse(NewsEntity newsEntity) {
        if ( newsEntity == null ) {
            return null;
        }

        NewsResponse.NewsResponseBuilder newsResponse = NewsResponse.builder();

        newsResponse.newsId( newsEntity.getNewsId() );
        newsResponse.title( newsEntity.getTitle() );
        newsResponse.content( newsEntity.getContent() );
        newsResponse.image( newsEntity.getImage() );
        newsResponse.createAt( newsEntity.getCreateAt() );
        newsResponse.updateAt( newsEntity.getUpdateAt() );
        newsResponse.enable( newsEntity.getEnable() );

        return newsResponse.build();
    }

    @Override
    public List<NewsResponse> toNewsResponseList(List<NewsEntity> newsEntityList) {
        if ( newsEntityList == null ) {
            return null;
        }

        List<NewsResponse> list = new ArrayList<NewsResponse>( newsEntityList.size() );
        for ( NewsEntity newsEntity : newsEntityList ) {
            list.add( toNewsResponse( newsEntity ) );
        }

        return list;
    }

    @Override
    public void flowToNewsEntity(NewsRequest newsRequest, NewsEntity newsEntity) {
        if ( newsRequest == null ) {
            return;
        }

        if ( newsRequest.getEnable() != null ) {
            newsEntity.setEnable( newsRequest.getEnable() );
        }
        else {
            newsEntity.setEnable( false );
        }
        newsEntity.setTitle( newsRequest.getTitle() );
        newsEntity.setContent( newsRequest.getContent() );
    }
}
