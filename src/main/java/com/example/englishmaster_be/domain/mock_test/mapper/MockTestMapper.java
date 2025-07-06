package com.example.englishmaster_be.domain.mock_test.mapper;

import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.*;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.mock_test_result.mapper.MockTestResultMapper;
import com.example.englishmaster_be.domain.speaking_submission.util.speaking.SpeakingUtil;
import com.example.englishmaster_be.domain.topic.mapper.TopicMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        imports = {MockTestResultMapper.class, AnswerMapper.class, TopicMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface MockTestMapper {

    MockTestMapper INSTANCE = Mappers.getMapper(MockTestMapper.class);

    @Mapping(target = "topic", expression = "java(TopicMapper.INSTANCE.toTopicAndTypeResponse(mockTestEntity.getTopic()))")
    @Mapping(target = "mockTestResults", expression = "java(MockTestResultMapper.INSTANCE.toMockTestResultResponseList(mockTestEntity.getMockTestResults()))")
    MockTestResponse toMockTestResponse(MockTestEntity mockTestEntity);

    List<MockTestResponse> toMockTestResponseList(Collection<MockTestEntity> mockTestEntityList);

    @Mapping(target = "topic", expression = "java(toTopicAndTypeResponse(mockTest.getTopic()))")
    @Mapping(target = "topic.topicType", source = "mockTest.topic.topicType")
    MockTestTopicResponse toMockTestTopicResponse(MockTestEntity mockTest);

    List<MockTestTopicResponse> toMockTestTopicResponseList(Collection<MockTestEntity> mockTestEntityList);

    MockTestEntity toMockTestEntity(MockTestRequest mockTestRequest);

    @Mapping(target = "mockTestResponse" , expression = "java(toMockTestTopicResponse(mockTest))")
    @Mapping(target = "mockTestResultResponses" , expression = "java(MockTestResultMapper.INSTANCE.toMockTestResultResponseList(mockTest.getMockTestResults()))")
    MockTestInforResponse toMockTestRnListeningInforResponse(MockTestEntity mockTest);

    default MockTestInforResponse toMockTestSpeakingResponse(MockTestEntity mockTest){
        if(mockTest == null) return null;
        MockTestInforResponse infResponse = new MockTestInforResponse();
        infResponse.setMockTestResponse(toMockTestTopicResponse(mockTest));
        infResponse.setSpeakingSubmissionResults(SpeakingUtil.fillToSpeakingSubmissionResults(mockTest.getSpeakingSubmissions().stream().toList()));
        return infResponse;
    }

}
