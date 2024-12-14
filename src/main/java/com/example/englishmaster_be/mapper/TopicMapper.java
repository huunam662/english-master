package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.model.request.Topic.TopicRequest;
import com.example.englishmaster_be.entity.PartEntity;
import com.example.englishmaster_be.model.response.TopicResponse;
import com.example.englishmaster_be.model.response.excel.TopicByExcelFileResponse;
import com.example.englishmaster_be.entity.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper
public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicRequest toTopicRequest(TopicByExcelFileResponse topicByExcelFileResponse);

    @Mapping(target = "parts", expression = "java(toListPartId(topicEntity.getParts()))")
    @Mapping(target = "packId", source = "pack.packId")
    @Mapping(target = "statusId", source = "status.statusId")
    @Mapping(target = "numberQuestion", defaultValue = "0")
    TopicResponse toTopicResponse(TopicEntity topicEntity);

    default List<UUID> toListPartId(List<PartEntity> parts){

        if(parts == null) return null;

        return parts.stream().filter(Objects::nonNull).map(PartEntity::getPartId).toList();
    }

    List<TopicResponse> toTopicResponseList(List<TopicEntity> topicEntityList);

    @Mapping(target = "topicImage", ignore = true)
    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "numberQuestion", defaultValue = "0")
    void flowToTopicEntity(TopicRequest topicRequest, @MappingTarget TopicEntity topicEntity);

    void flowToTopicEntity(TopicByExcelFileResponse topicByExcelFileResponse, @MappingTarget TopicEntity topicEntity);

}
