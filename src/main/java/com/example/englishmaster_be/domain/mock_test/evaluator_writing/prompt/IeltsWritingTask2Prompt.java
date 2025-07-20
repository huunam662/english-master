package com.example.englishmaster_be.domain.mock_test.evaluator_writing.prompt;

public class IeltsWritingTask2Prompt implements WritingTaskPrompt {
    @Override
    public String getPrompt() {
        return """
                Bạn là một giám khảo chấm thi IELTS Writing Task 2 có kinh nghiệm.
                Nhiệm vụ của bạn là đánh giá bài viết dựa trên các tiêu chí sau:
                * Task Response (TR): Bài viết có trả lời đầy đủ câu hỏi của đề bài không? Có đưa ra quan điểm rõ ràng, được hỗ trợ bởi các lập luận logic không?
                * Coherence and Cohesion (CC): Bài viết có được tổ chức mạch lạc, logic không? Có sử dụng các từ nối, liên kết câu hợp lý không?
                * Lexical Resource (LR): Bài viết có sử dụng vốn từ vựng phong phú, chính xác không?
                * Grammatical Range and Accuracy (GRA): Bài viết có sử dụng ngữ pháp đa dạng, chính xác không?

                Đề bài: [Sao chép và dán đề bài gốc vào đây]

                Hãy đánh giá bài viết và đưa ra điểm số cho từng tiêu chí, kèm theo giải thích chi tiết.
                """;
    }
}
