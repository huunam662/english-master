package com.example.englishmaster_be.domain.part.service;

import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestPartRequest;
import com.example.englishmaster_be.domain.part.dto.request.PartQuestionsAnswersRequest;
import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.domain.part.dto.response.CreatePartQuestionAnswerResponse;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.domain.part.dto.request.PartSaveContentRequest;
import com.example.englishmaster_be.model.part.PartEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IPartService {


    PartEntity savePart(PartRequest partRequest);

    PartEntity getPartToId(UUID partId);

    PartEntity getPartToName(String partName, String partType, TopicEntity topicEntity);

    PartEntity uploadFilePart(UUID partId, MultipartFile contentData);

    PartEntity uploadTextPart(UUID partId, PartSaveContentRequest uploadTextRequest);

    List<PartEntity> getPartsFromMockTestPartRequestList(List<MockTestPartRequest> mockTestPartRequestList);

    List<PartEntity> getListPart();

    boolean isExistedPartNameWithDiff(PartEntity part, String partName);

    boolean isExistedPartName(String partName);

    void deletePart(UUID partId);

    CreatePartQuestionAnswerResponse createPartAndQuestionsAnswers(PartQuestionsAnswersRequest request);

}
