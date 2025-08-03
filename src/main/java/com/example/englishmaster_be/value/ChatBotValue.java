package com.example.englishmaster_be.value;

<<<<<<< HEAD
import lombok.Getter;
=======
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
<<<<<<< HEAD
public class ChatBotValue {

    @Value("${cohere.api-key}")
    private String apiKey;

    @Value("${cohere.chat-model}")
    private String chatModel;

    @Value("${cohere.chat-endpoint}")
    private String chatEndpoint;
=======
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatBotValue {

    @Value("${cohere.api-key}")
    String apiKey;

    @Value("${cohere.chat-model}")
    String chatModel;

    @Value("${cohere.chat-endpoint}")
    String chatEndpoint;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c

}
