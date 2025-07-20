package com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.res;

import com.example.englishmaster_be.domain.exam.part.dto.res.PartBasicRes;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class ReadingListeningSubmissionRes {

    private PartBasicRes part;
    private Integer totalScoreResult;
    private Integer totalCorrect;
    private List<ReadingListeningDetailRes> mockTestDetails;

}
