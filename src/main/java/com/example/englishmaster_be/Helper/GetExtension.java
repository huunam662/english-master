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
        String fileLink = link;

        switch (extension){
            case ".jpg":
            case ".JPG":
            case ".jpeg":
            case ".JPEG":
            case ".png":
            case ".PNG":
            case ".gif":
            case ".GIF":
                fileLink = link + "file/showImage/";
                break;
            case ".mp3":
                fileLink = link + "file/showAudio/";
                break;
            default:
                fileLink = "";
                break;
        }
        return fileLink;
    }

    public static String typeFile(String filename){
        String type ;
        String extension = GetExtension.getExtension(filename);

        switch (extension){
            case ".jpg":
            case ".JPG":
            case ".jpeg":
            case ".JPEG":
            case ".png":
            case ".PNG":
            case ".gif":
            case ".GIF":
                type = "IMAGE";
                break;
            case ".mp3":
                type = "AUDIO";
                break;
            default:
                type = "TEXT";
                break;
        }
        return type;
    }
}
