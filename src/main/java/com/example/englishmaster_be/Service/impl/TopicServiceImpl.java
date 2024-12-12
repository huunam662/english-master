package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Common.enums.StatusEnum;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Common.enums.RoleEnum;
import com.example.englishmaster_be.Mapper.AnswerMapper;
import com.example.englishmaster_be.Mapper.QuestionMapper;
import com.example.englishmaster_be.Model.Request.Answer.AnswerBasicRequest;
import com.example.englishmaster_be.Model.Request.Question.QuestionRequest;
import com.example.englishmaster_be.Model.Request.Topic.ListQuestionRequest;
import com.example.englishmaster_be.Model.Request.Topic.TopicRequest;
import com.example.englishmaster_be.Model.Request.Topic.TopicFilterRequest;
import com.example.englishmaster_be.Model.Request.UploadFileRequest;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.Mapper.TopicMapper;
import com.example.englishmaster_be.Model.Response.PartResponse;
import com.example.englishmaster_be.Model.Response.QuestionBasicResponse;
import com.example.englishmaster_be.Model.Response.QuestionResponse;
import com.example.englishmaster_be.Model.Response.TopicResponse;
import com.example.englishmaster_be.Model.Response.excel.ListQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.QuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.TopicByExcelFileResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.Util.LinkUtil;
import com.example.englishmaster_be.entity.*;
import com.google.cloud.storage.Blob;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicServiceImpl implements ITopicService {

    JPAQueryFactory queryFactory;

    TopicRepository topicRepository;

    PartRepository partRepository;

    QuestionRepository questionRepository;

    AnswerRepository answerRepository;

    ContentRepository contentRepository;

    IQuestionService questionService;

    IStatusService statusService;

    IUserService userService;

    IPackService packService;

    IPartService partService;

    IFileStorageService fileStorageService;

    IExcelService excelService;

    IAnswerService answerService;

    IContentService contentService;

    ITopicService topicService;


    @Transactional
    @Override
    public TopicEntity saveTopic(TopicRequest topicRequest) {

        TopicEntity topic;

        UserEntity user = userService.currentUser();

        PackEntity pack = packService.getPackById(topicRequest.getPackId());

        if(topicRequest.getTopicId() != null){

            topic = getTopicById(topicRequest.getTopicId());

            if (
                    topicRequest.getTopicImage() != null
                    && !topicRequest.getTopicImage().isEmpty()
                    && topic.getTopicImage() != null
                    && fileStorageService.load(topic.getTopicImage()) != null
            ) fileStorageService.delete(topic.getTopicImage());

        }
        else{
            topic = TopicEntity.builder()
                    .createAt(LocalDateTime.now())
                    .userCreate(user)
                    .build();
        }

        TopicMapper.INSTANCE.flowToTopicEntity(topicRequest, topic);

        topic.setStatus(statusService.getStatusByName(StatusEnum.ACTIVE));
        topic.setUserUpdate(user);
        topic.setPack(pack);

        if(topicRequest.getListPart() != null){

            List<PartEntity> partList = topicRequest.getListPart().stream()
                    .filter(Objects::nonNull)
                    .map(partService::getPartToId)
                    .toList();

            topic.setParts(partList);
        }

        if(topicRequest.getTopicImage() != null && !topicRequest.getTopicImage().isEmpty()){

            Blob blobResponse = fileStorageService.save(topicRequest.getTopicImage());

            String fileName = blobResponse.getName();

            topic.setTopicImage(fileName);
        }

        return topicRepository.save(topic);
    }

    @Transactional
    @SneakyThrows
    @Override
    public TopicEntity updateTopicByExcelFile(UUID topicId, MultipartFile file, String url) {

        // Parse dữ liệu từ file
        TopicByExcelFileResponse topicByExcelFileResponse = excelService.parseCreateTopicDTO(file);

        UserEntity user = userService.currentUser();

        // Tìm pack dựa trên ID từ Request
        PackEntity pack = packService.getPackById(topicByExcelFileResponse.getPackId());

        // Tạo đối tượng TopicEntity
        TopicEntity topic = getTopicById(topicId);
        topic.setUserUpdate(user);
        topic.setUpdateAt(LocalDateTime.now());

        TopicMapper.INSTANCE.flowToTopicEntity(topicByExcelFileResponse, topic);

        // Cập nhật các thông tin liên quan đến số lượng câu hỏi, người tạo và trạng thái
        topic.setPack(pack);
        topic.setStatus(statusService.getStatusByName(StatusEnum.ACTIVE));

        // Xử lý ảnh chủ đề
        if (topicByExcelFileResponse.getTopicImageName() == null || topicByExcelFileResponse.getTopicImageName().isEmpty())
            topic.setTopicImage(url);

        // Lưu topic vào cơ sở dữ liệu
        topic = topicRepository.save(topic);

        // Thêm các phần vào topic
        topicByExcelFileResponse.getListPart().forEach(partId -> addPartToTopic(topicId, partId));

        // Tạo response với thông tin của topic

        return topic;

    }

    @Transactional
    @SneakyThrows
    @Override
    public TopicEntity saveTopicByExcelFile(MultipartFile file, String url) {

        TopicByExcelFileResponse topicByExcelFileResponse = excelService.parseCreateTopicDTO(file);

        TopicRequest topicRequest = TopicMapper.INSTANCE.toTopicRequest(topicByExcelFileResponse);

        log.warn(topicByExcelFileResponse.getTopicImageName());

        TopicEntity topicEntity = saveTopic(topicRequest);

        if (topicByExcelFileResponse.getTopicImageName() == null || topicByExcelFileResponse.getTopicImageName().isEmpty()) {

            topicEntity.setTopicImage(url);

            topicEntity = topicRepository.save(topicEntity);
        }

        return topicEntity;
    }

    @Transactional
    @Override
    public TopicEntity uploadFileImage(UUID topicId, UploadFileRequest uploadFileRequest) {

        if(uploadFileRequest == null) throw new BadRequestException("Object required non null");

        if(uploadFileRequest.getContentData() == null || uploadFileRequest.getContentData().isEmpty())
            throw new BadRequestException("File required non empty or null content");

        UserEntity user = userService.currentUser();

        TopicEntity topic = getTopicById(topicId);

        if(topic.getTopicImage() != null && !topic.getTopicImage().isEmpty())
            fileStorageService.delete(topic.getTopicImage());

        Blob blobResponse = fileStorageService.save(uploadFileRequest.getContentData());

        String fileName = blobResponse.getName();

        topic.setTopicImage(fileName);
        topic.setUserUpdate(user);
        topic.setUpdateAt(LocalDateTime.now());

        return topicRepository.save(topic);
    }

    @Override
    public TopicEntity getTopicById(UUID topicId) {

        return topicRepository.findByTopicId(topicId)
                .orElseThrow(
                        () -> new IllegalArgumentException("TopicEntity not found with ID: " + topicId)
                );
    }

    @Override
    public List<TopicEntity> get5TopicName(String keyword) {
        return topicRepository.findTopicsByQuery(keyword, PageRequest.of(0, 5, Sort.by(Sort.Order.asc("topicName").ignoreCase())));

    }

    @Override
    public List<TopicEntity> getAllTopicToPack(PackEntity pack) {
        return topicRepository.findAllByPack(pack);
    }


    @Override
    public List<PartResponse> getPartToTopic(UUID topicId) {

        TopicEntity topic = getTopicById(topicId);

        Pageable pageable = PageRequest.of(0, 7, Sort.by(Sort.Order.asc("partName")));

        Page<PartEntity> page = partRepository.findByTopics(topic, pageable);

        return page.getContent().stream().map(
                partItem -> {

                    int totalQuestion = totalQuestion(partItem, topicId);

                    PartResponse partResponse = PartMapper.INSTANCE.toPartResponse(partItem);
                    partResponse.setTotalQuestion(totalQuestion);

                    return partResponse;
                }
        ).toList();
    }

    @Override
    public List<QuestionEntity> getQuestionOfPartToTopic(UUID topicId, UUID partId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        PartEntity part = partService.getPartToId(partId);

        List<QuestionEntity> listQuestion = questionRepository.findByTopicsAndPart(topic, part);

        Collections.shuffle(listQuestion);

        return listQuestion;
    }


    @Override
    public void addPartToTopic(UUID topicId, UUID partId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        PartEntity part = partService.getPartToId(partId);

        if (topic.getParts() == null)
            topic.setParts(new ArrayList<>());

        topic.getParts().add(part);

        topicRepository.save(topic);
    }

    @Transactional
    @Override
    public void deletePartToTopic(UUID topicId, UUID partId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        PartEntity part = partService.getPartToId(partId);

        for (PartEntity partTopic : topic.getParts()) {
            if (partTopic.equals(part)) {
                topic.getParts().remove(part);
                topicRepository.save(topic);
                return;
            }
        }

        throw new BadRequestException("Delete Part to Topic fail: Topic don't have Part");
    }

    @Override
    public boolean existQuestionInTopic(TopicEntity topic, QuestionEntity question) {
        for (QuestionEntity questionItem : topic.getQuestions()) {
            if (questionItem.equals(question)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existPartInTopic(TopicEntity topic, PartEntity part) {
        for (PartEntity partItem : topic.getParts()) {
            if (partItem.equals(part)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    @Override
    public void deleteTopic(UUID topicId) {

        TopicEntity topic = getTopicById(topicId);

        if(topic.getTopicImage() != null && !topic.getTopicImage().isEmpty())
            fileStorageService.delete(topic.getTopicImage());

        topicRepository.delete(topic);
    }

    @Override
    public int totalQuestion(PartEntity part, UUID topicId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        int total = 0;

        for (QuestionEntity question : topic.getQuestions()) {
            if (question.getPart().getPartId() == part.getPartId()) {

                boolean check = questionService.checkQuestionGroup(question.getQuestionId());

                if (check) total = total + questionService.countQuestionToQuestionGroup(question);

                else total++;
            }
        }
        return total;
    }

    @Override
    public List<String> get5SuggestTopic(String query) {

        List<TopicEntity> topics = get5TopicName(query);

        return topics.stream()
                .filter(
                        topic -> topic != null && !topic.getTopicName().isEmpty()
                )
                .map(TopicEntity::getTopicName)
                .toList();
    }

    @Override
    public List<TopicEntity> getTopicsByStartTime(LocalDateTime startTime) {

        return topicRepository.findByStartTime(startTime);
    }

    @Override
    public List<String> getImageCdnLinkTopic() {

        List<String> listLinkCdn = topicRepository.findAllTopicImages();

        MessageResponseHolder.setMessage("Found " + listLinkCdn.size() + " links");

        return listLinkCdn.stream()
                .filter(linkCdn -> linkCdn != null && !linkCdn.isEmpty())
                .map(linkCdn -> LinkUtil.linkFileShowImageBE + linkCdn)
                .toList();
    }

    @Override
    public List<CommentEntity> listComment(UUID topicId) {

        TopicEntity topic = getTopicById(topicId);

        if(topic.getComments() == null) return new ArrayList<>();

        return topic.getComments().stream()
                .sorted(
                        Comparator.comparing(CommentEntity::getCreateAt).reversed()
                )
                .toList();
    }

    @Transactional
    @Override
    public void enableTopic(UUID topicId, boolean enable) {

        TopicEntity topic = getTopicById(topicId);

        topic.setEnable(enable);

        topic.setUpdateAt(LocalDateTime.now());

        StatusEnum statusName = enable ? StatusEnum.ACTIVE : StatusEnum.DEACTIVATE;

        StatusEntity statusUpdate = statusService.getStatusByName(statusName);

        topic.setStatus(statusUpdate);

        topicRepository.save(topic);

        MessageResponseHolder.setMessage(enable ? "TopicEntity enabled successfully" : "TopicEntity disabled successfully");

    }


    @Override
    public List<QuestionResponse> getQuestionOfToTopic(UUID topicId, UUID partId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userRole = authentication.getAuthorities().iterator().next().getAuthority().split("ROLE_")[1];

        List<QuestionEntity> questionList = getQuestionOfPartToTopic(topicId, partId);

        List<QuestionResponse> questionResponseList = new ArrayList<>();

        for (QuestionEntity question : questionList) {

            QuestionResponse questionResponse = QuestionMapper.INSTANCE.toQuestionResponse(question);

            if (questionService.checkQuestionGroup(question.getQuestionId())) {

                List<QuestionEntity> questionGroupList = questionService.listQuestionGroup(question);

                questionResponse.setQuestionGroupChildren(new ArrayList<>());

                for (QuestionEntity questionGroup : questionGroupList) {

                    QuestionResponse questionGroupResponse = QuestionMapper.INSTANCE.toQuestionResponse(questionGroup);

                    if (userRole.equals(RoleEnum.ADMIN.name())) {
                        AnswerEntity answerCorrect = answerService.correctAnswer(questionGroup);
                        questionGroupResponse.setAnswerCorrect(answerCorrect.getAnswerId());
                    }

                    questionResponse.getQuestionGroupChildren().add(questionGroupResponse);
                }
            } else {
                if (userRole.equals(RoleEnum.ADMIN.name())) {

                    AnswerEntity answerCorrect = answerService.correctAnswer(question);
                    questionResponse.setAnswerCorrect(answerCorrect.getAnswerId());
                }
            }
            if (question.getContentCollection().size() > 1) {
                questionResponseList.add(0, questionResponse);
            } else {
                questionResponseList.add(questionResponse);
            }
        }

        return questionResponseList;
    }

    @Transactional
    @Override
    public void deleteQuestionToTopic(UUID topicId, UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        TopicEntity topic = getTopicById(topicId);

        UserEntity user = userService.currentUser();

        if(!existQuestionInTopic(topic, question))
            throw new BadRequestException("Question don't have in Topic");

        topic.setUserUpdate(user);

        topic.setUpdateAt(LocalDateTime.now());

        topic.getQuestions().remove(question);

        topicRepository.save(topic);

    }

    @Transactional
    @Override
    @SneakyThrows
    public void addAllPartsToTopicByExcelFile(UUID topicId, MultipartFile file) {

        UserEntity user = userService.currentUser();

        ListQuestionByExcelFileResponse excelFileDTO = excelService.parseAllPartsDTO(topicId, file);

        processQuestions(excelFileDTO, topicId, user);
    }

    @Transactional
    @Override
    @SneakyThrows
    public void addListQuestionPart123467ToTopicByExcelFile(UUID topicId, MultipartFile file, int partName) {

        List<Integer> partNameTemplate = List.of(1, 2, 3, 4, 6, 7);

        if(!partNameTemplate.contains(partName))
            throw new BadRequestException(
                    "part name must be existed in ["
                            + partNameTemplate.stream().map(String::valueOf).collect(Collectors.joining(", "))
                            + "]"
            );

        UserEntity user = userService.currentUser();

        ListQuestionByExcelFileResponse excelFileDTO;

        if(partName == 1 || partName == 2)
            excelFileDTO = excelService.parseListeningPart12DTO(topicId, file, partName);
        else if(partName == 3 || partName == 4)
            excelFileDTO = excelService.parseListeningPart34DTO(topicId, file, partName);
        else
            excelFileDTO = excelService.parseReadingPart67DTO(topicId, file, partName);

        processQuestions(excelFileDTO, topicId, user);

    }

    @Transactional
    @Override
    @SneakyThrows
    public void addListQuestionPart5ToTopicByExcelFile(UUID topicId, MultipartFile file) {

        UserEntity user = userService.currentUser();

        ListQuestionByExcelFileResponse excelFileDTO = excelService.parseReadingPart5DTO(topicId, file);

        processQuestions(excelFileDTO, topicId, user);

    }

    @Transactional
    protected QuestionEntity saveQuestionFromExcelTemplate(QuestionByExcelFileResponse questionByExcelFileResponse, UserEntity user) {

        PartEntity part = partService.getPartToId(questionByExcelFileResponse.getPartId());

        QuestionEntity question = QuestionEntity.builder()
                .userCreate(user)
                .userUpdate(user)
                .part(part)
                .build();

        QuestionMapper.INSTANCE.flowToQuestionEntity(questionByExcelFileResponse, question);

        return questionRepository.save(question);
    }

    protected void processAnswers(List<AnswerBasicRequest> answerBasicRequestList, QuestionEntity question, UserEntity user) {

        if(answerBasicRequestList == null || answerBasicRequestList.isEmpty()) return;

        if (question.getAnswers() == null)
            question.setAnswers(new ArrayList<>());

        answerBasicRequestList.forEach(answerBasicRequest -> {

            AnswerEntity answer = AnswerEntity.builder()
                    .question(question)
                    .userCreate(user)
                    .userUpdate(user)
                    .build();

            AnswerMapper.INSTANCE.flowToAnswerEntity(answerBasicRequest, answer);

            answer = answerRepository.save(answer);

            question.getAnswers().add(answer);
        });

    }

    @Transactional
    protected void processContent(String contentImage, String contentAudio, QuestionEntity question, UserEntity user) {
        if (contentImage != null) {

            ContentEntity content = contentService.getContentByContentData(contentImage);

            content.setUserUpdate(user);

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            content.setQuestion(question);
            question.getContentCollection().add(content);

            contentRepository.save(content);
        }

        if (contentAudio != null) {

            ContentEntity content = contentService.getContentByContentData(contentAudio);

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            content.setQuestion(question);
            question.getContentCollection().add(content);

            contentRepository.save(content);
        }
    }


    @Transactional
    protected void processQuestions(ListQuestionByExcelFileResponse listQuestionByExcelFileResponse, UUID topicId, UserEntity user) {

        for (QuestionByExcelFileResponse questionByExcelFileResponse : listQuestionByExcelFileResponse.getQuestions()) {

            // Tạo câu hỏi và lưu nó trước khi xử lý câu trả lời

            QuestionEntity question = saveQuestionFromExcelTemplate(questionByExcelFileResponse, user);

            // Xử lý câu trả lời
            processAnswers(questionByExcelFileResponse.getListAnswer(), question, user);

            // Tương tự cho questionChild
            if (questionByExcelFileResponse.getListQuestionChild() != null && !questionByExcelFileResponse.getListQuestionChild().isEmpty()) {
                for (QuestionByExcelFileResponse createQuestionChildDTO : questionByExcelFileResponse.getListQuestionChild()) {

                    QuestionEntity questionChild = saveQuestionFromExcelTemplate(createQuestionChildDTO, user);

                    questionChild.setQuestionGroupParent(question);

                    questionChild = questionRepository.save(questionChild); // Lưu câu hỏi con trước

                    processAnswers(createQuestionChildDTO.getListAnswer(), questionChild, user);
                    processContent(createQuestionChildDTO.getContentImage(), createQuestionChildDTO.getContentAudio(), questionChild, user);
                }
            }

            // Xử lý ContentEntity
            processContent(questionByExcelFileResponse.getContentImage(), questionByExcelFileResponse.getContentAudio(), question, user);

            // Lưu câu hỏi đã cập nhật với ContentEntity
            questionRepository.save(question);

            // Xử lý TopicEntity
            TopicEntity topic = getTopicById(topicId);

            PartEntity part = question.getPart();

            if (existPartInTopic(topic, part))
                MessageResponseHolder.setMessage("PartEntity of QuestionEntity don't have in TopicEntity");
            else {
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());
                topic.getQuestions().add(question);
                topicRepository.save(topic);
                MessageResponseHolder.setMessage("Add QuestionEntity to TopicEntity successfully");
            }
        }
    }

    @Transactional
    @Override
    public void addListQuestionToTopic(UUID topicId, ListQuestionRequest listQuestionRequest) {

        UserEntity user = userService.currentUser();

        for (QuestionRequest questionRequest : listQuestionRequest.getListQuestion()) {

            PartEntity part = partService.getPartToId(questionRequest.getPartId());

            QuestionEntity question = QuestionEntity.builder()
                    .userCreate(user)
                    .userUpdate(user)
                    .part(part)
                    .build();

            QuestionMapper.INSTANCE.flowToQuestionEntity(questionRequest, question);

            question = questionRepository.save(question);

            if (questionRequest.getListAnswer() != null && !questionRequest.getListAnswer().isEmpty()) {
                for (AnswerBasicRequest answerBasicRequest : questionRequest.getListAnswer()) {

                    AnswerEntity answer = AnswerEntity.builder()
                            .question(question)
                            .userCreate(user)
                            .userUpdate(user)
                            .build();

                    AnswerMapper.INSTANCE.flowToAnswerEntity(answerBasicRequest, answer);

                    answerRepository.save(answer);
                }
            }

            if (questionRequest.getListQuestionChild() != null && !questionRequest.getListQuestionChild().isEmpty()) {
                for (QuestionRequest questionRequestItem : questionRequest.getListQuestionChild()) {

                    QuestionEntity questionChild = QuestionEntity.builder()
                            .questionGroupParent(question)
                            .part(part)
                            .userCreate(user)
                            .userUpdate(user)
                            .build();

                    QuestionMapper.INSTANCE.flowToQuestionEntity(questionRequestItem, questionChild);

                    questionChild = questionRepository.save(questionChild);


                    if (questionChild.getAnswers() == null)
                        questionChild.setAnswers(new ArrayList<>());

                    for (AnswerBasicRequest answerBasicRequest : questionRequestItem.getListAnswer()) {

                        AnswerEntity answer = AnswerEntity.builder()
                                .question(questionChild)
                                .userCreate(user)
                                .userUpdate(user)
                                .build();

                        answer.setQuestion(questionChild);
                        answer.setUserUpdate(user);
                        answer.setUserCreate(user);

                        AnswerMapper.INSTANCE.flowToAnswerEntity(answerBasicRequest, answer);

                        answer = answerRepository.save(answer);

                        questionChild.getAnswers().add(answer);
                    }

                    questionRepository.save(questionChild);
                }

            }

            if (questionRequest.getContentImage() != null && !questionRequest.getContentImage().isEmpty()) {

                Blob blobResponse = fileStorageService.save(questionRequest.getContentImage());

                String fileName = blobResponse.getName();

                ContentEntity content = ContentEntity.builder()
                        .contentData(fileName)
                        .contentType(GetExtension.typeFile(fileName))
                        .question(question)
                        .userCreate(user)
                        .userUpdate(user)
                        .build();

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);

                contentRepository.save(content);
            }
            if (questionRequest.getContentAudio() != null && !questionRequest.getContentAudio().isEmpty()) {

                Blob blobResponse = fileStorageService.save(questionRequest.getContentAudio());

                String fileName = blobResponse.getName();

                ContentEntity content = ContentEntity.builder()
                        .contentData(fileName)
                        .contentType(GetExtension.typeFile(fileName))
                        .question(question)
                        .userCreate(user)
                        .userUpdate(user)
                        .build();

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);

                contentRepository.save(content);
            }

            questionRepository.save(question);

            TopicEntity topic = getTopicById(topicId);

            if (existPartInTopic(topic, part))
                MessageResponseHolder.setMessage("Part of Question haven't in Topic");
            else {
                topic.setUserUpdate(user);
                topic.setUpdateAt(LocalDateTime.now());
                topic.getQuestions().add(question);
                topicRepository.save(topic);
                MessageResponseHolder.setMessage("Add Question to Topic successfully");
            }
        }
    }


    @Transactional
    @Override
    public QuestionResponse addQuestionToTopic(UUID topicId, QuestionRequest questionRequest) {

        UserEntity user = userService.currentUser();

        PartEntity part = partService.getPartToId(questionRequest.getPartId());

        QuestionEntity question = QuestionEntity.builder()
                .questionContent(questionRequest.getQuestionContent())
                .questionScore(questionRequest.getQuestionScore())
                .userCreate(user)
                .userUpdate(user)
                .part(part)
                .build();

        question = questionRepository.saveAndFlush(question);

        if (question.getAnswers() == null)
            question.setAnswers(new ArrayList<>());

        if (questionRequest.getListAnswer() != null && !questionRequest.getListAnswer().isEmpty()) {
            for (AnswerBasicRequest answerBasicRequest : questionRequest.getListAnswer()) {

                AnswerEntity answer = AnswerEntity.builder()
                        .question(question)
                        .userCreate(user)
                        .userUpdate(user)
                        .build();

                AnswerMapper.INSTANCE.flowToAnswerEntity(answerBasicRequest, answer);

                answerRepository.save(answer);

                question.getAnswers().add(answer);
            }
        }

        if (questionRequest.getListQuestionChild() != null && !questionRequest.getListQuestionChild().isEmpty()) {
            for (QuestionRequest questionRequestItem : questionRequest.getListQuestionChild()) {

                QuestionEntity questionChild = QuestionEntity.builder()
                        .questionGroupParent(question)
                        .part(question.getPart())
                        .userCreate(user)
                        .userUpdate(user)
                        .build();

                QuestionMapper.INSTANCE.flowToQuestionEntity(questionRequestItem, questionChild);

                questionChild = questionRepository.save(questionChild);

                if (questionChild.getAnswers() == null)
                    questionChild.setAnswers(new ArrayList<>());

                for (AnswerBasicRequest answerBasicRequest : questionRequestItem.getListAnswer()) {

                    AnswerEntity answer = AnswerEntity.builder()
                            .question(questionChild)
                            .userCreate(user)
                            .userUpdate(user)
                            .build();

                    AnswerMapper.INSTANCE.flowToAnswerEntity(answerBasicRequest, answer);

                    answer = answerRepository.save(answer);

                    questionChild.getAnswers().add(answer);
                }

                questionRepository.save(questionChild);
            }

        }

        if (question.getContentCollection() == null)
            question.setContentCollection(new ArrayList<>());

        if (questionRequest.getContentImage() != null && !questionRequest.getContentImage().isEmpty()) {

            Blob blobResponse = fileStorageService.save(questionRequest.getContentImage());

            String fileName = blobResponse.getName();

            ContentEntity content = ContentEntity.builder()
                    .contentData(fileName)
                    .contentType(GetExtension.typeFile(fileName))
                    .question(question)
                    .userCreate(user)
                    .userUpdate(user)
                    .build();

            question.getContentCollection().add(content);

            contentRepository.save(content);

        }

        if (questionRequest.getContentAudio() != null && !questionRequest.getContentAudio().isEmpty()) {

            Blob blobResponse = fileStorageService.save(questionRequest.getContentAudio());

            String fileName = blobResponse.getName();

            ContentEntity content = ContentEntity.builder()
                    .contentData(fileName)
                    .contentType(GetExtension.typeFile(fileName))
                    .question(question)
                    .userCreate(user)
                    .userUpdate(user)
                    .build();


            question.getContentCollection().add(content);

            contentRepository.save(content);
        }

        question = questionRepository.saveAndFlush(question);

        TopicEntity topic = getTopicById(topicId);

        if (existPartInTopic(topic, part)){

            MessageResponseHolder.setMessage("Part of Question don't have in Topic");
            return QuestionMapper.INSTANCE.toQuestionResponse(question);
        }
        else {
            topic.setUserUpdate(user);
            topic.setUpdateAt(LocalDateTime.now());
            topic.getQuestions().add(question);
            topicRepository.save(topic);

            QuestionEntity question1 = questionService.getQuestionById(question.getQuestionId());
            QuestionResponse questionResponse = QuestionMapper.INSTANCE.toQuestionResponse(question1);

            if (questionService.checkQuestionGroup(question1.getQuestionId())) {
                List<QuestionEntity> questionGroupList = questionService.listQuestionGroup(question1);
                List<QuestionBasicResponse> questionGroupResponseList = new ArrayList<>();

                for (QuestionEntity questionGroup : questionGroupList) {

                    AnswerEntity answerCorrect = answerService.correctAnswer(questionGroup);
                    QuestionBasicResponse questionGroupResponse = QuestionMapper.INSTANCE.toQuestionBasicResponse(questionGroup);
                    questionGroupResponse.setAnswerCorrect(answerCorrect.getAnswerId());
                    questionGroupResponseList.add(questionGroupResponse);
                }

                questionResponse.setQuestionGroupChildren(questionGroupResponseList);
            } else {
                AnswerEntity answerCorrect = answerService.correctAnswer(question1);
                questionResponse.setAnswerCorrect(answerCorrect.getAnswerId());
            }

            MessageResponseHolder.setMessage("Add QuestionEntity to TopicEntity successfully");

            return questionResponse;
        }
    }

    @Override
    public FilterResponse<?> getAllTopic(TopicFilterRequest filterRequest) {

        FilterResponse<TopicResponse> filterResponse = FilterResponse.<TopicResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        BooleanExpression wherePattern = QTopicEntity.topicEntity.isNotNull();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {

            boolean isUser = authentication.getAuthorities().stream().anyMatch(
                    auth -> auth.getAuthority().equals("ROLE_" + RoleEnum.USER.name())
                                );

            if(isUser) wherePattern = wherePattern.and(QTopicEntity.topicEntity.enable.eq(Boolean.TRUE));

        }

        if (filterRequest.getPackId() != null)
            wherePattern = wherePattern.and(QTopicEntity.topicEntity.pack.packId.eq(filterRequest.getPackId()));

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty())
            wherePattern = wherePattern.and(QTopicEntity.topicEntity.topicName.lower().like("%" + filterRequest.getSearch().toLowerCase() + "%"));

        if (filterRequest.getType() != null && !filterRequest.getType().isEmpty())
            wherePattern = wherePattern.and(QTopicEntity.topicEntity.topicType.lower().like(filterRequest.getType().toLowerCase()));

        long totalElements = Optional.ofNullable(
                            queryFactory
                                    .select(QTopicEntity.topicEntity.count())
                                    .from(QTopicEntity.topicEntity)
                                    .where(wherePattern)
                                    .fetchOne()
                ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterRequest.getSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if ("name".equalsIgnoreCase(filterRequest.getSortBy())) {
            if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
                orderSpecifier = QTopicEntity.topicEntity.topicName.lower().desc();
            else
                orderSpecifier = QTopicEntity.topicEntity.topicName.lower().asc();

        } else if ("updateAt".equalsIgnoreCase(filterRequest.getSortBy())) {
            if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
                orderSpecifier = QTopicEntity.topicEntity.updateAt.desc();
            else
                orderSpecifier = QTopicEntity.topicEntity.updateAt.asc();

        } else {
            if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
                orderSpecifier = QTopicEntity.topicEntity.updateAt.desc();
            else
                orderSpecifier = QTopicEntity.topicEntity.updateAt.asc();
        }

        JPAQuery<TopicEntity> query = queryFactory.selectFrom(QTopicEntity.topicEntity)
                                            .where(wherePattern)
                                            .orderBy(orderSpecifier)
                                            .offset(filterResponse.getOffset())
                                            .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                TopicMapper.INSTANCE.toTopicResponseList(query.fetch())
        );

        return filterResponse;
    }
}
