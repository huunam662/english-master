package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestPartRequest;

import java.util.List;
import java.util.Objects;

public class MockTestHelper {


    public static int totalQuestionOfPart(MockTestPartRequest mockTestPartRequest) {

        if(mockTestPartRequest == null) return 0;

        if(mockTestPartRequest.getQuestionParentAnswers() == null) return 0;

        return mockTestPartRequest.getQuestionParentAnswers().stream()
                .filter(Objects::nonNull)
                .map(
                questionParentRequest -> {

                    if(questionParentRequest.getQuestionChildrenAnswers() == null)
                        return 0;

                    return questionParentRequest.getQuestionChildrenAnswers().size();
                }
        ).reduce(0, Integer::sum);
    }

    public static int totalQuestionOfPartRequestList(List<MockTestPartRequest> mockTestPartRequestList){

        if(mockTestPartRequestList == null) return 0;

        return mockTestPartRequestList.stream()
                .filter(Objects::nonNull)
                .map(
                MockTestHelper::totalQuestionOfPart
        ).reduce(0, Integer::sum);
    }

}
