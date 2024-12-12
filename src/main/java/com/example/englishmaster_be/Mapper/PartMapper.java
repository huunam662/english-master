package com.example.englishmaster_be.Mapper;


import com.example.englishmaster_be.Model.Request.Part.PartRequest;
import com.example.englishmaster_be.entity.PartEntity;
import com.example.englishmaster_be.Model.Response.PartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    PartEntity toPartEntity(PartRequest partDto);

    @Mapping(target = "totalQuestion", expression = "java(part.getQuestions() != null ? part.getQuestions().size() : 0)")
    PartResponse toPartResponse(PartEntity part);

    List<PartResponse> toPartResponseList(List<PartEntity> partList);

    @Mapping(target = "partId", ignore = true)
    void flowToPartEntity(PartRequest partRequest, @MappingTarget PartEntity partEntity);
    
}
