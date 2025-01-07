package com.example.englishmaster_be.domain.question_label.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.question_label.dto.request.QuestionLabelRequest;
import com.example.englishmaster_be.domain.question_label.service.IQuestionLabelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Question label")
@RestController
@RequestMapping("/question-labels")
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionLabelController {

    IQuestionLabelService labelService;

    @PostMapping("/add-label")
    @DefaultMessage("Save label successfully")
    public void addLabel(@RequestBody QuestionLabelRequest request) {
        labelService.addLabel(request);
    }
}
