package com.example.englishmaster_be.domain.speaking_submission.util.bot;

import com.example.englishmaster_be.domain.speaking_submission.dto.response.BotFeedbackResponse;
import com.example.englishmaster_be.domain.speaking_submission.util.speaking.SpeakingUtil;
import com.example.englishmaster_be.shared.util.gemini.SpringApplicationContext;
import com.example.englishmaster_be.value.AssemblyValue;
import com.example.englishmaster_be.value.ChatBotValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "BOT-UTIL")
public class BotUtil {

    public static String speechToText(String audioUrl) throws InterruptedException {
        Assert.notNull(audioUrl, "audioUrl must not be null");
        AssemblyValue assemblyValue = SpringApplicationContext.getBean(AssemblyValue.class);
        RestTemplate restTemplate = SpringApplicationContext.getBean(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", assemblyValue.getApiKey());
        Map<String, String> body = new HashMap<>();
        body.put("audio_url", audioUrl);
        String urlRequest = assemblyValue.getTranscribeAudioEndpoint();
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                urlRequest,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                JsonNode.class
        );
        if(!response.getStatusCode().is2xxSuccessful()) return "";
        JsonNode responseBody = response.getBody();
        if(responseBody.get("status").asText().equalsIgnoreCase("completed"))
            return responseBody.get("text").asText();
        String idQueued = responseBody.get("id").asText();
        String transcriptUrl = assemblyValue.getTranscriptEndpoint();
        headers.remove("Content-Type");
        response = restTemplate.exchange(
                transcriptUrl.replace(":transcript_id", idQueued),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        if(!response.getStatusCode().is2xxSuccessful()) return "";
        responseBody = response.getBody();
        if(!responseBody.get("status").asText().equalsIgnoreCase("completed")){
            int repeatRequest = 0;
            while (++repeatRequest <= 3){
                Thread.sleep(1000);
                response = restTemplate.exchange(
                        transcriptUrl.replace(":transcript_id", idQueued),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        JsonNode.class
                );
                if(response.getStatusCode().is2xxSuccessful()) {
                    responseBody = response.getBody();
                    if(responseBody.get("status").asText().equalsIgnoreCase("completed"))
                        break;
                }
            }
            // nếu request 3 lần mà vẫn chưa nhan được text thì ngưng
            if (repeatRequest > 3) return "";
        }
        return responseBody.get("text").asText();
    }


    public static BotFeedbackResponse feedback4Speaking(String sendContent){
        Assert.notNull(sendContent, "Content to send is required.");
        ChatBotValue chatBotValue = SpringApplicationContext.getBean(ChatBotValue.class);
        RestTemplate restTemplate = SpringApplicationContext.getBean(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(chatBotValue.getApiKey());
        headers.set("accept", MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = BotSendContentUtil.contentToSend(sendContent);
        String urlRequest = chatBotValue.getChatEndpoint();
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                urlRequest,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                JsonNode.class
        );
        if(!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException(response.getBody().get("message").asText());
        String assistantContent = response.getBody().get("message").get("content").get(0).get("text").asText();
        String assistantContentJsonPretty = SpeakingUtil.parseToJsonPretty(assistantContent);
        try{
            ObjectMapper objectMapper = SpringApplicationContext.getBean(ObjectMapper.class);
            return objectMapper.readValue(assistantContentJsonPretty, BotFeedbackResponse.class);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
