package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.Question.SaveQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.SaveTopicDTO;
import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Model.Response.TopicResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateTopicByExcelFileResponse;
import com.example.englishmaster_be.Model.Topic;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper
public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    SaveTopicDTO toSaveTopicDTO(CreateTopicByExcelFileResponse createTopicByExcelFileResponse);

    @Mapping(source = "parts", target = "listPart", qualifiedByName = {"mapPartsToListPartId"})
    @Mapping(source = "enable", target = "isEnable")
    TopicResponse toTopicResponse(Topic topic);

    @Named("mapPartsToListPartId")
    default List<UUID> mapPartsToListPartId(List<Part> parts){

        if(parts == null) return null;

        return parts.stream().filter(Objects::nonNull).map(Part::getPartId).toList();
    }

    @IterableMapping(elementTargetType = TopicResponse.class)
    List<TopicResponse> toListTopicResponses(List<Topic> topics);

    @Mapping(target = "topicImage", ignore = true)
    void updateTopicEntity(SaveTopicDTO saveTopicDTO, @MappingTarget Topic topic);

}
