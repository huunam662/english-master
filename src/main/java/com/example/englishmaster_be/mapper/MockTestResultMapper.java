package com.example.englishmaster_be.mapper;


import com.example.englishmaster_be.domain.mock_test_result.dto.response.MockTestResultResponse;
import com.example.englishmaster_be.domain.mock_test_result.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface MockTestResultMapper {

    MockTestResultMapper INSTANCE = Mappers.getMapper(MockTestResultMapper.class);

    @Mapping(target = "mockTestResultId", ignore = true)
    void flowToResultMockTest(ResultMockTestRequest request, @MappingTarget MockTestResultEntity resultMockTestEntity);

    @Mapping(target = "part", expression = "java(MockTestMapper.INSTANCE.toMockTestPartResponse(mockTestResultEntity.getPart()))")
    @Mapping(target = "mockTestDetails", expression = "java(MockTestDetailMapper.INSTANCE.toMockTestDetailResponseList(mockTestResultEntity.getMockTestDetails()))")
    MockTestResultResponse toMockTestResultResponse(MockTestResultEntity mockTestResultEntity);

    List<MockTestResultResponse> toMockTestResultResponseList(List<MockTestResultEntity> mockTestResultEntityList);

}
