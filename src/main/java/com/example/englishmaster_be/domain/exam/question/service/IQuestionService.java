package com.example.englishmaster_be.domain.exam.question.service;

import com.example.englishmaster_be.domain.exam.question.dto.req.CreateQuestionParentReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.EditQuestionParentReq;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.question.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionPartRes;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import java.util.List;
import java.util.UUID;


public interface IQuestionService {

    QuestionEntity getQuestionById(UUID questionId);

    boolean checkQuestionGroup(UUID questionId);

    List<QuestionEntity> listQuestionGroup(QuestionEntity question);

    List<QuestionPartRes> getAllPartQuestions(String partName, UUID topicId);

    void createListQuestionsParentOfPart(PartEntity part, List<CreateQuestionParentReq> questionParentsRequest);

    void editListQuestionsParentOfPart(PartEntity part, List<EditQuestionParentReq> questionParentsRequest);

    void deleteAllQuestions(List<UUID> questionIds);

    INumberAndScoreQuestionTopic getNumberAndScoreQuestionTopic(UUID topicId);

    void orderQuestionNumberToTopicId(UUID topicId);
}
