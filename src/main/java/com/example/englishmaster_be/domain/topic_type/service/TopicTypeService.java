package com.example.englishmaster_be.domain.topic_type.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.topic_type.dto.request.CreateTopicTypeRequest;
import com.example.englishmaster_be.domain.topic_type.dto.request.UpdateTopicTypeRequest;
import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeKeyResponse;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.topic_type.repository.jpa.TopicTypeRepository;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicTypeService implements ITopicTypeService{

    TopicTypeRepository topicTypeRepository;
    IUserService userService;

    @Override
    public TopicTypeEntity getTopicTypeToId(UUID topicTypeId) {

        Assert.notNull(topicTypeId, "topic type id is required.");
        return topicTypeRepository.findById(topicTypeId)
                .orElseThrow(() -> new ErrorHolder(Error.RESOURCE_NOT_FOUND));
    }

    @Override
    public List<TopicTypeEntity> getAllTopicTypes() {
        return topicTypeRepository.findAll();
    }

    @Transactional
    @Override
    public TopicTypeKeyResponse createTopicType(CreateTopicTypeRequest request) {

        Assert.notNull(request, "topic type for create is required.");

        UserEntity userCurrent = userService.currentUser();

        if(topicTypeRepository.existsByTypeName(request.getTopicTypeName()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Topic type name already exists.");

        TopicTypeEntity topicType = new TopicTypeEntity();
        topicType.setTopicTypeName(request.getTopicTypeName());
        topicType.setUserCreate(userCurrent);
        topicType.setUserUpdate(userCurrent);
        topicType = topicTypeRepository.save(topicType);

        return new TopicTypeKeyResponse(topicType.getTopicTypeId());
    }

    @Override
    public TopicTypeKeyResponse updateTopicType(UpdateTopicTypeRequest request) {

        Assert.notNull(request, "topic type for update is required.");

        UserEntity userCurrent = userService.currentUser();

        TopicTypeEntity topicType = getTopicTypeToId(request.getTopicTypeId());

        if(topicTypeRepository.existsByTypeName(request.getTopicTypeId(), request.getTopicTypeName()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Topic type name already exists.");

        topicType.setTopicTypeName(request.getTopicTypeName());
        topicType.setUserUpdate(userCurrent);
        topicType = topicTypeRepository.save(topicType);

        return new TopicTypeKeyResponse(topicType.getTopicTypeId());
    }

    @Override
    public void deleteTopicType(UUID topicTypeId) {

        TopicTypeEntity topicType = getTopicTypeToId(topicTypeId);

        topicTypeRepository.delete(topicType);
    }

    @Override
    public UUID getTopicTypeIdToName(String topicTypeName) {
        return topicTypeRepository.findIdByTypeName(topicTypeName);
    }

    @Transactional
    @Override
    public TopicTypeEntity saveTopicType(TopicTypeEntity topicTypeEntity) {
        Assert.notNull(topicTypeEntity, "topic type entity is required.");
        return topicTypeRepository.save(topicTypeEntity);
    }
}
