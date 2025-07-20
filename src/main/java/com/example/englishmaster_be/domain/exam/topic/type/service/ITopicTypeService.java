package com.example.englishmaster_be.domain.exam.topic.type.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.req.CreateTopicTypeReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.req.UpdateTopicTypeReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeKeyRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.view.ITopicTypePageView;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ITopicTypeService {

    TopicTypeEntity getTopicTypeToId(UUID topicTypeId);

    List<TopicTypeEntity> getAllTopicTypes();

    TopicTypeKeyRes createTopicType(CreateTopicTypeReq request);

    TopicTypeKeyRes updateTopicType(UpdateTopicTypeReq request);

    void deleteTopicType(UUID topicTypeId);

    UUID getTopicTypeIdToName(String topicTypeName);

    TopicTypeEntity saveTopicType(TopicTypeEntity topicTypeEntity);

    Page<ITopicTypePageView> getPageTopicType(PageOptionsReq optionsReq);
}
