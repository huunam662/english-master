package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.News.CreateNewsDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
@SuppressWarnings("unchecked")
public class NewsController {
    @Autowired
    private INewsService INewsService;
    @Autowired
    private IFileStorageService IFileStorageService;

    @Autowired
    private JPAQueryFactory queryFactory;

    @GetMapping(value = "/listNewsAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listNewsOfAdmin(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
                                                  @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                  @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
                                                  @RequestParam(value = "search", required = false) String search,
                                                  @RequestParam(value = "enable", required = false) String isEnable){
        ResponseModel responseModel = new ResponseModel();
        try {
            JSONObject responseObject = new JSONObject();
            OrderSpecifier<?> orderSpecifier;

            if(Sort.Direction.DESC.equals(sortDirection)){
                orderSpecifier = QNews.news.updateAt.desc();
            }else {
                orderSpecifier = QNews.news.updateAt.asc();
            }

            JPAQuery<News> query = queryFactory.selectFrom(QNews.news)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);

            if (search != null && !search.isEmpty()) {
                String likeExpression = "%" + search.toLowerCase().replace(" ", "%") + "%";

                query.where(QNews.news.title.lower().like(likeExpression)
                        .or(QNews.news.content.lower().like(likeExpression)));
            }
            if (isEnable != null){
                query.where(QNews.news.enable.eq(isEnable.equalsIgnoreCase("enable")));
            }

            long totalRecords = query.fetchCount();
            long totalPages = (long) Math.ceil((double) totalRecords / size);
            responseObject.put("totalRecords", totalRecords);
            responseObject.put("totalPages", totalPages);

            List<News> newsList = query.fetch();

            List<NewsResponse> newsResponseList = new ArrayList<>();

            for (News news : newsList) {
                NewsResponse newsResponse = new NewsResponse(news);
                newsResponseList.add(newsResponse);
            }

            responseObject.put("listNews", newsResponseList);
            responseModel.setMessage("List News successful");
            responseModel.setResponseData(responseObject);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("List News fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/listNewsUser")
    public ResponseEntity<ResponseModel> listNewsOfUser(@RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) Integer size){
        ResponseModel responseModel = new ResponseModel();
        try {
            OrderSpecifier<?> orderSpecifier = QNews.news.updateAt.desc();

            JPAQuery<News> query = queryFactory.selectFrom(QNews.news)
                    .orderBy(orderSpecifier)
                    .limit(size);

            query.where(QNews.news.enable.eq(true));

            List<News> newsList = query.fetch();

            List<NewsResponse> newsResponseList = new ArrayList<>();

            for (News news : newsList) {
                NewsResponse newsResponse = new NewsResponse(news);
                newsResponseList.add(newsResponse);
            }

            responseModel.setMessage("List News successful");
            responseModel.setResponseData(newsResponseList);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("List News fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/createNews" , consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<ResponseModel> createNews(@ModelAttribute("contentNews") CreateNewsDTO createNewsDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            News news = new News();
            news.setTitle(createNewsDTO.getTitle());
            news.setContent(createNewsDTO.getContent());

            String fileNameImage = null;
            MultipartFile image = createNewsDTO.getImage();
            if (image != null && !image.isEmpty()) {
                fileNameImage = IFileStorageService.nameFile(image);
            }

            if(fileNameImage != null){
                news.setImage(fileNameImage);
            }
            INewsService.save(news);

            if(fileNameImage != null){
                IFileStorageService.save(createNewsDTO.getImage(), fileNameImage);
            }
            NewsResponse newsResponse = new NewsResponse(news);
            responseModel.setMessage("Create News successful");
            responseModel.setResponseData(newsResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create News fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PatchMapping (value = "/{newsId:.+}/enableNews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> enableNews(@PathVariable UUID newsId, @RequestParam boolean enable){
        ResponseModel responseModel = new ResponseModel();
        try {

            News news = INewsService.findNewsById(newsId);
            news.setEnable(enable);

            INewsService.save(news);
            if(enable){
                responseModel.setMessage("Enable News successful");

            }else {
                responseModel.setMessage("Disable News successful");
            }


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            if(enable){
                exceptionResponseModel.setMessage("Enable News fail: " + e.getMessage());

            }else {
                exceptionResponseModel.setMessage("Disable News fail: "+ e.getMessage());

            }

            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PatchMapping(value = "/{newsId:.+}/updateNews", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateNews(@PathVariable UUID newsId, @ModelAttribute("contentNews") CreateNewsDTO createNewsDTO){
        ResponseModel responseModel = new ResponseModel();
        try {

            News news = INewsService.findNewsById(newsId);

            news.setTitle(createNewsDTO.getTitle());
            news.setContent(createNewsDTO.getContent());

            String fileNameImage = null;
            if(createNewsDTO.getImage() != null){
                MultipartFile image = createNewsDTO.getImage();
                if (!image.isEmpty()) {
                    fileNameImage = IFileStorageService.nameFile(image);
                }

                if(news.getImage() != null){
                    IFileStorageService.delete(news.getImage());
                }

                if(fileNameImage != null){
                    news.setImage(fileNameImage);
                }
                if(fileNameImage != null){
                    IFileStorageService.save(createNewsDTO.getImage(), fileNameImage);
                }
            }

            INewsService.save(news);

            NewsResponse newsResponse = new NewsResponse(news);
            responseModel.setMessage("Update News successful");
            responseModel.setResponseData(newsResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update News fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/{newsId:.+}/deleteNews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteNews(@PathVariable UUID newsId){
        ResponseModel responseModel = new ResponseModel();
        try {
            News news = INewsService.findNewsById(newsId);
            if(news.getImage() != null){
                IFileStorageService.delete(news.getImage());
            }

            INewsService.delete(news);

            responseModel.setMessage("Delete News successful");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete News fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }
    @GetMapping(value="/searchByTitle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> searchByTitle(@RequestParam("title") String title) {
        ResponseModel responseModel = new ResponseModel();
        try {
            // Sử dụng queryFactory để tìm kiếm các tin tức có tiêu đề chứa chuỗi "title"
            List<News> newsList = queryFactory.selectFrom(QNews.news)
                    .where(QNews.news.title.containsIgnoreCase(title))
                    .fetch();

            List<NewsResponse> newsResponseList = new ArrayList<>();
            for (News news : newsList) {
                NewsResponse newsResponse = new NewsResponse(news);
                newsResponseList.add(newsResponse);
            }

            responseModel.setMessage("Search by title successful");
            responseModel.setResponseData(newsResponseList);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Search by title fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

}
