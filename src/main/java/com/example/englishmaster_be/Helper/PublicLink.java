package com.example.englishmaster_be.Helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PublicLink {
    private static String link;

    @Value("${masterE.linkBE}")
    public void setLink(String link) {
        PublicLink.link = link;
    }

    public static String getLink() {
        return link;
    }
}
