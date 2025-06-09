package com.example.englishmaster_be.domain.part.service;

import com.example.englishmaster_be.domain.part.dto.request.CreatePartQuestionsAnswersRequest;
import com.example.englishmaster_be.domain.part.dto.request.EditPartQuestionsAnswersRequest;
import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartKeyResponse;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import java.util.List;
import java.util.UUID;

public interface IPartService {

    PartEntity savePart(PartRequest partRequest);

    PartEntity getPartToId(UUID partId);

    PartEntity getPartToName(String partName, String partType, TopicEntity topicEntity);

    List<PartEntity> getListPart();

    boolean isExistedPartNameWithDiff(PartEntity part, String partName);

    boolean isExistedPartName(String partName);

    void deletePart(UUID partId);

    PartKeyResponse createPartAndQuestionsAnswers(CreatePartQuestionsAnswersRequest request);

    PartKeyResponse editPartAndQuestionsAnswers(EditPartQuestionsAnswersRequest request);

    PartEntity getPartQuestionsAnswers(UUID partId);
}
