package com.example.englishmaster_be.domain.evaluator_writing.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserSubmissionResultResponse {
    List<WritingPart> writingPartList;
}
