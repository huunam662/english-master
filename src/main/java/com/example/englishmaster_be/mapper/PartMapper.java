package com.example.englishmaster_be.mapper;


import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true))
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    PartEntity toPartEntity(PartRequest partDto);

    PartResponse toPartResponse(PartEntity part);

    List<PartResponse> toPartResponseList(Collection<PartEntity> partList);

    PartBasicResponse toPartBasicResponse(PartEntity partEntity);

    List<PartBasicResponse> toPartBasicResponseList(Collection<PartEntity> partList);

    default List<String> toPartNameResponseList(Collection<PartEntity> partEntities) {

        return partEntities.stream().map(
                PartEntity::getPartName
        ).distinct().sorted().toList();
    }

    @Mapping(target = "partId", ignore = true)
    void flowToPartEntity(PartRequest partRequest, @MappingTarget PartEntity partEntity);
    
}
