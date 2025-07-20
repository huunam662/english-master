package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FileValue {

    @Value("${englishmaster.fileSave}")
    private String fileSave;

    @Value("${englishmaster.linkFileShowImageBE}")
    private String prefixLinkFileShow;

}
