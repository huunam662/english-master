package com.example.englishmaster_be.domain.mock_test_result.mapper;


import com.example.englishmaster_be.domain.mock_test.mapper.MockTestMapper;
import com.example.englishmaster_be.domain.mock_test_result.dto.response.MockTestResultResponse;
import com.example.englishmaster_be.domain.mock_test_result.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestResultEntity;
import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        imports = {MockTestMapper.class, MockTestDetailMapper.class, PartMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface MockTestResultMapper {

    MockTestResultMapper INSTANCE = Mappers.getMapper(MockTestResultMapper.class);

    @Mapping(target = "mockTestResultId", ignore = true)
    void flowToResultMockTest(ResultMockTestRequest request, @MappingTarget MockTestResultEntity resultMockTestEntity);

    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartBasicResponse(mockTestResultEntity.getPart()))")
    @Mapping(target = "mockTestDetails", expression = "java(MockTestDetailMapper.INSTANCE.toMockTestDetailResponseList(mockTestResultEntity.getMockTestDetails()))")
    MockTestResultResponse toMockTestResultResponse(MockTestResultEntity mockTestResultEntity);

    List<MockTestResultResponse> toMockTestResultResponseList(Collection<MockTestResultEntity> mockTestResultEntityList);

}
