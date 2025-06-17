package com.example.englishmaster_be.domain.mock_test.mapper;

import com.example.englishmaster_be.domain.answer.dto.response.Answer1Response;
import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.*;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.mock_test_result.mapper.MockTestResultMapper;
import com.example.englishmaster_be.domain.question.dto.response.QuestionAnswersResponse;
import com.example.englishmaster_be.domain.part.dto.response.Part2Response;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.dto.response.Question2Response;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.speaking_submission.util.speaking.SpeakingUtil;
import com.example.englishmaster_be.domain.topic.dto.response.Topic1Response;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        imports = {MockTestResultMapper.class, AnswerMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface MockTestMapper {

    MockTestMapper INSTANCE = Mappers.getMapper(MockTestMapper.class);

    @Mapping(target = "topic", expression = "java(toMockTestTopicResponse(mockTestEntity.getTopic()))")
    @Mapping(target = "mockTestResults", expression = "java(MockTestResultMapper.INSTANCE.toMockTestResultResponseList(mockTestEntity.getMockTestResults()))")
    MockTestResponse toMockTestResponse(MockTestEntity mockTestEntity);

    List<MockTestResponse> toMockTestResponseList(Collection<MockTestEntity> mockTestEntityList);

    @Mapping(target = "topic", expression = "java(toMockTestTopicResponse(mockTest.getTopic()))")
    @Mapping(target = "topic.topicType", source = "mockTest.topic.topicType")
    MockTest1Response toMockTest1Response(MockTestEntity mockTest);

    List<MockTest1Response> toMockTest1ResponseList(Collection<MockTestEntity> mockTestEntityList);

    MockTestEntity toMockTestEntity(MockTestRequest mockTestRequest);

    Part2Response toMockTestPartResponse(PartEntity partEntity);

    Topic1Response toMockTestTopicResponse(TopicEntity topicEntity);

    Question2Response toMockTestQuestionResponse(QuestionEntity questionEntity);

    List<Question2Response> toMockTestQuestionResponseList(Collection<QuestionEntity> questionEntityList);

    Answer1Response toMockTestAnswerResponse(AnswerEntity answerEntity);

    List<Answer1Response> toMockTestAnswerResponseList(Collection<AnswerEntity> answerEntityList);

    @Mapping(target = "mockTestResponse" , expression = "java(toMockTest1Response(mockTest))")
    @Mapping(target = "mockTestResultResponses" , expression = "java(MockTestResultMapper.INSTANCE.toMockTestResultResponseList(mockTest.getMockTestResults()))")
    MockTestInforResponse toMockTestRnListeningInforResponse(MockTestEntity mockTest);

    default MockTestInforResponse toMockTestSpeakingResponse(MockTestEntity mockTest){
        if(mockTest == null) return null;
        MockTestInforResponse infResponse = new MockTestInforResponse();
        infResponse.setMockTestResponse(toMockTest1Response(mockTest));
        infResponse.setSpeakingSubmissionResults(SpeakingUtil.fillToSpeakingSubmissionResults(mockTest.getSpeakingSubmissions().stream().toList()));
        return infResponse;
    }

    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionChild.getAnswers()))")
    QuestionAnswersResponse toMockTestQuestionAnswersResponse(QuestionEntity questionChild);
}
