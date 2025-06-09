package com.example.englishmaster_be.domain.topic.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicContentResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.response.TopicBasicResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;
import java.util.*;

@Mapper(imports = {PartMapper.class, LocalTime.class}, builder = @Builder(disableBuilder = true))
public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    @Mapping(target = "packId", source = "pack.packId")
    @Mapping(target = "packName", source = "pack.packName")
    @Mapping(target = "partNames", expression = "java(PartMapper.INSTANCE.toPartNameResponseList(topicEntity.getParts()))")
    @Mapping(target = "topicType", source = "topicType.topicTypeName")
    TopicResponse toTopicResponse(TopicEntity topicEntity);

    List<TopicResponse> toTopicResponseList(Collection<TopicEntity> topicEntityList);

    TopicBasicResponse toTopicBasicResponse(TopicEntity topicEntity);

    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "topicImage", ignore = true)
    @Mapping(target = "workTime", ignore = true)
    @Mapping(target = "topicType", ignore = true)
    void flowToTopicEntity(TopicRequest topicRequest, @MappingTarget TopicEntity topicEntity);


    @Mapping(target = "topicType", ignore = true)
    void flowToTopicEntity(ExcelTopicContentResponse excelTopicContentResponse, @MappingTarget TopicEntity topicEntity);
}
