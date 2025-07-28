package com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.res;

import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionAnswersRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ReadingListeningDetailRes {

    UUID mockTestDetailId;
    String answerContent;
    Boolean isCorrectAnswer;
    Integer scoreAchieved;
    AnswerRes answerChoice;
    QuestionAnswersRes question;
}
