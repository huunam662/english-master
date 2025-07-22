package com.example.englishmaster_be.domain.mock_test.mock_test.mapper;

import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.req.MockTestReq;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestFullRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestInforRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestPageRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestRes;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestPageView;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.mapper.TopicMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.Collection;
import java.util.List;

@Mapper(
        imports = {AnswerMapper.class, TopicMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface MockTestMapper {

    MockTestMapper INSTANCE = Mappers.getMapper(MockTestMapper.class);

    MockTestRes toMockTestResponse(MockTestEntity mockTestEntity);

    List<MockTestRes> toMockTestResponseList(Collection<MockTestEntity> mockTestEntityList);

    MockTestFullRes toMockTestFullResponse(MockTestEntity mockTest);

    List<MockTestFullRes> toMockTestFullResponseList(Collection<MockTestEntity> mockTestEntityList);

    MockTestEntity toMockTestEntity(MockTestReq mockTestRequest);

    default MockTestInforRes toMockTestRnListeningInforResponse(MockTestEntity mockTest) {
        if(mockTest == null) return null;
        MockTestInforRes infResponse = new MockTestInforRes();
        infResponse.setMockTestResponse(toMockTestFullResponse(mockTest));
        infResponse.setMockTestResultResponses(MockTestMapperUtil.mapToReadingListeningSubmissionResults(mockTest.getReadingListeningSubmissions()));
        return infResponse;
    }

    default MockTestInforRes toMockTestSpeakingResponse(MockTestEntity mockTest){
        if(mockTest == null) return null;
        MockTestInforRes infResponse = new MockTestInforRes();
        infResponse.setMockTestResponse(toMockTestFullResponse(mockTest));
        infResponse.setSpeakingSubmissionResults(MockTestMapperUtil.mapToSpeakingSubmissionResults(mockTest.getSpeakingSubmissions()));
        return infResponse;
    }

    MockTestPageRes toMockTestPageRes(IMockTestPageView mockTestPageView);

    List<MockTestPageRes> toMockTestPageResList(List<IMockTestPageView> mockTestPageViews);
}
