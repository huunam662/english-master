package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatBotValue {

    @Value("${cohere.api-key}")
    String apiKey;

    @Value("${cohere.chat-model}")
    String chatModel;

    @Value("${cohere.chat-endpoint}")
    String chatEndpoint;

}
