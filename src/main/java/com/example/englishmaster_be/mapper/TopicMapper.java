package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicContentResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.response.TopicBasicResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;
import java.util.*;

@Mapper(builder = @Builder(disableBuilder = true), imports = {LocalTime.class})
public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicRequest toTopicRequest(ExcelTopicResponse topicByExcelFileResponse);

    @Mapping(target = "packId", source = "pack.packId")
    @Mapping(target = "packName", source = "pack.packName")
    @Mapping(target = "statusId", source = "status.statusId")
    @Mapping(target = "partNames", expression = "java(PartMapper.INSTANCE.toPartNameResponseList(topicEntity.getParts()))")
    TopicResponse toTopicResponse(TopicEntity topicEntity);

    List<TopicResponse> toTopicResponseList(List<TopicEntity> topicEntityList);
    TopicBasicResponse toTopicBasicResponse(TopicEntity topicEntity);

    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "topicImage", ignore = true)
    @Mapping(target = "workTime", expression = "java(LocalTime.parse(topicRequest.getWorkTime()))")
    void flowToTopicEntity(TopicRequest topicRequest, @MappingTarget TopicEntity topicEntity);


    void flowToTopicEntity(ExcelTopicResponse topicByExcelFileResponse, @MappingTarget TopicEntity topicEntity);

    void flowToTopicEntity(ExcelTopicContentResponse excelTopicContentResponse, @MappingTarget TopicEntity topicEntity);
}
