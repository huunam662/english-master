package com.example.englishmaster_be.value;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ChatBotValue {

    @Value("${cohere.api-key}")
    private String apiKey;

    @Value("${cohere.chat-model}")
    private String chatModel;

    @Value("${cohere.chat-endpoint}")
    private String chatEndpoint;

}
