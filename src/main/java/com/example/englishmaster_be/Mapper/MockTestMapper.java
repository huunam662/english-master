package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.MockTest.MockTestRequest;
import com.example.englishmaster_be.Model.Response.MockTestResponse;
import com.example.englishmaster_be.Model.Response.PartMockTestResponse;
import com.example.englishmaster_be.Model.Response.PartResponse;
import com.example.englishmaster_be.entity.MockTestEntity;
import com.example.englishmaster_be.entity.PartEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
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
