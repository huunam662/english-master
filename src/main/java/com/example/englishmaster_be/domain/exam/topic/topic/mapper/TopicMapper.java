package com.example.englishmaster_be.domain.exam.topic.topic.mapper;

import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.*;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.view.ITopicPageView;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.exam.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.req.TopicReq;
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

    @Mapping(target = ".", source = "topic")
    TopicPageToPackRes toTopicPageToPackRes(ITopicPageView topic);

    List<TopicPageToPackRes> toTopicPageToPackResList(Collection<ITopicPageView> topics);

    TopicPageRes toTopicPageRes(ITopicPageView topicPageView);

    List<TopicPageRes> toTopicPageResList(Collection<ITopicPageView> topicPageViews);
}
