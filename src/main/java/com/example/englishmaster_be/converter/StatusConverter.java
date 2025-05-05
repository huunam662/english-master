package com.example.englishmaster_be.converter;

import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.domain.status.dto.response.StatusResponse;
import com.example.englishmaster_be.model.status.StatusEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface StatusConverter {

    StatusConverter INSTANCE = Mappers.getMapper(StatusConverter.class);

    @Mapping(target = "statusId", ignore = true)
    void flowToStatusEntity(StatusRequest statusRequest, @MappingTarget StatusEntity statusEntity);

    StatusResponse toStatusResponse(StatusEntity statusEntity);

    List<StatusResponse> toStatusResponseList(List<StatusEntity> statusEntityList);
}
