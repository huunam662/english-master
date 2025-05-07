package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.*;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface MockTestMapper {

    MockTestMapper INSTANCE = Mappers.getMapper(MockTestMapper.class);

    @Mapping(target = "topic", expression = "java(toMockTestTopicResponse(mockTestEntity.getTopic()))")
    @Mapping(target = "mockTestResults", expression = "java(MockTestResultMapper.INSTANCE.toMockTestResultResponseList(mockTestEntity.getMockTestResults()))")
    MockTestResponse toMockTestResponse(MockTestEntity mockTestEntity);

    List<MockTestResponse> toMockTestResponseList(List<MockTestEntity> mockTestEntityList);

    MockTestEntity toMockTestEntity(MockTestRequest mockTestRequest);

    MockTestPartResponse toMockTestPartResponse(PartEntity partEntity);

    MockTestTopicResponse toMockTestTopicResponse(TopicEntity topicEntity);

    MockTestQuestionResponse toMockTestQuestionResponse(QuestionEntity questionEntity);

    List<MockTestQuestionResponse> toMockTestQuestionResponseList(List<QuestionEntity> questionEntityList);

    MockTestAnswerResponse toMockTestAnswerResponse(AnswerEntity answerEntity);

    List<MockTestAnswerResponse> toMockTestAnswerResponseList(List<AnswerEntity> answerEntityList);

    @Mapping(target = "mockTestResponse" , expression = "java(toMockTestResponse(mockTestEntity))")
    @Mapping(target = "mockTestResultResponses" , expression = "java(MockTestResultMapper.INSTANCE.toMockTestResultResponseList(mockTestEntity.getMockTestResults()))")
    MockTestInforResponse toMockTestInforResponse(MockTestEntity mockTestEntity);
}
