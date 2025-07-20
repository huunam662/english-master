package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Getter
public class LinkValue {

    @Value("${englishmaster.linkFileShowImageBE}")
    private String linkFileShowImageBE;

    @Value("${englishmaster.linkBE}")
    private String linkBE;

    @Value("${englishmaster.linkFE}")
    private String linkFE;

    @Value("${link.fe.mock-test.result.reading-listening}")
    private String linkFeMockTestResultReadingListening;

    @Value("${link.fe.mock-test.result.speaking}")
    private String linkFeMockTestResultSpeaking;

    @Value("${link.fe.mock-test.result.writing}")
    private String linkFeMockTestResultWriting;

    @Value("${link.fe.show-more-topic}")
    private String linkFeShowMoreTopic;
}
