package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.model.request.Question.LabelRequest;
import com.example.englishmaster_be.service.ILabelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Label")
@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionLabelController {
    private final ILabelService labelService;

    @PostMapping("/add-label")
    public void addLabel(@RequestBody LabelRequest request) {
        labelService.addLabel(request);
    }
}
