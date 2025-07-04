package com.example.englishmaster_be.domain.upload.util;

public class FileUtil {


    public static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }


    public static String mimeTypeFile(String filename){

        String extension = FileUtil.getExtension(filename);

        return switch (extension) {
            case ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF" -> String.format("image/%s", extension.replaceFirst(".", "").trim().toLowerCase());
            case ".mp3", ".m4a" -> "audio/mpeg";
            case ".mp4" -> "video/mp4";
            default -> "TEXT";
        };
    }
}
