package com.example.englishmaster_be.util;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestPartRequest;
import com.example.englishmaster_be.model.part.PartEntity;

import java.util.List;
import java.util.UUID;

public class PartUtil {

    public static List<UUID> convertToPartUUID(List<MockTestPartRequest> mockTestPartRequestList){

        if(mockTestPartRequestList == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "parts list from Mock test is null");

        return mockTestPartRequestList.stream().map(
                MockTestPartRequest::getPartId
        ).toList();
    }

    public static int totalScoreOfPart(PartEntity partEntity){

        if(partEntity == null) return 0;

        if(partEntity.getQuestions() == null) return 0;

        return QuestionUtil.totalScoreQuestionsParent(partEntity.getQuestions());
    }

}
