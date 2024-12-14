package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.model.request.Type.TypeRequest;
import com.example.englishmaster_be.model.response.TypeResponse;
import com.example.englishmaster_be.entity.TypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TypeMapper {

    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeResponse toTypeResponse(TypeEntity typeEntity);

    TypeEntity toTypeEntity(TypeRequest typeRequest);

    List<TypeResponse> toTypeResponseList(List<TypeEntity> typeEntityList);

    @Mapping(target = "typeId", ignore = true)
    void flowToTypeEntity(TypeRequest typeRequest, @MappingTarget TypeEntity typeEntity);

}
