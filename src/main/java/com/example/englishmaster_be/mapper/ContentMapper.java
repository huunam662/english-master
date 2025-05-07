package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.content.dto.request.ContentRequest;
import com.example.englishmaster_be.domain.content.dto.response.ContentBasicResponse;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.domain.content.dto.response.ContentResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface ContentMapper {

    ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);

    ContentResponse toContentResponse(ContentEntity content);

    List<ContentResponse> toContentResponseList(List<ContentEntity> content);

    ContentBasicResponse toContentBasicResponse(ContentEntity content);

    List<ContentBasicResponse> toContentBasicResponseList(List<ContentEntity> contentEntityList);

    @Mapping(target = "contentId", ignore = true)
    void flowToContentEntity(ContentRequest contentRequest, @MappingTarget ContentEntity content);
}
