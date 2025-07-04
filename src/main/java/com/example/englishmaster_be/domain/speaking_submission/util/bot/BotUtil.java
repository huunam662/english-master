package com.example.englishmaster_be.domain.speaking_submission.util.bot;

import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_submission.BotFeedbackResponse;
import com.example.englishmaster_be.domain.speaking_submission.util.speaking.SpeakingUtil;
import com.example.englishmaster_be.domain.evaluator_writing.util.SpringApplicationContext;
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
        // Ràng buộc dữ liệu đầu vào phải có giá trị (audioUrl)
        Assert.notNull(audioUrl, "audioUrl must not be null");
        // Lấy các instance cần thiết phục vụ cho chuyển đổi giọng nói thành văn bản
        AssemblyValue assemblyValue = SpringApplicationContext.getBean(AssemblyValue.class);
        RestTemplate restTemplate = SpringApplicationContext.getBean(RestTemplate.class);
        // Khởi tạo headers tiếp nhận các options cần thiết cho việc giao tiếp với API bên thứ 3 (AssemblyAI open API)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Gán api key được cấp sau khi đăng ký thành công dịch vụ API bên thứ 3 (AssemblyAI open API)
        headers.set("Authorization", assemblyValue.getApiKey());
        // Khởi tạo body sẽ gửi đi
        Map<String, String> body = new HashMap<>();
        // Audio url được thêm vào body, nhận được sau khi upload audio ghi âm speaking của thí sinh lên cloud
        body.put("audio_url", audioUrl);
        String urlRequest = assemblyValue.getTranscribeAudioEndpoint();
        // Tiến hành request bao gồm headers và body đến endpoint được cung cấp từ dịch vụ bên thứ 3 (AssemblyAI open API)
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                urlRequest,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                JsonNode.class
        );
        // Nếu nhận được status thất bại thì ném ra ngoại lệ ngay lập tức
        if(!response.getStatusCode().is2xxSuccessful()) throw new InterruptedException("Speech to text fail.");
        JsonNode responseBody = response.getBody();
        // Nếu nhận được status convert speech to text thành công trong lần request đầu tiên thì trả về đoạn text
        if(responseBody.get("status").asText().equalsIgnoreCase("completed"))
            return responseBody.get("text").asText();
        // Ngược lại, nếu gửi yêu cầu convert thành công nhưng hệ thống bên thứ 3 đang xếp request vào hàng chờ
        // thì tiến hành lấy ra id queued và sẽ sử dụng để lấy ra đoạn text trong các lần tiếp theo
        String idQueued = responseBody.get("id").asText();
        String transcriptUrl = assemblyValue.getTranscriptEndpoint();
        headers.remove("Content-Type");
        // Tiến hành request để lấy ra đoạn text
        response = restTemplate.exchange(
                transcriptUrl.replace(":transcript_id", idQueued),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        // Nếu vẫn nhận được status thất bại thì ném ra ngoại lệ ngay lập tức
        if(!response.getStatusCode().is2xxSuccessful()) throw new InterruptedException("Speech to text fail.");
        responseBody = response.getBody();
        // nếu vẫn nhận được status là queued thì tiến hành request lại tối đa 3 lần cho đến khi completed
        if(!responseBody.get("status").asText().equalsIgnoreCase("completed")){
            int repeatRequest = 0;
            while (++repeatRequest <= 3){
                // Chờ 2 giây sau mỗi lần request lại
                Thread.sleep(2000);
                response = restTemplate.exchange(
                        transcriptUrl.replace(":transcript_id", idQueued),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        JsonNode.class
                );
                // Nếu lấy text thành công thì trả về ngay
                if(response.getStatusCode().is2xxSuccessful()) {
                    responseBody = response.getBody();
                    if(responseBody.get("status").asText().equalsIgnoreCase("completed"))
                        break;
                }
            }
            // nếu request 3 lần mà vẫn chưa nhan được đoạn text thì ném ra ngoại lệ ngay lập tức
            if (repeatRequest > 3) throw new InterruptedException("Speech to text fail.");
        }
        // Nếu lần đầu send id queued và lấy được đoạn text thành công thì trả về ngay
        return responseBody.get("text").asText();
    }


    public static BotFeedbackResponse feedback4Speaking(String sendContent) throws InterruptedException {
        // Ràng buộc dữ liệu đầu vào phải có giá trị (sendContent)
        Assert.notNull(sendContent, "Content to send is required.");
        // Lấy các instance cần thiết phục vụ cho đánh giá content Speaking tương ứng với đề bài
        ChatBotValue chatBotValue = SpringApplicationContext.getBean(ChatBotValue.class);
        RestTemplate restTemplate = SpringApplicationContext.getBean(RestTemplate.class);
        // Khởi tạo headers tiếp nhận các options cần thiết cho việc giao tiếp với API bên thứ 3 (coHere chatbot open API)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Gán api key được cấp sau khi đăng ký thành công dịch vụ API bên thứ 3 (coHere chatbot open API)
        headers.setBearerAuth(chatBotValue.getApiKey());
        headers.set("accept", MediaType.APPLICATION_JSON_VALUE);
        // Khởi tạo body sẽ được gửi yêu cầu đánh giá đến cho dịch vụ bên thứ 3 (coHere chatbot open API)
        Map<String, Object> body = BotSendContentUtil.contentToSend(sendContent);
        String urlRequest = chatBotValue.getChatEndpoint();
        // Tiến hành request bao gồm headers và body đến endpoint được cung cấp từ dịch vụ bên thứ 3 (coHere chatbot open API)
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                urlRequest,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                JsonNode.class
        );
        // Nếu nhận được status thất bại thì ném ra ngoại lệ ngay lập tức
        if(!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException(response.getBody().get("message").asText());
        JsonNode responseBody = response.getBody();
        String finishReasonStatus = responseBody.get("finish_reason").asText();
        // nếu vẫn nhận được status không phải là complete thì tiến hành request lại tối đa 3 lần cho đến khi thỏa mãn
        if(!finishReasonStatus.equalsIgnoreCase("complete")){
            int repeatRequest = 0;
            while (++repeatRequest <= 3){
                // Chờ 2 giây sau mỗi lần request lại
                Thread.sleep(2000);
                response = restTemplate.exchange(
                        urlRequest,
                        HttpMethod.POST,
                        new HttpEntity<>(body, headers),
                        JsonNode.class
                );
                // Nếu lấy text thành công thì trả về ngay
                if(response.getStatusCode().is2xxSuccessful()) {
                    responseBody = response.getBody();
                    if(finishReasonStatus.equalsIgnoreCase("complete"))
                        break;
                }
            }
            // nếu request 3 lần mà vẫn chưa nhan được feedback thì ném ra ngoại lệ ngay lập tức
            if (repeatRequest > 3) throw new InterruptedException("Cannot send content to chatbot.");
        }
        // Sau khi nhận được feedback type json từ chatbot thành công thì tiến hành lọc chuỗi cho đúng định dạng.
        String assistantContent = responseBody.get("message").get("content").get(0).get("text").asText();
        String assistantContentJsonPretty = SpeakingUtil.parseToJsonPretty(assistantContent);
        try{
            // Sau khi nhận được feedback type json từ chatbot thành công thì tiến hành convert sang dto tương ứng
            ObjectMapper objectMapper = SpringApplicationContext.getBean(ObjectMapper.class);
            return objectMapper.readValue(assistantContentJsonPretty, BotFeedbackResponse.class);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
