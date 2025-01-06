package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestResponse;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
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
    MockTestPartResponse toPartMockTestResponse(MockTestEntity mockTestEntity);

}
