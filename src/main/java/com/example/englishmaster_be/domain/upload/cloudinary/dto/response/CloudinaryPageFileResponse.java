package com.example.englishmaster_be.domain.upload.cloudinary.dto.response;

import com.example.englishmaster_be.domain.upload.cloudinary.dto.request.CloudinaryOptionsRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.util.List;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryPageFileResponse {

    List<CloudinaryFileResponse> elements;

    Integer elementsSize;

    String nextPageToken;

    Integer size;

    public CloudinaryPageFileResponse(Map cloudinaryResponse, CloudinaryOptionsRequest params) {
        if(cloudinaryResponse != null){
            List<Map<String, Object>> resources = (List<Map<String, Object>>) cloudinaryResponse.get("resources");
            String nextCursor = (String) cloudinaryResponse.get("next_cursor");
            this.elements = resources.stream().map(CloudinaryFileResponse::new).toList();
            this.nextPageToken = nextCursor;
            this.elementsSize = resources.size();
        }
        if(params != null) this.size = params.getSize();
    }
}
