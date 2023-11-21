package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.DTO.Answer.CreateListAnswerDTO;
import com.example.englishmaster_be.DTO.Question.CreateQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.*;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.*;
import org.json.simple.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private IFileStorageService IFileStorageService;
    @Autowired
    private ITopicService ITopicService;
    @Autowired
    private IPackService IPackService;
    @Autowired
    private IPartService IPartService;
    @Autowired
    private IContentService IContentService;
    @Autowired
    private IAnswerService IAnswerService;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private IQuestionService IQuestionService;
    @Autowired
    private JPAQueryFactory queryFactory;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createTopic(@ModelAttribute CreateTopicDTO createtopicDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            String filename = IFileStorageService.nameFile(createtopicDTO.getTopicImage());
            Pack pack = IPackService.findPackById(createtopicDTO.getTopicPack());

            Topic topic = new Topic(createtopicDTO.getTopicName(), filename, createtopicDTO.getTopicDescription(), createtopicDTO.getTopicType(),
                    createtopicDTO.getWorkTime(), createtopicDTO.getStartTime(), createtopicDTO.getEndTime());

            topic.setPack(pack);
            topic.setNumberQuestion(createtopicDTO.getNumberQuestion());
            topic.setUserCreate(user);
            topic.setUserUpdate(user);

            ITopicService.createTopic(topic);
            IFileStorageService.save(createtopicDTO.getTopicImage(), filename);

            for(UUID partId : createtopicDTO.getListPart()){
                ITopicService.addPartToTopic(topic.getTopicId(), partId);
            }

            TopicResponse topicResponse = new TopicResponse(topic);

            responseModel.setMessage("Create topic successfully");

            responseModel.setResponseData(topicResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Create topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PutMapping(value = "/{topicId:.+}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateTopic(@PathVariable UUID topicId, @RequestBody UpdateTopicDTO updateTopicDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);

            ITopicService.updateTopic(topic, updateTopicDTO);

            TopicResponse topicResponse = new TopicResponse(topic);

            responseModel.setMessage("Update topic successfully");
            responseModel.setResponseData(topicResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Update topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PutMapping(value = "/{topicId:.+}/uploadImage", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> uploadFileImage(@PathVariable UUID topicId, @ModelAttribute UploadFileDTO uploadFileDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            Topic topic = ITopicService.findTopicById(topicId);

            String filename = IFileStorageService.nameFile(uploadFileDTO.getContentData());
            IFileStorageService.delete(topic.getTopicImage());

            topic.setTopicImage(filename);
            topic.setUserUpdate(user);
            topic.setUpdateAt(LocalDateTime.now());

            TopicResponse topicResponse = new TopicResponse(topic);

            responseModel.setMessage("Update topic successfully");
            responseModel.setResponseData(topicResponse);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Update topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteTopic(@PathVariable UUID topicId){
        ResponseModel responseModel = new ResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);

            ITopicService.deleteTopic(topic);
            IFileStorageService.delete(topic.getTopicImage());

            responseModel.setMessage("Delete topic successfully");
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Delete topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/listTopic")
    public ResponseEntity<ResponseModel> getAllTopic(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                     @RequestParam(value = "size", defaultValue = "12") @Min(1) @Max(100) Integer size,
                                                     @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                     @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
                                                     @RequestParam(value = "pack", required = false) UUID packId,
                                                     @RequestParam(value = "search", required = false) String search,
                                                     @RequestParam(value = "type", required = false) String type,
                                                     Authentication authentication) {
        ResponseModel responseModel = new ResponseModel();

        try {
            JSONObject responseObject = new JSONObject();

            OrderSpecifier<?> orderSpecifier;

            if ("name".equalsIgnoreCase(sortBy)) {
                if(Sort.Direction.DESC.equals(sortDirection)){
                    orderSpecifier = QTopic.topic.topicName.lower().desc();
                }else {
                    orderSpecifier = QTopic.topic.topicName.lower().asc();
                }
            } else if ("updateAt".equalsIgnoreCase(sortBy)) {
                if(Sort.Direction.DESC.equals(sortDirection)){
                    orderSpecifier = QTopic.topic.updateAt.desc();
                }else {
                    orderSpecifier = QTopic.topic.updateAt.asc();
                }
            } else {
                if(Sort.Direction.DESC.equals(sortDirection)){
                    orderSpecifier = QTopic.topic.updateAt.desc();
                }else {
                    orderSpecifier = QTopic.topic.updateAt.asc();
                }
            }


            JPAQuery<Topic> query = queryFactory.selectFrom(QTopic.topic)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);

            if(authentication != null){
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_USER")) {
                        query.where(QTopic.topic.enable.eq(true));
                    }
                }
            }else {
                query.where(QTopic.topic.enable.eq(true));
            }

            if (packId != null) {
                query.where(QTopic.topic.pack.packId.eq(packId));
            }

            if (search != null && !search.isEmpty()) {
                query.where(QTopic.topic.topicName.lower().like("%" + search.toLowerCase() + "%"));
            }

            if (type != null && !type.isEmpty()) {
                query.where(QTopic.topic.topicType.lower().like(type.toLowerCase()));
            }

            long totalRecords = query.fetchCount();
            long totalPages = (long) Math.ceil((double) totalRecords / size);
            responseObject.put("totalPage", totalPages);
            responseObject.put("totalRecords", totalRecords);

            List<Topic> topicList = query.fetch();

            List<TopicResponse> topicResponseList = new ArrayList<>();

            for (Topic topic : topicList) {
                TopicResponse topicResponse = new TopicResponse(topic);
                topicResponseList.add(topicResponse);
            }

            responseObject.put("listTopic", topicResponseList);

            responseModel.setMessage("Show list topic successfully");
            responseModel.setResponseData(responseObject);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show list topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    @GetMapping(value = "/suggestTopic")
    public ResponseEntity<ResponseModel> get5SuggestTopic(@RequestParam(value = "query") String query) {
        ResponseModel responseModel = new ResponseModel();

        try {
            JSONArray responseArray = new JSONArray();

            for (Topic topic : ITopicService.get5TopicName(query)) {
                responseArray.add(topic.getTopicName());
            }

            responseModel.setMessage("Show list 5 topic name successfully");
            responseModel.setResponseData(responseArray);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show list 5 topic name fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addPartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId){
        ResponseModel responseModel = new ResponseModel();
        try{
            ITopicService.addPartToTopic(topicId, partId);

            responseModel.setMessage("Add part to topic successful");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            responseModel.setMessage("Add part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deletePartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId){
        ResponseModel responseModel = new ResponseModel();
        try{
            boolean check = ITopicService.deletePartToTopic(topicId, partId);

            if(check){
                responseModel.setMessage("Delete part to topic successful");
                responseModel.setStatus("success");
            }else{
                responseModel.setMessage("Delete part to topic fail: Topic don't have part");
                responseModel.setStatus("fail");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);

        }catch (Exception e) {
            responseModel.setMessage("Delete part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addQuestionToTopic(@PathVariable UUID topicId, @RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Question question = IQuestionService.findQuestionById(questionId);
            Topic topic = ITopicService.findTopicById(topicId);
            Part part = question.getPart();

            User user = IUserService.currentUser();


            if(ITopicService.existPartInTopic(topic, part)){
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());
                ITopicService.addQuestionToTopic(topic, question);
                responseModel.setMessage("Add question to topic successfully");
            }else{
                responseModel.setMessage("Part of question don't have in topic");
            }

            JSONObject response = new JSONObject();
            response.put("Topic Id", topicId);
            response.put("Question Id", questionId);


            responseModel.setResponseData(response);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Add question to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    @PostMapping(value = "/{topicId:.+}/addListQuestion", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addListQuestionToTopic(@PathVariable UUID topicId, @ModelAttribute("listQuestion") CreateListQuestionDTO createQuestionDTOList) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            for(CreateQuestionDTO createQuestionDTO : createQuestionDTOList.getListQuestion()){
                Question question = new Question();
                question.setQuestionContent(createQuestionDTO.getQuestionContent());
                question.setQuestionScore(createQuestionDTO.getQuestionScore());
                question.setUserCreate(user);
                question.setUserUpdate(user);



                Part part = IPartService.getPartToId(createQuestionDTO.getPartId());
                question.setPart(part);
                IQuestionService.createQuestion(question);


                if(createQuestionDTO.getListAnswer() != null && !createQuestionDTO.getListAnswer().isEmpty()){
                    for(CreateListAnswerDTO createListAnswerDTO: createQuestionDTO.getListAnswer() ){
                        Answer answer = new Answer();
                        answer.setQuestion(question);
                        answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                        answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                        answer.setUserUpdate(user);
                        answer.setUserCreate(user);

                        IAnswerService.createAnswer(answer);
                    }
                }

                if(createQuestionDTO.getListQuestionChild() != null && !createQuestionDTO.getListQuestionChild().isEmpty() ){
                    for (CreateQuestionDTO createQuestionChildDTO : createQuestionDTO.getListQuestionChild()){
                        Question questionChild = new Question();
                        questionChild.setQuestionId(UUID.randomUUID());
                        questionChild.setQuestionContent(createQuestionChildDTO.getQuestionContent());
                        questionChild.setQuestionScore(createQuestionChildDTO.getQuestionScore());
                        questionChild.setUserCreate(user);
                        questionChild.setUserUpdate(user);

                        for(CreateListAnswerDTO createListAnswerDTO: createQuestionChildDTO.getListAnswer() ){
                            Answer answer = new Answer();
                            answer.setQuestion(question);
                            answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                            answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                            answer.setUserUpdate(user);
                            answer.setUserCreate(user);

                            IAnswerService.createAnswer(answer);
                        }

                        IQuestionService.createQuestion(questionChild);
                    }

                }

                if(createQuestionDTO.getContentImage() != null && !createQuestionDTO.getContentImage().isEmpty()){
                    String filename = IFileStorageService.nameFile(createQuestionDTO.getContentImage());
                    Content content = new Content(question, GetExtension.typeFile(filename), filename);
                    content.setUserCreate(user);
                    content.setUserUpdate(user);

                    if(question.getContentCollection() == null){
                        question.setContentCollection(new ArrayList<>());
                    }
                    question.getContentCollection().add(content);
                    IContentService.uploadContent(content);
                    IFileStorageService.save(createQuestionDTO.getContentImage(), filename);
                }
                if(createQuestionDTO.getContentAudio() != null && !createQuestionDTO.getContentAudio().isEmpty()){
                    String filename = IFileStorageService.nameFile(createQuestionDTO.getContentAudio());
                    Content content = new Content(question, GetExtension.typeFile(filename), filename);
                    content.setUserCreate(user);
                    content.setUserUpdate(user);

                    if(question.getContentCollection() == null){
                        question.setContentCollection(new ArrayList<>());
                    }
                    question.getContentCollection().add(content);
                    IContentService.uploadContent(content);
                    IFileStorageService.save(createQuestionDTO.getContentImage(), filename);
                }

                IQuestionService.createQuestion(question);

                Topic topic = ITopicService.findTopicById(topicId);

                if(ITopicService.existPartInTopic(topic, part)){
                    topic.setUserUpdate(user);
                    topic.setUpdateAt(LocalDateTime.now());
                    ITopicService.addQuestionToTopic(topic, question);
                    responseModel.setMessage("Add question to topic successfully");
                }else{
                    responseModel.setMessage("Part of question don't have in topic");
                }
            }

            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Add question to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteQuestionToTopic(@PathVariable  UUID topicId, @RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            Topic topic = ITopicService.findTopicById(topicId);

            User user = IUserService.currentUser();

            if(ITopicService.existQuestionInTopic(topic, question)){
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());

                ITopicService.deleteQuestionToTopic(topic, question);
                responseModel.setMessage("Delete question to topic successfully");
            }else {
                responseModel.setMessage("Question don't have in topic");
            }

            JSONObject response = new JSONObject();
            response.put("Topic Id", topicId);
            response.put("Question Id", questionId);

            responseModel.setResponseData(response);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Delete question to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getPartToTopic(@PathVariable UUID topicId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Part> partList = ITopicService.getPartToTopic(topicId);

            JSONArray responseArray = new JSONArray();

            for(Part part : partList ){
                int totalQuestion = ITopicService.totalQuestion(part, topicId);

                PartResponse partResponse = new PartResponse(part, totalQuestion);

                responseArray.add(partResponse);
            }

            responseModel.setMessage("Show part to topic successfully");

            responseModel.setResponseData(responseArray);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getQuestionOfToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Question> questionList = ITopicService.getQuestionOfPartToTopic(topicId, partId);
            List<QuestionResponse> questionResponseList = new ArrayList<>();

            for(Question question : questionList ){
                QuestionResponse questionResponse = new QuestionResponse(question);
                if(IQuestionService.checkQuestionGroup(question)){
                    List<Question> questionGroupList = IQuestionService.listQuestionGroup(question);
                    List<QuestionResponse> questionGroupResponseList = new ArrayList<>();
                    for(Question questionGroup : questionGroupList){
                        QuestionResponse questionGroupResponse = new QuestionResponse(questionGroup);
                        questionGroupResponseList.add(questionGroupResponse);
                        questionResponse.setQuestionGroup(questionGroupResponseList);
                    }
                }

                questionResponseList.add(questionResponse);
            }

            responseModel.setMessage("Show question of part to topic successfully");
            responseModel.setResponseData(questionResponseList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show question of part to topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PatchMapping(value = "/{topicId:.+}/enableTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> enableTopic(@PathVariable UUID topicId, @RequestParam boolean enable) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);

            topic.setEnable(!enable);
            topic.setUpdateAt(LocalDateTime.now());
            ITopicService.createTopic(topic);

            if(enable){
                responseModel.setMessage("Enable topic successfully");
            }else{
                responseModel.setMessage("Disable topic fail");
            }
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Enable topic fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }
}
