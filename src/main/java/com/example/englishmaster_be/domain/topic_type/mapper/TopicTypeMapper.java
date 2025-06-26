package com.example.englishmaster_be.domain.topic_type.mapper;

import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeResponse;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TopicTypeMapper {

    TopicTypeMapper INTANCE = Mappers.getMapper(TopicTypeMapper.class);

    TopicTypeResponse toTopicTypeResponse(TopicTypeEntity topicType);

    List<TopicTypeResponse> toTopicTypeResponseList(List<TopicTypeEntity> topicTypes);
}
