package com.example.englishmaster_be.domain.exam.question.dto.res;

import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class QuestionAnswersRes extends QuestionReadingListeningRes {

    private List<AnswerRes> answers;
}
