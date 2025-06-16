package com.example.englishmaster_be.common.constant.speaking_test;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SpeakingSendContent {

    Content("""
        -> Đề bài speaking: ":question"
        -> Hãy tìm lỗi phát âm và ngữ pháp xảy ra dựa trên phần nói của thí sinh: ":speakingText"
        -> Feedback bằng tiếng việt, phần nói của thí sinh phải là tiếng anh và có ý nghĩa với ngữ cảnh của đề bài. 
        Không được có từ "viết" hoặc chào hỏi người nói, hãy xưng người nói là bạn và gọi phần nói của thí sinh là chủ đề trong JSON template. 
        Đề cao đánh giá phát âm và ngữ pháp. Liệt kê các từ vựng cần thiết về nội dung đề bài cho người nói. 
        ReachedPercent chỉ mang giá trị là một số nguyên từ 0 đến 100, nếu phần nói của thí sinh không có ý nghĩa với ngữ cảnh của đề bài thì ReachedPercent mặc định là 10.
        Chỉ trả về kết quả đánh giá có cấu trúc như một JSON template bên dưới và không có gì thêm, hãy tuân thủ tiêu chuẩn mà tôi muốn.
        -> JSON template: {
            "feedback": "...",
            "reachedPercent": ...,
            "errors": {
                "pronunciations": [
                    {
                        "word": "...",
                        "feedback": "...",
                        "wordRecommend": "...",
                        "pronunciation": "...",
                        "pronunciationUrl": "https://translate.google.com/translate_tts?ie=UTF-8&tl=en&q=<wordRecommend>&client=tw-ob"
                    }
                ],
                "grammars": [
                    {
                        "word": "...",
                        "feedback": "...",
                        "wordRecommend": "..."
                    }
                ],
                "fluencies": [
                    {
                        "word": "...",
                        "feedback": "...",
                        "wordRecommend": "..."
                    }
                ],
                "vocabularies": [
                {
                    "wordRecommend": "...",
                    "pronunciation": "...",
                    "pronunciationUrl": "https://translate.google.com/translate_tts?ie=UTF-8&tl=en&q=<wordRecommend>&client=tw-ob"
                }
            ]
            }
        }
    """);

    String content;

    SpeakingSendContent(String content){
        this.content = content;
    }

}
