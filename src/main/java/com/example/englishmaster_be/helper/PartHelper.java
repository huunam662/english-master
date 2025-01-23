package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestPartRequest;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.model.part.PartEntity;

import java.util.List;
import java.util.UUID;

public class PartHelper {

    public static List<UUID> convertToPartUUID(List<MockTestPartRequest> mockTestPartRequestList){

        if(mockTestPartRequestList == null)
            throw new BadRequestException("parts list from Mock test is null");

        return mockTestPartRequestList.stream().map(
                MockTestPartRequest::getPartId
        ).toList();
    }

    public static int totalScoreOfPart(PartEntity partEntity){

        if(partEntity == null) return 0;

        if(partEntity.getQuestions() == null) return 0;

        return QuestionHelper.totalScoreQuestionsParent(partEntity.getQuestions());
    }

}
