package com.example.englishmaster_be.domain.upload.cloudinary.dto.res;

import com.example.englishmaster_be.domain.upload.cloudinary.dto.req.CloudinaryOptionsReq;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CloudinaryPageFileRes {

    private List<CloudinaryFileRes> elements;

    private Integer elementsSize;

    private String nextPageToken;

    private Integer size;

    public CloudinaryPageFileRes(Map cloudinaryResponse, CloudinaryOptionsReq params) {
        if(cloudinaryResponse != null){
            List<Map<String, Object>> resources = (List<Map<String, Object>>) cloudinaryResponse.get("resources");
            String nextCursor = (String) cloudinaryResponse.get("next_cursor");
            this.elements = resources.stream().map(CloudinaryFileRes::new).toList();
            this.nextPageToken = nextCursor;
            this.elementsSize = resources.size();
        }
        if(params != null) this.size = params.getSize();
    }
}
