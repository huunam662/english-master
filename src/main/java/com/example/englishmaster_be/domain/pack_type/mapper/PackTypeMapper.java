package com.example.englishmaster_be.domain.pack_type.mapper;

import com.example.englishmaster_be.domain.pack_type.dto.request.CreatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.UpdatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeKeyResponse;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeResponse;
import com.example.englishmaster_be.domain.pack_type.model.PackTypeEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface PackTypeMapper {

    PackTypeMapper INSTANCE = Mappers.getMapper(PackTypeMapper.class);

    @Mapping(target = "packTypeId", source = "id")
    @Mapping(target = "packTypeName", source = "name")
    PackTypeResponse toPackTypeResponse(PackTypeEntity packTypeEntity);

    List<PackTypeResponse> toPackTypeResponseList(Collection<PackTypeEntity> packTypeEntityList);

    PackTypeEntity toPackTypeEntity(CreatePackTypeRequest createPackTypeRequest);

    void flowToPackTypeEntity(@MappingTarget PackTypeEntity packTypeEntity, CreatePackTypeRequest createPackTypeRequest);

    void flowToPackTypeEntity(@MappingTarget PackTypeEntity packTypeEntity, UpdatePackTypeRequest updatePackTypeRequest);

    @Mapping(target = "packTypeId", source = "id")
    PackTypeKeyResponse toPackTypeKeyResponse(PackTypeEntity packType);
}
