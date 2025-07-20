package com.example.englishmaster_be.domain.exam.question.dto.res;

import com.example.englishmaster_be.domain.exam.part.dto.res.PartAndTotalQuestionRes;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.TopicBasicRes;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class QuestionPartRes {

    private TopicBasicRes topic;

    private PartAndTotalQuestionRes part;

    private List<QuestionRes> questionParents;

}
