package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.type.dto.request.TypeRequest;
import com.example.englishmaster_be.domain.type.dto.response.TypeResponse;
import com.example.englishmaster_be.model.type.TypeEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface TypeMapper {

    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeResponse toTypeResponse(TypeEntity typeEntity);

    TypeEntity toTypeEntity(TypeRequest typeRequest);

    List<TypeResponse> toTypeResponseList(List<TypeEntity> typeEntityList);

    @Mapping(target = "typeId", ignore = true)
    void flowToTypeEntity(TypeRequest typeRequest, @MappingTarget TypeEntity typeEntity);

}
