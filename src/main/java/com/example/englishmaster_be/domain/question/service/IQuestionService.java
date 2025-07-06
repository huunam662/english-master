package com.example.englishmaster_be.domain.question.service;

import com.example.englishmaster_be.domain.question.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.question.dto.request.*;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface IQuestionService {

    QuestionEntity getQuestionById(UUID questionId);

    boolean checkQuestionGroup(UUID questionId);

    List<QuestionEntity> listQuestionGroup(QuestionEntity question);

    List<QuestionPartResponse> getAllPartQuestions(String partName, UUID topicId);

    void createListQuestionsParentOfPart(PartEntity part, List<CreateQuestionParentRequest> questionParentsRequest);

    void editListQuestionsParentOfPart(PartEntity part, List<EditQuestionParentRequest> questionParentsRequest);

    void deleteAllQuestions(List<UUID> questionIds);

    INumberAndScoreQuestionTopic getNumberAndScoreQuestionTopic(UUID topicId);

    void orderQuestionNumberToTopicId(UUID topicId);
}
