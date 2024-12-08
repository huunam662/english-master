package com.example.englishmaster_be.Helper;

import org.springframework.stereotype.Component;

@Component
public class GetExtension {

    public static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    public static String linkName(String name){

        String link = PublicLink.getLink();

        String extension = GetExtension.getExtension(name);

        return switch (extension) {
            case ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF" -> link + "file/showImage/";
            case ".mp3" -> link + "file/showAudio/";
            default -> "";
        };
    }

    public static String typeFile(String filename){

        String extension = GetExtension.getExtension(filename);

        return switch (extension) {
            case ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF" -> "IMAGE";
            case ".mp3" -> "AUDIO";
            default -> "TEXT";
        };
    }
}
