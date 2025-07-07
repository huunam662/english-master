package com.example.englishmaster_be.domain.topic.mapper;

import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.topic.dto.request.TopicReq;
import com.example.englishmaster_be.domain.topic.dto.response.*;
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
    TopicRes toTopicResponse(TopicEntity topicEntity);

    List<TopicRes> toTopicResponseList(Collection<TopicEntity> topicEntityList);

    @Mapping(target = "topicType", source = "topicType.topicTypeName")
    TopicBasicRes toTopicBasicResponse(TopicEntity topicEntity);

    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "topicImage", ignore = true)
    @Mapping(target = "workTime", ignore = true)
    @Mapping(target = "topicType", ignore = true)
    void flowToTopicEntity(TopicReq topicRequest, @MappingTarget TopicEntity topicEntity);

    TopicAndTypeRes toTopicAndTypeResponse(TopicEntity topicEntity);

    TopicAudioImageRes toTopicAudioImageRes(TopicEntity topic);
}
