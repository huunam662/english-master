package com.example.englishmaster_be.domain.pack.mapper;

import com.example.englishmaster_be.domain.pack.dto.response.PackPackTypeResponse;
import com.example.englishmaster_be.domain.pack.dto.response.PackResponse;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.pack_type.mapper.PackTypeMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.Collection;
import java.util.List;

@Mapper(imports = {PackTypeMapper.class}, builder = @Builder(disableBuilder = true))
public interface PackMapper {

    PackMapper INSTANCE = Mappers.getMapper(PackMapper.class);

    PackResponse toPackResponse(PackEntity packEntity);

    List<PackResponse> toPackResponseList(Collection<PackEntity> packEntityList);

    @Mapping(target = "packType", expression = "java(PackTypeMapper.INSTANCE.toPackTypeResponse(pack.getPackType()))")
    PackPackTypeResponse toPackPackTypeResponse(PackEntity pack);

    List<PackPackTypeResponse> toPackPackTypeResponseList(Collection<PackEntity> packEntityList);
}
