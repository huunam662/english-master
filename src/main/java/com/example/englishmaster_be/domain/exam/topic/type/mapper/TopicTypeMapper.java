package com.example.englishmaster_be.domain.exam.topic.type.mapper;

import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeFullRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypePageRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.view.ITopicTypePageView;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TopicTypeMapper {

    TopicTypeMapper INTANCE = Mappers.getMapper(TopicTypeMapper.class);

    TopicTypeFullRes toTopicTypeResponse(TopicTypeEntity topicType);

    List<TopicTypeFullRes> toTopicTypeResponseList(Collection<TopicTypeEntity> topicTypes);

    TopicTypePageRes toTopicTypePageRes(ITopicTypePageView topicTypePageView);

    List<TopicTypePageRes> toTopicTypePageResList(Collection<ITopicTypePageView> topicTypePageViews);
}
