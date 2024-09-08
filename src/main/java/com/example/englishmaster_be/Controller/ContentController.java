package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.ContentResponse;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/content")
public class ContentController {
    @Autowired
    private IContentService IContentService;
    @Autowired
    private ContentRepository contentRepository;

    @GetMapping()
    public ResponseEntity<ResponseModel> getContentById(@RequestParam UUID id) {
        ResponseModel responseModel = new ResponseModel();
        Content content = IContentService.getContentToContentId(id);
        ContentResponse contentResponse = new ContentResponse(content);
        System.out.println(contentResponse);
        if (content != null) {
            responseModel.setMessage("Get content successful");
            responseModel.setResponseData(contentResponse);
            responseModel.setStatus("success");
            return ResponseEntity.ok(responseModel);
        }
        responseModel.setMessage("Get content failed");
        responseModel.setResponseData(null);
        responseModel.setStatus("failed");
        return ResponseEntity.ok(responseModel);
    }

    @GetMapping("contentData")
    public ResponseEntity<ResponseModel> getContentData(@RequestParam UUID topicId, @RequestParam String code) {
        ResponseModel responseModel = new ResponseModel();
        String content = contentRepository.findContentDataByTopicIdAndCode(topicId, code);
        if (content != null) {
            responseModel.setMessage("Get content successful");
            responseModel.setResponseData(content);
            responseModel.setStatus("success");
            return ResponseEntity.ok(responseModel);
        }
        responseModel.setMessage("Get content failed");
        responseModel.setResponseData(null);
        responseModel.setStatus("failed");
        return ResponseEntity.ok(responseModel);
    }

    @GetMapping("content")
    public ResponseEntity<ResponseModel> getContent(@RequestParam String contentData) {
        ResponseModel responseModel = new ResponseModel();
        Content content = contentRepository.findByContentData(contentData).orElse(null);
        ContentResponse contentResponse = new ContentResponse(content);
        if (content != null) {
            responseModel.setMessage("Get content successful");
            responseModel.setResponseData(contentResponse);
            responseModel.setStatus("success");
            return ResponseEntity.ok(responseModel);
        }
        responseModel.setMessage("Get content failed");
        responseModel.setResponseData(null);
        responseModel.setStatus("failed");
        return ResponseEntity.ok(responseModel);
    }
}
