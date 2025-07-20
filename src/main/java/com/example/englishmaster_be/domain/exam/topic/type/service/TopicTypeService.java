package com.example.englishmaster_be.domain.exam.topic.type.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.req.CreateTopicTypeReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.req.UpdateTopicTypeReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeKeyRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.view.ITopicTypePageView;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.exam.topic.type.repository.TopicTypeDslRepository;
import com.example.englishmaster_be.domain.exam.topic.type.repository.TopicTypeRepository;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;

@Service
public class TopicTypeService implements ITopicTypeService{

    private final TopicTypeRepository topicTypeRepository;
    private final IUserService userService;
    private final TopicTypeDslRepository topicTypeDslRepository;

    @Lazy
    public TopicTypeService(TopicTypeDslRepository topicTypeDslRepository, TopicTypeRepository topicTypeRepository, IUserService userService) {
        this.topicTypeRepository = topicTypeRepository;
        this.userService = userService;
        this.topicTypeDslRepository = topicTypeDslRepository;
    }

    @Override
    public TopicTypeEntity getTopicTypeToId(UUID topicTypeId) {
        return topicTypeRepository.findEntityById(topicTypeId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Topic type not found."));
    }

    @Override
    public List<TopicTypeEntity> getAllTopicTypes() {
        return topicTypeRepository.findAllEntity();
    }

    @Transactional
    @Override
    public TopicTypeKeyRes createTopicType(CreateTopicTypeReq request) {
        UserEntity userCurrent = userService.currentUser();
        if(topicTypeRepository.existsByTypeName(request.getTopicTypeName()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Topic type name already exists.");
        TopicTypeEntity topicType = new TopicTypeEntity();
        topicType.setTopicTypeName(request.getTopicTypeName());
        topicType.setUserCreate(userCurrent);
        topicType.setUserUpdate(userCurrent);
        topicType = topicTypeRepository.save(topicType);
        return new TopicTypeKeyRes(topicType.getTopicTypeId());
    }

    @Override
    public TopicTypeKeyRes updateTopicType(UpdateTopicTypeReq request) {
        UserEntity userCurrent = userService.currentUser();
        TopicTypeEntity topicType = getTopicTypeToId(request.getTopicTypeId());
        if(topicTypeRepository.existsByTypeName(request.getTopicTypeId(), request.getTopicTypeName()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Topic type name already exists.");
        topicType.setTopicTypeName(request.getTopicTypeName());
        topicType.setUserUpdate(userCurrent);
        topicType = topicTypeRepository.save(topicType);
        return new TopicTypeKeyRes(topicType.getTopicTypeId());
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

    @Override
    public Page<ITopicTypePageView> getPageTopicType(PageOptionsReq optionsReq) {
        return topicTypeDslRepository.getTopicTypePage(optionsReq);
    }
}
