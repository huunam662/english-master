package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.value.LinkValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GetExtensionHelper {

    LinkValue linkValue;

    public String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    public String linkName(String name){

        String extension = this.getExtension(name);

        return switch (extension) {
            case ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF" -> linkValue.getLinkBE() + "file/showImage/";
            case ".mp3" -> linkValue.getLinkBE() + "file/showAudio/";
            default -> "";
        };
    }

    public String typeFile(String filename){

        String extension = this.getExtension(filename);

        return switch (extension) {
            case ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF" -> "IMAGE";
            case ".mp3" -> "AUDIO";
            default -> "TEXT";
        };
    }
}
