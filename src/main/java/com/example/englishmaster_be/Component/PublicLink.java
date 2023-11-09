package com.example.englishmaster_be.Component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PublicLink {
    private static String link;
    @Value("${masterE.linkFE}")
    public void setLink(String link) {
        PublicLink.link = link;
    }

    public static String getLink() {
        return link;
    }
}
