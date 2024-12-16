package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.result_mock_test.dto.request.ResultMockTestRequest;
import com.example.englishmaster_be.domain.result_mock_test.dto.response.ResultMockTestResponse;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class ResultMockTestMapperImpl implements ResultMockTestMapper {

    @Override
    public void flowToResultMockTest(ResultMockTestRequest request, ResultMockTestEntity resultMockTestEntity) {
        if ( request == null ) {
            return;
        }

        resultMockTestEntity.setCorrectAnswer( request.getCorrectAnswer() );
        resultMockTestEntity.setScore( request.getScore() );
    }

    @Override
    public ResultMockTestResponse toResultMockTestResponse(ResultMockTestEntity resultMockTest) {
        if ( resultMockTest == null ) {
            return null;
        }

        ResultMockTestResponse.ResultMockTestResponseBuilder resultMockTestResponse = ResultMockTestResponse.builder();

        resultMockTestResponse.mockTestId( resultMockTestMockTestMockTestId( resultMockTest ) );
        resultMockTestResponse.resultMockTestId( resultMockTest.getResultMockTestId() );
        resultMockTestResponse.createAt( resultMockTest.getCreateAt() );
        resultMockTestResponse.updateAt( resultMockTest.getUpdateAt() );
        resultMockTestResponse.correctAnswer( resultMockTest.getCorrectAnswer() );
        resultMockTestResponse.score( resultMockTest.getScore() );

        resultMockTestResponse.part( PartMapper.INSTANCE.toPartResponse(resultMockTest.getPart()) );

        return resultMockTestResponse.build();
    }

    @Override
    public List<ResultMockTestResponse> toResultMockTestResponseList(List<ResultMockTestEntity> resultMockTestList) {
        if ( resultMockTestList == null ) {
            return null;
        }

        List<ResultMockTestResponse> list = new ArrayList<ResultMockTestResponse>( resultMockTestList.size() );
        for ( ResultMockTestEntity resultMockTestEntity : resultMockTestList ) {
            list.add( toResultMockTestResponse( resultMockTestEntity ) );
        }

        return list;
    }

    private UUID resultMockTestMockTestMockTestId(ResultMockTestEntity resultMockTestEntity) {
        MockTestEntity mockTest = resultMockTestEntity.getMockTest();
        if ( mockTest == null ) {
            return null;
        }
        return mockTest.getMockTestId();
    }
}
