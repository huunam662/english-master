package com.example.englishmaster_be.mapper;


import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    PartEntity toPartEntity(PartRequest partDto);

    @Mapping(target = "totalQuestion", expression = "java(part.getQuestions() != null ? part.getQuestions().size() : 0)")
    PartResponse toPartResponse(PartEntity part);

    List<PartResponse> toPartResponseList(List<PartEntity> partList);

    @Mapping(target = "partId", ignore = true)
    void flowToPartEntity(PartRequest partRequest, @MappingTarget PartEntity partEntity);
    
}
