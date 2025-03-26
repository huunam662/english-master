package com.example.englishmaster_be.domain.evaluator_writing.factory;

import com.example.englishmaster_be.domain.evaluator_writing.prompt.IeltsWritingTask1Prompt;
import com.example.englishmaster_be.domain.evaluator_writing.prompt.IeltsWritingTask2Prompt;
import com.example.englishmaster_be.domain.evaluator_writing.prompt.ToeicWritingPrompt;
import com.example.englishmaster_be.domain.evaluator_writing.prompt.WritingTaskPrompt;
import org.springframework.stereotype.Component;

@Component
public class WritingTaskPromptFactory {
    public WritingTaskPrompt createPrompt(String taskType) {
        switch (taskType.toLowerCase()) {
            case "ielts_writing_task_1":
                return new IeltsWritingTask1Prompt();
            case "ielts_writing_task_2":
                return new IeltsWritingTask2Prompt();
            case "toeic writing":
                return new ToeicWritingPrompt();
            default:
                return new IeltsWritingTask2Prompt();
        }
    }
}
