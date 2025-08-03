package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
<<<<<<< HEAD
public class AssemblyValue {

    @Value("${assembly.api-key}")
    private String apiKey;

    @Value("${assembly.transcribe-audio.endpoint}")
    private String transcribeAudioEndpoint;

    @Value("${assembly.transcript.endpoint}")
    private String transcriptEndpoint;
=======
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssemblyValue {

    @Value("${assembly.api-key}")
    String apiKey;

    @Value("${assembly.transcribe-audio.endpoint}")
    String transcribeAudioEndpoint;

    @Value("${assembly.transcript.endpoint}")
    String transcriptEndpoint;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
}
