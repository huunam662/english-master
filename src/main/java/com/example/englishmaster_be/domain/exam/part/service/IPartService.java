package com.example.englishmaster_be.domain.exam.part.service;

import com.example.englishmaster_be.domain.exam.part.dto.req.CreatePartQuestionsAnswersReq;
import com.example.englishmaster_be.domain.exam.part.dto.req.EditPartQuestionsAnswersReq;
import com.example.englishmaster_be.domain.exam.part.dto.req.PartReq;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartKeyRes;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import java.util.List;
import java.util.UUID;

public interface IPartService {

    PartEntity savePart(PartReq partRequest);

    PartEntity getPartToId(UUID partId);

    List<PartEntity> getListPart();

    boolean isExistedPartNameWithDiff(PartEntity part, String partName);

    boolean isExistedPartName(String partName);

    void deletePart(UUID partId);

    PartKeyRes createPartAndQuestionsAnswers(CreatePartQuestionsAnswersReq request);

    PartKeyRes editPartAndQuestionsAnswers(EditPartQuestionsAnswersReq request);

    PartEntity getPartQuestionsAnswers(UUID partId);
    List<PartEntity> getPartsInTopicIds(List<UUID> topicIds);
}
