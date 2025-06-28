package com.example.englishmaster_be.domain.cloudinary.dto.response;

import com.example.englishmaster_be.shared.dto.response.FileResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryFileResponse extends FileResponse {

    String publicId;

    String format;

    public CloudinaryFileResponse(Map cloudinaryResponse) {
        if(cloudinaryResponse != null) {
            this.setUrl((String) cloudinaryResponse.get("secure_url"));
            this.setType((String) cloudinaryResponse.get("resource_type"));
            this.setPublicId((String) cloudinaryResponse.get("public_id"));
            this.setFormat((String) cloudinaryResponse.get("format"));
        }
    }
}
