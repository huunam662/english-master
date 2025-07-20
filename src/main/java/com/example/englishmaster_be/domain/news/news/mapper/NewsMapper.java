package com.example.englishmaster_be.domain.news.news.mapper;

import com.example.englishmaster_be.domain.news.news.dto.req.CreateNewsReq;
import com.example.englishmaster_be.domain.news.news.dto.req.UpdateNewsReq;
import com.example.englishmaster_be.domain.news.news.dto.res.NewsPageRes;
import com.example.englishmaster_be.domain.news.news.dto.res.NewsRes;
import com.example.englishmaster_be.domain.news.news.dto.view.INewsPageView;
import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
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

    NewsRes toNewsResponse(NewsEntity newsEntity);

    List<NewsRes> toNewsResponseList(Collection<NewsEntity> newsEntityList);

    @Mapping(target = "image", ignore = true)
    void flowToNewsEntity(CreateNewsReq newsRequest, @MappingTarget NewsEntity newsEntity);

    @Mapping(target = "newsId", ignore = true)
    @Mapping(target = "enable", defaultValue = "false")
    @Mapping(target = "image", ignore = true)
    void flowToNewsEntity(UpdateNewsReq newsRequest, @MappingTarget NewsEntity newsEntity);

    NewsEntity toNewsEntity(CreateNewsReq newsRequest);

    NewsPageRes toNewsPageRes(INewsPageView newsPageView);

    List<NewsPageRes> toNewsPageResList(Collection<INewsPageView> newsPageViews);
}
