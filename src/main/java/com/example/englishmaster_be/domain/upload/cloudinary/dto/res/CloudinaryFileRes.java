package com.example.englishmaster_be.domain.upload.cloudinary.dto.res;

import com.example.englishmaster_be.common.dto.res.FileRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CloudinaryFileRes extends FileRes {

    private String publicId;

    private String format;

    public CloudinaryFileRes(Map cloudinaryResponse) {
        if(cloudinaryResponse != null) {
            this.setUrl((String) cloudinaryResponse.get("secure_url"));
            this.setType((String) cloudinaryResponse.get("resource_type"));
            this.setPublicId((String) cloudinaryResponse.get("public_id"));
            this.setFormat((String) cloudinaryResponse.get("format"));
        }
    }
}
