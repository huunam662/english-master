package com.example.englishmaster_be.converter;

import com.example.englishmaster_be.domain.pack.dto.response.PackResponse;
import com.example.englishmaster_be.model.pack.PackEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface PackConverter {

    PackConverter INSTANCE = Mappers.getMapper(PackConverter.class);

    PackResponse toPackResponse(PackEntity packEntity);

    List<PackResponse> toPackResponseList(List<PackEntity> packEntityList);

}
