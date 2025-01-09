package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicContentResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(builder = @Builder(disableBuilder = true))
public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicRequest toTopicRequest(ExcelTopicResponse topicByExcelFileResponse);

    @Mapping(target = "parts", expression = "java(toListPartId(topicEntity.getParts()))")
    @Mapping(target = "packId", source = "pack.packId")
    @Mapping(target = "packName", source = "pack.packName")
    @Mapping(target = "statusId", source = "status.statusId")
    @Mapping(target = "numberQuestion", defaultValue = "0")
    TopicResponse toTopicResponse(TopicEntity topicEntity);

    default List<UUID> toListPartId(List<PartEntity> parts){

        if(parts == null) return null;

        return parts.stream().filter(Objects::nonNull).map(PartEntity::getPartId).toList();
    }

    List<TopicResponse> toTopicResponseList(List<TopicEntity> topicEntityList);

    @Mapping(target = "pack", expression = "java(PackMapper.INSTANCE.toPackResponse(topicEntity.getPack()))")
    @Mapping(target = "parts", expression = "java(PartMapper.INSTANCE.toPartResponseList(topicEntity.getParts()))")
    ExcelTopicResponse toExcelTopicResponse(TopicEntity topicEntity);

    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "numberQuestion", defaultValue = "0")
    @Mapping(target = "topicImage", ignore = true)
    void flowToTopicEntity(TopicRequest topicRequest, @MappingTarget TopicEntity topicEntity);

    void flowToTopicEntity(ExcelTopicResponse topicByExcelFileResponse, @MappingTarget TopicEntity topicEntity);

    void flowToTopicEntity(ExcelTopicContentResponse excelTopicContentResponse, @MappingTarget TopicEntity topicEntity);
}
