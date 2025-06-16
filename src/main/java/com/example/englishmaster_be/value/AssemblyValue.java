package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssemblyValue {

    @Value("${assembly.api-key}")
    String apiKey;

    @Value("${assembly.transcribe-audio.endpoint}")
    String transcribeAudioEndpoint;

    @Value("${assembly.transcript.endpoint}")
    String transcriptEndpoint;
}
