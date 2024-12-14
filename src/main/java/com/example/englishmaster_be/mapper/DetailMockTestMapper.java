package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.model.response.DetailMockTestResponse;
import com.example.englishmaster_be.entity.DetailMockTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DetailMockTestMapper {

    DetailMockTestMapper INSTANCE = Mappers.getMapper(DetailMockTestMapper.class);

    @Mapping(target = "answerId", source = "answer.answerId")
    @Mapping(target = "answerContent", source = "answer.answerContent")
    @Mapping(target = "correctAnswer", source = "answer.correctAnswer", defaultValue = "false")
    @Mapping(target = "scoreAnswer", source = "answer.question.questionScore", defaultValue = "0")
    DetailMockTestResponse toDetailMockTestResponse(DetailMockTestEntity detailMockTestEntity);

    List<DetailMockTestResponse> toDetailMockTestResponseList(List<DetailMockTestEntity> detailMockTestList);

}
