package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/cdn")
public class ImageCdnLinkController {
    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private TopicRepository topicRepository;

    private final String linkBE = "https://gateway.dev.meu-solutions.com/englishmaster/api/file/showImage/";

    @GetMapping
    public ResponseEntity<ResponseModel> getImageCdnLink() {
        ResponseModel responseModel = new ResponseModel();
        List<String> listLinkCdn = contentRepository.findAllContentData();
        List<String> responseList = new ArrayList<>();
        if (!listLinkCdn.isEmpty()) {
            for (int i = 0; i < listLinkCdn.size(); i++) {
                if (!listLinkCdn.get(i).equalsIgnoreCase("")) {
                    responseList.add(linkBE + listLinkCdn.get(i));
                }
            }
        }
        responseModel.setMessage("Found " + responseList.size() + " links");
        responseModel.setStatus("success");
        responseModel.setResponseData(responseList);
        return ResponseEntity.ok(responseModel);
    }

    @GetMapping("topic")
    public ResponseEntity<ResponseModel> getImageCdnLinkTopic() {
        ResponseModel responseModel = new ResponseModel();
        List<String> listLinkCdn = topicRepository.findAllTopicImages();
        List<String> responseList = new ArrayList<>();
        if (!listLinkCdn.isEmpty()) {
            for (int i = 0; i < listLinkCdn.size(); i++) {
                if (!listLinkCdn.get(i).equalsIgnoreCase("")) {
                    responseList.add(linkBE + listLinkCdn.get(i));
                }
            }
        }
        responseModel.setMessage("Found " + responseList.size() + " links");
        responseModel.setStatus("success");
        responseModel.setResponseData(responseList);
        return ResponseEntity.ok(responseModel);
    }
}
