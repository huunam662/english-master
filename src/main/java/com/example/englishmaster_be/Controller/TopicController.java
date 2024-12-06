package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Constant.StatusConstant;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.Response.excel.CreateQuestionByExcelFileResponse;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.DTO.Answer.CreateListAnswerDTO;
import com.example.englishmaster_be.DTO.Question.CreateQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.*;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.Response.excel.CreateListQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateTopicByExcelFileResponse;
import com.example.englishmaster_be.Repository.AnswerRepository;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Repository.StatusRepository;
import com.example.englishmaster_be.Repository.UserRepository;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.Service.impl.TopicServiceImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.*;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/api/topic")
public class TopicController {

    private static final Logger log = LoggerFactory.getLogger(TopicController.class);
    @Autowired
    private IFileStorageService IFileStorageService;
    @Autowired
    private ITopicService ITopicService;
    @Autowired
    private IPackService IPackService;
    @Autowired
    private IPartService IPartService;
    @Autowired
    private ICommentService ICommentService;
    @Autowired
    private IContentService IContentService;
    @Autowired
    private IAnswerService IAnswerService;
    @Autowired
    private IQuestionService IQuestionService;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private IExcelService excelService;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private TopicServiceImpl topicServiceImpl;
    @Autowired
    private AnswerRepository answerRepository;


    @GetMapping(value = "/{topicId:.+}/inforTopic")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getInformationTopic(@PathVariable UUID topicId) {
        ResponseModel responseModel = new ResponseModel();

        try {
            Topic topic = ITopicService.findTopicById(topicId);

            responseModel.setMessage("Show list Topic successfully");
            responseModel.setResponseData(new TopicResponse(topic));


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createTopic(@ModelAttribute CreateTopicDTO createTopicDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            String filename = IFileStorageService.nameFile(createTopicDTO.getTopicImage());
            Pack pack = IPackService.findPackById(createTopicDTO.getTopicPack());

            Topic topic = new Topic(
                    createTopicDTO.getTopicName(),
                    filename,
                    createTopicDTO.getTopicDescription(),
                    createTopicDTO.getTopicType(),
                    createTopicDTO.getWorkTime(),
                    null,
                    null
            );

            topic.setPack(pack);
            topic.setNumberQuestion(createTopicDTO.getNumberQuestion());
            topic.setUserCreate(user);
            topic.setUserUpdate(user);
            topic.setStatus(statusRepository.findByStatusName(StatusConstant.ACTIVE).orElse(null));

            ITopicService.createTopic(topic);
            IFileStorageService.save(createTopicDTO.getTopicImage());

            createTopicDTO.getListPart().forEach(partId -> ITopicService.addPartToTopic(topic.getTopicId(), partId));

            TopicResponse topicResponse = new TopicResponse(topic);
            responseModel.setMessage("Create Topic successfully");
            responseModel.setResponseData(topicResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create Topic failed: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/createTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createTopicByExcelFile(@RequestParam("file") MultipartFile file, @RequestParam("url") String url) {
        ResponseModel responseModel = new ResponseModel();
        try {
            // Parse dữ liệu từ file
            CreateTopicByExcelFileResponse createTopicByExcelFileDTO = excelService.parseCreateTopicDTO(file);

            User user = IUserService.currentUser();

            // Tìm pack dựa trên ID từ DTO
            Pack pack = IPackService.findPackById(createTopicByExcelFileDTO.getTopicPackId());
            if (pack == null) {
                throw new Exception("Pack not found with ID: " + createTopicByExcelFileDTO.getTopicPackId());
            }

            // Tạo đối tượng Topic
            Topic topic = new Topic(
                    createTopicByExcelFileDTO.getTopicName(),
                    null,
                    createTopicByExcelFileDTO.getTopicDescription(),
                    createTopicByExcelFileDTO.getTopicType(),
                    createTopicByExcelFileDTO.getWorkTime(),
                    createTopicByExcelFileDTO.getStartTime(),
                    createTopicByExcelFileDTO.getEndTime()
            );

            topic.setPack(pack);

            // Xử lý ảnh chủ đề
            if (createTopicByExcelFileDTO.getTopicImageName() == null || createTopicByExcelFileDTO.getTopicImageName().isEmpty()) {
                topic.setTopicImage(url);
            }

            // Cập nhật các thông tin liên quan đến số lượng câu hỏi, người tạo và trạng thái
            topic.setNumberQuestion(createTopicByExcelFileDTO.getNumberQuestion());
            topic.setUserCreate(user);  // Gán người tạo
            topic.setUserUpdate(user);  // Gán người cập nhật
            topic.setStatus(statusRepository.findByStatusName(StatusConstant.ACTIVE)
                    .orElseThrow(() -> new CustomException(Error.STATUS_NOT_FOUND)));

            // Lưu topic vào cơ sở dữ liệu
            ITopicService.createTopic(topic);

            // Thêm các phần vào topic
            createTopicByExcelFileDTO.getListPart().forEach(partId -> ITopicService.addPartToTopic(topic.getTopicId(), partId));

            // Tạo response với thông tin của topic
            TopicResponse topicResponse = new TopicResponse(topic);
            responseModel.setMessage("Create Topic successfully");
            responseModel.setResponseData(topicResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (CustomException ce) {
            // Xử lý lỗi do người dùng không hợp lệ hoặc các lỗi tùy chỉnh khác
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create Topic failed: " + ce.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.BAD_REQUEST));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
        } catch (Exception e) {
            // Xử lý các lỗi không mong muốn
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create Topic failed: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }



    @PutMapping(value = "/{topicId:.+}/updateTopic", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateTopic(@PathVariable UUID topicId, @ModelAttribute UpdateTopicDTO updateTopicDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            String filename = null;
            MultipartFile image = updateTopicDTO.getTopicImage();
            if (image != null && !image.isEmpty()) {
                filename = IFileStorageService.nameFile(image);
            }
            Pack pack = IPackService.findPackById(updateTopicDTO.getTopicPack());
            Topic topic = ITopicService.findTopicById(topicId);
            topic.setTopicName(updateTopicDTO.getTopicName());
            topic.setTopicDescription(updateTopicDTO.getTopicDescription());
            if (filename != null) {
                topic.setTopicImage(filename);
            }
            topic.setTopicType(updateTopicDTO.getTopicType());
            topic.setWorkTime(updateTopicDTO.getWorkTime());
            topic.setStartTime(updateTopicDTO.getStartTime());
            topic.setEndTime(updateTopicDTO.getEndTime());
            topic.setPack(pack);
            topic.setNumberQuestion(updateTopicDTO.getNumberQuestion());
            topic.setUserUpdate(user);
            if (filename != null && topic.getTopicImage() != null && IFileStorageService.load(topic.getTopicImage()) != null) {
                IFileStorageService.delete(topic.getTopicImage());
            }
            ITopicService.createTopic(topic);
            if (filename != null) {
                IFileStorageService.save(updateTopicDTO.getTopicImage());
            }
            List<Part> listPart = new ArrayList<>();
            for (UUID partId : updateTopicDTO.getListPart()) {
                Part part = IPartService.getPartToId(partId);
                listPart.add(part);
            }
            topic.setParts(listPart);
            ITopicService.createTopic(topic);
            TopicResponse topicResponse = new TopicResponse(topic);
            responseModel.setMessage("Update Topic successfully");
            responseModel.setResponseData(topicResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PutMapping(value = "/{topicId:.+}/uploadImage", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> uploadFileImage(@PathVariable UUID topicId, @ModelAttribute UploadFileDTO uploadFileDTO) {
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
            responseModel.setMessage("Update Topic successfully");
            responseModel.setResponseData(topicResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteTopic(@PathVariable UUID topicId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);
            ITopicService.deleteTopic(topic);
            IFileStorageService.delete(topic.getTopicImage());
            responseModel.setMessage("Delete Topic successfully");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (IllegalArgumentException e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage(e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseModel);
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
                if (Sort.Direction.DESC.equals(sortDirection)) {
                    orderSpecifier = QTopic.topic.topicName.lower().desc();
                } else {
                    orderSpecifier = QTopic.topic.topicName.lower().asc();
                }
            } else if ("updateAt".equalsIgnoreCase(sortBy)) {
                if (Sort.Direction.DESC.equals(sortDirection)) {
                    orderSpecifier = QTopic.topic.updateAt.desc();
                } else {
                    orderSpecifier = QTopic.topic.updateAt.asc();
                }
            } else {
                if (Sort.Direction.DESC.equals(sortDirection)) {
                    orderSpecifier = QTopic.topic.updateAt.desc();
                } else {
                    orderSpecifier = QTopic.topic.updateAt.asc();
                }
            }

            JPAQuery<Topic> query = queryFactory.selectFrom(QTopic.topic)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);

            if (authentication != null) {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_USER")) {
                        query.where(QTopic.topic.enable.eq(true));
                    }
                }
            } else {
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

            responseModel.setMessage("Show list Topic successfully");
            responseModel.setResponseData(responseObject);



            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping("/getTopic")
    public ResponseEntity<ResponseModel> getTopic(@RequestParam(value = "id", required = false) UUID id) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        if (id == null) {
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            exceptionResponseModel.setMessage("Topic ID is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
        }

        JPAQuery<Topic> query = queryFactory.selectFrom(QTopic.topic)
                .where(QTopic.topic.topicId.eq(id));
        Topic topic = query.fetchOne();

        if (topic == null) {
            exceptionResponseModel.setStatus(HttpStatus.NOT_FOUND);
            exceptionResponseModel.setMessage("Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseModel);
        }

        TopicResponse topicResponse = new TopicResponse(topic);

        responseModel.setResponseData(topicResponse);
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }


    @GetMapping(value = "/suggestTopic")
    public ResponseEntity<ResponseModel> get5SuggestTopic(@RequestParam(value = "query") String query) {
        ResponseModel responseModel = new ResponseModel();

        try {
            JSONArray responseArray = new JSONArray();

            for (Topic topic : ITopicService.get5TopicName(query)) {
                responseArray.add(topic.getTopicName());
            }

            responseModel.setMessage("Show list 5 Topic name successfully");
            responseModel.setResponseData(responseArray);



            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list 5 Topic name fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addPartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            ITopicService.addPartToTopic(topicId, partId);

            responseModel.setMessage("Add Part to Topic successful");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Part to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deletePartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            boolean check = ITopicService.deletePartToTopic(topicId, partId);

            if (check) {
                responseModel.setMessage("Delete Part to Topic successful");

            } else {
                exceptionResponseModel.setMessage("Delete Part to Topic fail: Topic don't have Part");
                exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);

            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);

        } catch (Exception e) {
            exceptionResponseModel.setMessage("Delete Part to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addQuestionToTopic(@PathVariable UUID topicId, @ModelAttribute CreateQuestionDTO createQuestionDTO) {
        ResponseModel responseModel = new ResponseModel();

        User user = IUserService.currentUser();
        try {

            Question question = new Question();
            question.setQuestionContent(createQuestionDTO.getQuestionContent());
            question.setQuestionScore(createQuestionDTO.getQuestionScore());
            question.setUserCreate(user);
            question.setUserUpdate(user);

            Part part = IPartService.getPartToId(createQuestionDTO.getPartId());
            question.setPart(part);
            IQuestionService.createQuestion(question);


            if (createQuestionDTO.getListAnswer() != null && !createQuestionDTO.getListAnswer().isEmpty()) {
                for (CreateListAnswerDTO createListAnswerDTO : createQuestionDTO.getListAnswer()) {
                    Answer answer = new Answer();
                    answer.setQuestion(question);
                    answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                    answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                    answer.setUserUpdate(user);
                    answer.setUserCreate(user);

                    answerRepository.save(answer);
                    if (question.getAnswers() == null) {
                        question.setAnswers(new ArrayList<>());
                    }
                    question.getAnswers().add(answer);
                }
            }

            if (createQuestionDTO.getListQuestionChild() != null && !createQuestionDTO.getListQuestionChild().isEmpty()) {
                for (CreateQuestionDTO createQuestionChildDTO : createQuestionDTO.getListQuestionChild()) {
                    Question questionChild = new Question();
                    questionChild.setQuestionGroup(question);
                    questionChild.setQuestionContent(createQuestionChildDTO.getQuestionContent());
                    questionChild.setQuestionScore(createQuestionChildDTO.getQuestionScore());
                    questionChild.setPart(question.getPart());
                    questionChild.setUserCreate(user);
                    questionChild.setUserUpdate(user);

                    IQuestionService.createQuestion(questionChild);

                    for (CreateListAnswerDTO createListAnswerDTO : createQuestionChildDTO.getListAnswer()) {
                        Answer answer = new Answer();
                        answer.setQuestion(questionChild);
                        answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                        answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                        answer.setUserUpdate(user);
                        answer.setUserCreate(user);

                        answerRepository.save(answer);
                        if (questionChild.getAnswers() == null) {
                            questionChild.setAnswers(new ArrayList<>());
                        }
                        questionChild.getAnswers().add(answer);
                    }

                    IQuestionService.createQuestion(questionChild);
                }

            }

            if (createQuestionDTO.getContentImage() != null && !createQuestionDTO.getContentImage().isEmpty()) {
                String filename = IFileStorageService.nameFile(createQuestionDTO.getContentImage());
                Content content = new Content(question, GetExtension.typeFile(filename), filename);
                content.setUserCreate(user);
                content.setUserUpdate(user);

                if (question.getContentCollection() == null) {
                    question.setContentCollection(new ArrayList<>());
                }
                question.getContentCollection().add(content);
                IContentService.uploadContent(content);
                IFileStorageService.save(createQuestionDTO.getContentImage());
            }
            if (createQuestionDTO.getContentAudio() != null && !createQuestionDTO.getContentAudio().isEmpty()) {
                String filename = IFileStorageService.nameFile(createQuestionDTO.getContentAudio());
                Content content = new Content(question, GetExtension.typeFile(filename), filename);
                content.setUserCreate(user);
                content.setUserUpdate(user);

                if (question.getContentCollection() == null) {
                    question.setContentCollection(new ArrayList<>());
                }
                question.getContentCollection().add(content);
                IContentService.uploadContent(content);
                IFileStorageService.save(createQuestionDTO.getContentAudio());
            }

            IQuestionService.createQuestion(question);

            Topic topic = ITopicService.findTopicById(topicId);

            if (ITopicService.existPartInTopic(topic, part)) {
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());
                ITopicService.addQuestionToTopic(topic, question);

                Question question1 = IQuestionService.findQuestionById(question.getQuestionId());
                QuestionResponse questionResponse = new QuestionResponse(question1);

                if (IQuestionService.checkQuestionGroup(question1)) {
                    List<Question> questionGroupList = IQuestionService.listQuestionGroup(question1);
                    List<QuestionResponse> questionGroupResponseList = new ArrayList<>();

                    for (Question questionGroup : questionGroupList) {
                        QuestionResponse questionGroupResponse;

                        Answer answerCorrect = IAnswerService.correctAnswer(questionGroup);
                        questionGroupResponse = new QuestionResponse(questionGroup, answerCorrect);
                        questionGroupResponseList.add(questionGroupResponse);
                        questionResponse.setQuestionGroup(questionGroupResponseList);
                    }
                } else {
                    Answer answerCorrect = IAnswerService.correctAnswer(question1);
                    questionResponse.setAnswerCorrect(answerCorrect.getAnswerId());
                }
                responseModel.setResponseData(questionResponse);
                responseModel.setMessage("Add Question to Topic successfully");
            } else {
                responseModel.setMessage("Part of Question don't have in Topic");
            }


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestion", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addListQuestionToTopic(@PathVariable UUID topicId, @ModelAttribute("listQuestion") CreateListQuestionDTO createQuestionDTOList) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            for (CreateQuestionDTO createQuestionDTO : createQuestionDTOList.getListQuestion()) {
                Question question = new Question();
                question.setQuestionContent(createQuestionDTO.getQuestionContent());
                question.setQuestionScore(createQuestionDTO.getQuestionScore());
                question.setUserCreate(user);
                question.setUserUpdate(user);

                Part part = IPartService.getPartToId(createQuestionDTO.getPartId());
                question.setPart(part);
                IQuestionService.createQuestion(question);


                if (createQuestionDTO.getListAnswer() != null && !createQuestionDTO.getListAnswer().isEmpty()) {
                    for (CreateListAnswerDTO createListAnswerDTO : createQuestionDTO.getListAnswer()) {
                        Answer answer = new Answer();
                        answer.setQuestion(question);
                        answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                        answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                        answer.setUserUpdate(user);
                        answer.setUserCreate(user);

                        answerRepository.save(answer);
                    }
                }

                if (createQuestionDTO.getListQuestionChild() != null && !createQuestionDTO.getListQuestionChild().isEmpty()) {
                    for (CreateQuestionDTO createQuestionChildDTO : createQuestionDTO.getListQuestionChild()) {
                        Question questionChild = new Question();
                        questionChild.setQuestionGroup(question);
                        questionChild.setQuestionContent(createQuestionChildDTO.getQuestionContent());
                        questionChild.setQuestionScore(createQuestionChildDTO.getQuestionScore());
                        questionChild.setPart(question.getPart());
                        questionChild.setUserCreate(user);
                        questionChild.setUserUpdate(user);

                        IQuestionService.createQuestion(questionChild);

                        for (CreateListAnswerDTO createListAnswerDTO : createQuestionChildDTO.getListAnswer()) {
                            Answer answer = new Answer();
                            answer.setQuestion(questionChild);
                            answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                            answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                            answer.setUserUpdate(user);
                            answer.setUserCreate(user);

                            answerRepository.save(answer);
                            if (questionChild.getAnswers() == null) {
                                questionChild.setAnswers(new ArrayList<>());
                            }
                            questionChild.getAnswers().add(answer);
                        }

                        IQuestionService.createQuestion(questionChild);
                    }

                }

                if (createQuestionDTO.getContentImage() != null && !createQuestionDTO.getContentImage().isEmpty()) {
                    String filename = IFileStorageService.nameFile(createQuestionDTO.getContentImage());
                    Content content = new Content(question, GetExtension.typeFile(filename), filename);
                    content.setUserCreate(user);
                    content.setUserUpdate(user);

                    if (question.getContentCollection() == null) {
                        question.setContentCollection(new ArrayList<>());
                    }
                    question.getContentCollection().add(content);
                    IContentService.uploadContent(content);
                    IFileStorageService.save(createQuestionDTO.getContentImage());
                }
                if (createQuestionDTO.getContentAudio() != null && !createQuestionDTO.getContentAudio().isEmpty()) {
                    String filename = IFileStorageService.nameFile(createQuestionDTO.getContentAudio());
                    Content content = new Content(question, GetExtension.typeFile(filename), filename);
                    content.setUserCreate(user);
                    content.setUserUpdate(user);

                    if (question.getContentCollection() == null) {
                        question.setContentCollection(new ArrayList<>());
                    }
                    question.getContentCollection().add(content);
                    IContentService.uploadContent(content);
                    IFileStorageService.save(createQuestionDTO.getContentAudio());
                }

                IQuestionService.createQuestion(question);

                Topic topic = ITopicService.findTopicById(topicId);

                if (ITopicService.existPartInTopic(topic, part)) {
                    topic.setUserUpdate(user);
                    topic.setUpdateAt(LocalDateTime.now());
                    ITopicService.addQuestionToTopic(topic, question);
                    responseModel.setMessage("Add Question to Topic successfully");
                } else {
                    responseModel.setMessage("Part of Question don't have in Topic");
                }
            }


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }



    @PostMapping(value = "/{topicId:.+}/addListQuestionPart34ToTopicByExcelFile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addListQuestionPart34ToTopicByExcelFile(@PathVariable UUID topicId, @RequestParam("file") MultipartFile file, @RequestParam("part") int partName) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            CreateListQuestionByExcelFileResponse excelFileDTO = excelService.parseListeningPart34DTO(topicId, file, partName);
            processQuestions(excelFileDTO, topicId, user, responseModel);
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestionPart5ToTopicByExcelFile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addListQuestionPart5ToTopicByExcelFile(@PathVariable UUID topicId, @RequestParam("file") MultipartFile file) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            CreateListQuestionByExcelFileResponse excelFileDTO = excelService.parseReadingPart5DTO(topicId, file);

            processQuestions(excelFileDTO, topicId, user, responseModel);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestionPart67ToTopicByExcelFile", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseModel> addListQuestionPart67ToTopicByExcelFile(@PathVariable UUID topicId, @RequestParam("file") MultipartFile file, @RequestParam("part") int partName) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            CreateListQuestionByExcelFileResponse excelFileDTO = excelService.parseReadingPart67DTO(topicId, file, partName);

            processQuestions(excelFileDTO, topicId, user, responseModel);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestionPart12ToTopicByExcelFile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addListQuestionPart12ToTopicByExcelFile(@PathVariable UUID topicId, @RequestParam("file") MultipartFile file, @RequestParam("part") int partName) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            CreateListQuestionByExcelFileResponse excelFileDTO = excelService.parseListeningPart12DTO(topicId, file, partName);
            processQuestions(excelFileDTO, topicId, user, responseModel);
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }
    @PostMapping(value = "/{topicId:.+}/addAllPartsToTopicByExcelFile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addAllPartsToTopicByExcelFile(@PathVariable UUID topicId, @RequestParam("file") MultipartFile file) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();
            CreateListQuestionByExcelFileResponse excelFileDTO = excelService.parseAllPartsDTO(topicId, file);
            processQuestions(excelFileDTO, topicId, user, responseModel);
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Add Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @DeleteMapping(value = "/{topicId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteQuestionToTopic(@PathVariable UUID topicId, @RequestParam UUID questionId) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Question question = IQuestionService.findQuestionById(questionId);
            Topic topic = ITopicService.findTopicById(topicId);

            User user = IUserService.currentUser();

            if (ITopicService.existQuestionInTopic(topic, question)) {
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());

                ITopicService.deleteQuestionToTopic(topic, question);
                responseModel.setMessage("Delete Question to Topic successfully");
            } else {
                responseModel.setMessage("Question don't have in Topic");
            }

            JSONObject response = new JSONObject();
            response.put("Topic Id", topicId);
            response.put("Question Id", questionId);

            responseModel.setResponseData(response);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete Question to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getPartToTopic(@PathVariable UUID topicId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Part> partList = ITopicService.getPartToTopic(topicId);

            JSONArray responseArray = new JSONArray();

            for (Part part : partList) {
                int totalQuestion = ITopicService.totalQuestion(part, topicId);

                PartResponse partResponse = new PartResponse(part, totalQuestion);

                responseArray.add(partResponse);
            }

            responseModel.setMessage("Show Part to Topic successfully");

            responseModel.setResponseData(responseArray);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show Part to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getQuestionOfToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = authentication.getAuthorities().iterator().next().getAuthority();
        ResponseModel responseModel = new ResponseModel();
        try {
            List<Question> questionList = ITopicService.getQuestionOfPartToTopic(topicId, partId);
            List<QuestionResponse> questionResponseList = new ArrayList<>();
            for (Question question : questionList) {
                QuestionResponse questionResponse = new QuestionResponse(question);

                if (IQuestionService.checkQuestionGroup(question)) {
                    List<Question> questionGroupList = IQuestionService.listQuestionGroup(question);
                    List<QuestionResponse> questionGroupResponseList = new ArrayList<>();

                    for (Question questionGroup : questionGroupList) {
                        QuestionResponse questionGroupResponse;
                        if (userRole.equals("ROLE_ADMIN")) {
                            Answer answerCorrect = IAnswerService.correctAnswer(questionGroup);
                            questionGroupResponse = new QuestionResponse(questionGroup, answerCorrect);
                        } else {
                            questionGroupResponse = new QuestionResponse(questionGroup);
                        }
                        questionGroupResponseList.add(questionGroupResponse);
                        questionResponse.setQuestionGroup(questionGroupResponseList);
                    }
                } else {
                    if (userRole.equals("ROLE_ADMIN")) {
                        Answer answerCorrect = IAnswerService.correctAnswer(question);
                        questionResponse.setAnswerCorrect(answerCorrect.getAnswerId());
                    }
                }
                if (question.getContentCollection().size() > 1) {
                    questionResponseList.add(0, questionResponse);
                } else {
                    questionResponseList.add(questionResponse);
                }
            }

            responseModel.setMessage("Show Question of Part to Topic successfully");
            responseModel.setResponseData(questionResponseList);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show Question of Part to Topic fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @PatchMapping(value = "/{topicId:.+}/enableTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> enableTopic(@PathVariable UUID topicId, @RequestParam boolean enable) {
        ResponseModel responseModel = new ResponseModel();

        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);

            if (topic == null) {

                exceptionResponseModel.setMessage("Topic not found");
                exceptionResponseModel.setStatus(HttpStatus.NOT_FOUND);
                exceptionResponseModel.setViolations(String.valueOf(HttpStatus.NOT_FOUND));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseModel);
            }

            topic.setEnable(enable);
            topic.setUpdateAt(LocalDateTime.now());

            String statusName = enable ? StatusConstant.ACTIVE : StatusConstant.DEACTIVATE;
            topic.setStatus(statusRepository.findByStatusName(statusName).orElseThrow(() -> new CustomException(Error.STATUS_NOT_FOUND)));

            ITopicService.createTopic(topic);

            responseModel.setMessage(enable ? "Topic enabled successfully" : "Topic disabled successfully");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            exceptionResponseModel.setMessage("Failed to enable/disable Topic: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @GetMapping(value = "/{topicId:.+}/listComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> listComment(@PathVariable UUID topicId) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Topic topic = ITopicService.findTopicById(topicId);
            List<Comment> commentList = new ArrayList<>();
            List<CommentResponse> commentResponseList = new ArrayList<>();

            if (topic.getComments() != null && !topic.getComments().stream().toList().isEmpty()) {
                commentList = topic.getComments().stream().sorted(Comparator.comparing(Comment::getCreateAt).reversed()).toList();
                for (Comment comment : commentList) {
                    if (comment.getCommentParent() == null) {
                        commentResponseList.add(new CommentResponse(comment, ICommentService.checkCommentParent(comment)));

                    }
                }
            }
            responseModel.setMessage("Show list Comment successful");
            responseModel.setResponseData(commentResponseList);



            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list Comment fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    private void processQuestions(CreateListQuestionByExcelFileResponse excelFileDTO, UUID topicId, User user, ResponseModel responseModel) {
        for (CreateQuestionByExcelFileResponse createQuestionDTO : excelFileDTO.getQuestions()) {
            // Tạo câu hỏi và lưu nó trước khi xử lý câu trả lời
            Question question = createQuestion(createQuestionDTO, user);
            IQuestionService.createQuestion(question); // Lưu câu hỏi trước

            // Xử lý câu trả lời
            processAnswers(createQuestionDTO.getListAnswer(), question, user);

            // Tương tự cho questionChild
            if (createQuestionDTO.getListQuestionChild() != null && !createQuestionDTO.getListQuestionChild().isEmpty()) {
                for (CreateQuestionByExcelFileResponse createQuestionChildDTO : createQuestionDTO.getListQuestionChild()) {
                    Question questionChild = createQuestion(createQuestionChildDTO, user);
                    questionChild.setQuestionGroup(question);

                    IQuestionService.createQuestion(questionChild); // Lưu câu hỏi con trước

                    processAnswers(createQuestionChildDTO.getListAnswer(), questionChild, user);
                    processContent(createQuestionChildDTO.getContentImage(), createQuestionChildDTO.getContentAudio(), questionChild, user);
                }
            }

            // Xử lý Content
            processContent(createQuestionDTO.getContentImage(), createQuestionDTO.getContentAudio(), question, user);

            // Lưu câu hỏi đã cập nhật với Content
            IQuestionService.createQuestion(question);

            // Xử lý Topic
            Topic topic = ITopicService.findTopicById(topicId);
            Part part = question.getPart();

            if (ITopicService.existPartInTopic(topic, part)) {
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());
                ITopicService.addQuestionToTopic(topic, question);
                responseModel.setMessage("Add Question to Topic successfully");
            } else {
                responseModel.setMessage("Part of Question don't have in Topic");
            }
        }
    }

    private Question createQuestion(CreateQuestionByExcelFileResponse createQuestionDTO, User user) {
        Question question = new Question();
        question.setQuestionContent(createQuestionDTO.getQuestionContent());
        question.setQuestionScore(createQuestionDTO.getQuestionScore());
        question.setUserCreate(user);
        question.setUserUpdate(user);
        question.setQuestionExplainEn(createQuestionDTO.getQuestionExplainEn());
        question.setQuestionExplainVn(createQuestionDTO.getQuestionExplainVn());

        Part part = IPartService.getPartToId(createQuestionDTO.getPartId());
        question.setPart(part);

        return question;
    }

    private void processAnswers(List<CreateListAnswerDTO> listAnswerDTO, Question question, User user) {
        if (listAnswerDTO != null && !listAnswerDTO.isEmpty()) {
            for (CreateListAnswerDTO createListAnswerDTO : listAnswerDTO) {
                Answer answer = new Answer();
                answer.setQuestion(question);
                answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                answer.setUserUpdate(user);
                answer.setUserCreate(user);

                answerRepository.save(answer);

                if (question.getAnswers() == null) {
                    question.setAnswers(new ArrayList<>());
                }
                question.getAnswers().add(answer);
            }
        }
    }

    private void processContent(String contentImage, String contentAudio, Question question, User user) {
        if (contentImage != null) {
            Content content = contentRepository.findByContentData(contentImage)
                    .orElseThrow(() -> new CustomException(Error.CONTENT_NOT_FOUND));
            content.setUserUpdate(user);

            if (question.getContentCollection() == null) {
                question.setContentCollection(new ArrayList<>());
            }
            content.setQuestion(question);
            question.getContentCollection().add(content);
            IContentService.uploadContent(content);
        }

        if (contentAudio != null) {
            Content content = contentRepository.findByContentData(contentAudio)
                    .orElseThrow(() -> new CustomException(Error.CONTENT_NOT_FOUND));
            content.setUserUpdate(user);

            if (question.getContentCollection() == null) {
                question.setContentCollection(new ArrayList<>());
            }
            content.setQuestion(question);
            question.getContentCollection().add(content);
            IContentService.uploadContent(content);
        }
    }

    @GetMapping("searchByStartTime")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?>getTopicByStartTime(
            @RequestParam @DateTimeFormat(pattern ="yyyy-MM-dd") LocalDateTime startDate ){
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            List<Topic> topics = topicServiceImpl.getTopicsByStartTime(startDate);
            if(topics.isEmpty()){
                exceptionResponseModel.setMessage("No topics found for this start date.");
                exceptionResponseModel.setStatus(HttpStatus.NOT_FOUND);
                exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseModel);
            }

            responseModel.setMessage("Topic retrieved successful");

            responseModel.setResponseData(topics);

            return ResponseEntity.ok(topics);
        }catch (Exception e) {
            exceptionResponseModel.setMessage("Failed to retrieved topics: "+e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }
}
