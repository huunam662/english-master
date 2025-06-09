package com.example.englishmaster_be.domain.pack.mapper;

import com.example.englishmaster_be.domain.pack.dto.response.PackResponse;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface PackMapper {

    PackMapper INSTANCE = Mappers.getMapper(PackMapper.class);

    PackResponse toPackResponse(PackEntity packEntity);

    List<PackResponse> toPackResponseList(Collection<PackEntity> packEntityList);

}
