package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.news.dto.request.CreateNewsRequest;
import com.example.englishmaster_be.domain.news.dto.request.UpdateNewsRequest;
import com.example.englishmaster_be.domain.news.dto.response.NewsResponse;
import com.example.englishmaster_be.model.news.NewsEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface NewsMapper {

    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

    NewsResponse toNewsResponse(NewsEntity newsEntity);

    List<NewsResponse> toNewsResponseList(Collection<NewsEntity> newsEntityList);

    @Mapping(target = "image", ignore = true)
    void flowToNewsEntity(CreateNewsRequest newsRequest, @MappingTarget NewsEntity newsEntity);

    @Mapping(target = "newsId", ignore = true)
    @Mapping(target = "enable", defaultValue = "false")
    @Mapping(target = "image", ignore = true)
    void flowToNewsEntity(UpdateNewsRequest newsRequest, @MappingTarget NewsEntity newsEntity);

    NewsEntity toNewsEntity(CreateNewsRequest newsRequest);

}
