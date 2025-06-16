package com.example.englishmaster_be.domain.speaking_submission.util.bot;

import com.example.englishmaster_be.shared.util.gemini.SpringApplicationContext;
import com.example.englishmaster_be.value.ChatBotValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotSendContentUtil {

    public static Map<String, Object> contentToSend(String content){
        ChatBotValue chatBotValue = SpringApplicationContext.getBean(ChatBotValue.class);
        Map<String, Object> contentBox = new HashMap<>();
        contentBox.put("model", chatBotValue.getChatModel());
        contentBox.put("messages", List.of(
                Map.of(
                        "role", "user",
                        "content", content
                ))
        );
        return contentBox;
    }

}
