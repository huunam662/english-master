package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AssemblyValue {

    @Value("${assembly.api-key}")
    private String apiKey;

    @Value("${assembly.transcribe-audio.endpoint}")
    private String transcribeAudioEndpoint;

    @Value("${assembly.transcript.endpoint}")
    private String transcriptEndpoint;
}
