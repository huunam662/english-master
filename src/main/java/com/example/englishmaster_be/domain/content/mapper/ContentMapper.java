package com.example.englishmaster_be.domain.content.mapper;

import com.example.englishmaster_be.domain.content.dto.request.ContentRequest;
import com.example.englishmaster_be.domain.content.dto.response.ContentBasicResponse;
import com.example.englishmaster_be.domain.content.model.ContentEntity;
import com.example.englishmaster_be.domain.content.dto.response.ContentResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface ContentMapper {

    ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);

    ContentResponse toContentResponse(ContentEntity content);

    List<ContentResponse> toContentResponseList(Collection<ContentEntity> content);

    ContentBasicResponse toContentBasicResponse(ContentEntity content);

    List<ContentBasicResponse> toContentBasicResponseList(Collection<ContentEntity> contentEntityList);

    @Mapping(target = "contentId", ignore = true)
    void flowToContentEntity(ContentRequest contentRequest, @MappingTarget ContentEntity content);
}
