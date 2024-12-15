package com.example.englishmaster_be.mapper;


import com.example.englishmaster_be.domain.result_mock_test.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.domain.result_mock_test.dto.response.ResultMockTestResponse;
import com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ResultMockTestMapper {

    ResultMockTestMapper INSTANCE = Mappers.getMapper(ResultMockTestMapper.class);

    @Mapping(target = "resultMockTestId", ignore = true)
    void flowToResultMockTest(ResultMockTestRequest request, @MappingTarget ResultMockTestEntity resultMockTestEntity);

    @Mapping(target = "mockTestId", source = "mockTest.mockTestId")
    @Mapping(target = "part", expression = "java(PartMapper.INSTANCE.toPartResponse(resultMockTest.getPart()))")
    ResultMockTestResponse toResultMockTestResponse(ResultMockTestEntity resultMockTest);

    List<ResultMockTestResponse> toResultMockTestResponseList(List<ResultMockTestEntity> resultMockTestList);
}
