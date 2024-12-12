package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.News.NewsRequest;
import com.example.englishmaster_be.Model.Response.NewsResponse;
import com.example.englishmaster_be.entity.NewsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface NewsMapper {

    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

    NewsResponse toNewsResponse(NewsEntity newsEntity);

    List<NewsResponse> toNewsResponseList(List<NewsEntity> newsEntityList);

    @Mapping(target = "newsId", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "enable", defaultValue = "false")
    void flowToNewsEntity(NewsRequest newsRequest, @MappingTarget NewsEntity newsEntity);
}
