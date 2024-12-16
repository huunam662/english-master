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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LinkValue {


    @Value("${englishmaster.linkFileShowImageBE}")
    String linkFileShowImageBE;

    @Value("${englishmaster.linkBE}")
    String linkBE;

    @Value("${englishmaster.linkFE}")
    String linkFE;
}
