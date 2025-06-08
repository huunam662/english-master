package com.example.englishmaster_be.shared.helper;

import com.example.englishmaster_be.shared.util.FileUtil;
import com.example.englishmaster_be.value.LinkValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileHelper {

    LinkValue linkValue;

    public String linkName(String name){

        String extension = FileUtil.getExtension(name);

        return switch (extension) {
            case ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF", ".mp3" -> linkValue.getLinkBE() + "file/show/";
            default -> "";
        };
    }
}
