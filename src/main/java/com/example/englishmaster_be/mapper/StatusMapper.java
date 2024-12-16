package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.domain.status.dto.response.StatusResponse;
import com.example.englishmaster_be.model.status.StatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StatusMapper {

    StatusMapper INSTANCE = Mappers.getMapper(StatusMapper.class);

    @Mapping(target = "statusId", ignore = true)
    void flowToStatusEntity(StatusRequest statusRequest, @MappingTarget StatusEntity statusEntity);

    StatusResponse toStatusResponse(StatusEntity statusEntity);

    List<StatusResponse> toStatusResponseList(List<StatusEntity> statusEntityList);
}
