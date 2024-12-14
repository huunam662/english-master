package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.model.request.MockTest.MockTestRequest;
import com.example.englishmaster_be.model.response.MockTestResponse;
import com.example.englishmaster_be.model.response.PartMockTestResponse;
import com.example.englishmaster_be.entity.MockTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MockTestMapper {

    MockTestMapper INSTANCE = Mappers.getMapper(MockTestMapper.class);

    @Mapping(target = "topicId", source = "topic.topicId")
    MockTestResponse toMockTestResponse(MockTestEntity mockTestEntity);

    List<MockTestResponse> toMockTestResponseList(List<MockTestEntity> mockTestEntityList);

    MockTestEntity toMockTestEntity(MockTestRequest mockTestRequest);

    @Mapping(target = "topicId", source = "topic.topicId")
    @Mapping(target = "topicName", source = "topic.topicName")
    @Mapping(target = "topicTime", source = "topic.workTime")
    @Mapping(target = "parts", ignore = true)
    PartMockTestResponse toPartMockTestResponse(MockTestEntity mockTestEntity);

}
