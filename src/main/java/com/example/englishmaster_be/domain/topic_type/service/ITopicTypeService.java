package com.example.englishmaster_be.domain.topic_type.service;

import com.example.englishmaster_be.domain.topic_type.dto.request.CreateTopicTypeRequest;
import com.example.englishmaster_be.domain.topic_type.dto.request.UpdateTopicTypeRequest;
import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeKeyResponse;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;

import java.util.List;
import java.util.UUID;

public interface ITopicTypeService {

    TopicTypeEntity getTopicTypeToId(UUID topicTypeId);

    List<TopicTypeEntity> getAllTopicTypes();

    TopicTypeKeyResponse createTopicType(CreateTopicTypeRequest request);

    TopicTypeKeyResponse updateTopicType(UpdateTopicTypeRequest request);

    void deleteTopicType(UUID topicTypeId);

    UUID getTopicTypeIdToName(String topicTypeName);

    TopicTypeEntity saveTopicType(TopicTypeEntity topicTypeEntity);
}
