package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Response.PackResponse;
import com.example.englishmaster_be.entity.PackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PackMapper {

    PackMapper INSTANCE = Mappers.getMapper(PackMapper.class);

    PackResponse toPackResponse(PackEntity packEntity);

    List<PackResponse> toPackResponseList(List<PackEntity> packEntityList);

}
