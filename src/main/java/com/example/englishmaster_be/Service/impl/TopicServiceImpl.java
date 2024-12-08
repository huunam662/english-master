package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Common.enums.RoleEnum;
import com.example.englishmaster_be.Constant.StatusConstant;
import com.example.englishmaster_be.DTO.Answer.SaveListAnswerDTO;
import com.example.englishmaster_be.DTO.Question.SaveQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.SaveListQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.SaveTopicDTO;
import com.example.englishmaster_be.DTO.Topic.TopicFilterRequest;
import com.example.englishmaster_be.DTO.Topic.UpdateTopicDTO;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.Mapper.TopicMapper;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.Response.excel.CreateListQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateTopicByExcelFileResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.Util.LinkUtil;
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
import org.springframework.security.core.GrantedAuthority;
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

    ICommentService commentService;

    IStatusService statusService;

    IUserService userService;

    IPackService packService;

    IPartService partService;

    IFileStorageService fileStorageService;

    IExcelService excelService;

    IAnswerService answerService;

    IContentService contentService;


    @Transactional
    @Override
    public TopicResponse saveTopic(SaveTopicDTO saveTopicDTO) {

        Topic topic;

        User user = userService.currentUser();

        Pack pack = packService.findPackById(saveTopicDTO.getTopicPackId());

        if(saveTopicDTO instanceof UpdateTopicDTO updateTopicDTO){

            topic = findTopicById(updateTopicDTO.getUpdateTopicId());

            if (
                    updateTopicDTO.getTopicImage() != null
                    && !updateTopicDTO.getTopicImage().isEmpty()
                    && topic.getTopicImage() != null
                    && fileStorageService.load(topic.getTopicImage()) != null
            ) {
                fileStorageService.delete(topic.getTopicImage());
            }
        }
        else{
            topic = Topic.builder()
                    .createAt(LocalDateTime.now())
                    .userCreate(user)
                    .build();
        }

        TopicMapper.INSTANCE.updateTopicEntity(saveTopicDTO, topic);

        topic.setStatus(statusService.getStatusByName(StatusConstant.ACTIVE));
        topic.setUserUpdate(user);
        topic.setPack(pack);

        if(saveTopicDTO.getListPart() != null){

            List<Part> partList = saveTopicDTO.getListPart().stream()
                    .filter(Objects::nonNull)
                    .map(partService::getPartToId)
                    .toList();

            topic.setParts(partList);
        }

        if(saveTopicDTO.getTopicImage() != null && !saveTopicDTO.getTopicImage().isEmpty()){

            Blob blobResponse = fileStorageService.save(saveTopicDTO.getTopicImage());

            String fileName = blobResponse.getName();

            topic.setTopicImage(fileName);
        }

        topic = topicRepository.save(topic);

        return new TopicResponse(topic);
    }

    @Transactional
    @SneakyThrows
    @Override
    public TopicResponse saveTopicByExcelFile(MultipartFile file, String url) {

        CreateTopicByExcelFileResponse createTopicByExcelFileDTO = excelService.parseCreateTopicDTO(file);

        SaveTopicDTO saveTopicDTO = TopicMapper.INSTANCE.toSaveTopicDTO(createTopicByExcelFileDTO);

        log.warn(createTopicByExcelFileDTO.getTopicImageName());

        TopicResponse topicResponse = saveTopic(saveTopicDTO);

        if (createTopicByExcelFileDTO.getTopicImageName() == null || createTopicByExcelFileDTO.getTopicImageName().isEmpty()) {

            Topic topic = findTopicById(topicResponse.getTopicId());

            topic.setTopicImage(url);

            topic = topicRepository.save(topic);

            topicResponse = new TopicResponse(topic);
        }

        return topicResponse;
    }

    @Transactional
    @Override
    public TopicResponse uploadFileImage(UUID topicId, UploadFileDTO uploadFileDTO) {

        if(uploadFileDTO == null) throw new BadRequestException("Object required non null");

        if(uploadFileDTO.getContentData() == null || uploadFileDTO.getContentData().isEmpty())
            throw new BadRequestException("File required non empty or null content");

        User user = userService.currentUser();

        Topic topic = findTopicById(topicId);

        if(topic.getTopicImage() != null && !topic.getTopicImage().isEmpty())
            fileStorageService.delete(topic.getTopicImage());

        Blob blobResponse = fileStorageService.save(uploadFileDTO.getContentData());

        String fileName = blobResponse.getName();

        topic.setTopicImage(fileName);
        topic.setUserUpdate(user);
        topic.setUpdateAt(LocalDateTime.now());

        topic = topicRepository.save(topic);

        return new TopicResponse(topic);
    }

    @Override
    public Topic findTopicById(UUID topicId) {
        return topicRepository.findByTopicId(topicId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Topic not found with ID: " + topicId)
                );
    }

    @Override
    public List<Topic> get5TopicName(String keyword) {
        return topicRepository.findTopicsByQuery(keyword, PageRequest.of(0, 5, Sort.by(Sort.Order.asc("topicName").ignoreCase())));

    }

    @Override
    public List<Topic> getAllTopicToPack(Pack pack) {
        return topicRepository.findAllByPack(pack);
    }


    @Override
    public List<PartResponse> getPartToTopic(UUID topicId) {

        Topic topic = findTopicById(topicId);

        Pageable pageable = PageRequest.of(0, 7, Sort.by(Sort.Order.asc("partName")));

        Page<Part> page = partRepository.findByTopics(topic, pageable);

        return page.getContent().stream().map(
                partItem -> {

                    int totalQuestion = totalQuestion(partItem, topicId);

                    PartResponse partResponse = PartMapper.INSTANCE.partEntityToPartResponse(partItem);
                    partResponse.setTotalQuestion(totalQuestion);

                    return partResponse;
                }
        ).toList();
    }

    @Override
    public List<Question> getQuestionOfPartToTopic(UUID topicId, UUID partId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));
        List<Question> listQuestion = questionRepository.findByTopicsAndPart(topic, part);
        Collections.shuffle(listQuestion);
        return listQuestion;
    }


    @Override
    public void addPartToTopic(UUID topicId, UUID partId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));

        if (topic.getParts() == null) {
            topic.setParts(new ArrayList<>());
        }
        topic.getParts().add(part);
        topicRepository.save(topic);
    }

    @Transactional
    @Override
    public void deletePartToTopic(UUID topicId, UUID partId) {

        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));

        for (Part partTopic : topic.getParts()) {
            if (partTopic.equals(part)) {
                topic.getParts().remove(part);
                topicRepository.save(topic);
                return;
            }
        }

        throw new BadRequestException("Delete Part to Topic fail: Topic don't have Part");
    }

    @Override
    public boolean existQuestionInTopic(Topic topic, Question question) {
        for (Question questionItem : topic.getQuestions()) {
            if (questionItem.equals(question)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existPartInTopic(Topic topic, Part part) {
        for (Part partItem : topic.getParts()) {
            if (partItem.equals(part)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    @Override
    public void deleteTopic(UUID topicId) {

        Topic topic = findTopicById(topicId);

        if(topic.getTopicImage() != null && !topic.getTopicImage().isEmpty())
            fileStorageService.delete(topic.getTopicImage());

        topicRepository.delete(topic);
    }

    @Override
    public int totalQuestion(Part part, UUID topicId) {
        int total = 0;
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));

        for (Question question : topic.getQuestions()) {
            if (question.getPart().getPartId() == part.getPartId()) {
                boolean check = questionService.checkQuestionGroup(question.getQuestionId());
                if (check) {
                    total = total + questionService.countQuestionToQuestionGroup(question);
                } else {
                    total++;
                }
            }
        }
        return total;
    }

    @Override
    public List<String> get5SuggestTopic(String query) {

        List<Topic> topics = get5TopicName(query);

        return topics.stream()
                .filter(
                        topic -> topic != null && !topic.getTopicName().isEmpty()
                ).map(Topic::getTopicName)
                .toList();
    }

    @Override
    public List<TopicResponse> getTopicsByStartTime(LocalDateTime startTime) {

        List<Topic> topics = topicRepository.findByStartTime(startTime);

        return TopicMapper.INSTANCE.toListTopicResponses(topics);
    }

    @Override
    public TopicResponse getTopic(UUID id) {

        if (id == null)
            throw new BadRequestException("Topic ID is required");

        JPAQuery<Topic> query = queryFactory.selectFrom(QTopic.topic)
                .where(QTopic.topic.topicId.eq(id));
        Topic topic = query.fetchOne();

        if (topic == null) throw new BadRequestException("Topic not found");

        return new TopicResponse(topic);
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
    public List<CommentResponse> listComment(UUID topicId) {

        Topic topic = findTopicById(topicId);

        if(topic.getComments() == null) return new ArrayList<>();

        return topic.getComments().stream()
                .sorted(
                        Comparator.comparing(Comment::getCreateAt).reversed()
                ).map(comment ->
                        new CommentResponse(comment, commentService.checkCommentParent(comment))
                ).toList();
    }

    @Transactional
    @Override
    public void enableTopic(UUID topicId, boolean enable) {

        Topic topic = findTopicById(topicId);

        topic.setEnable(enable);
        topic.setUpdateAt(LocalDateTime.now());

        String statusName = enable ? StatusConstant.ACTIVE : StatusConstant.DEACTIVATE;

        Status statusUpdate = statusService.getStatusByName(statusName);

        topic.setStatus(statusUpdate);

        topicRepository.save(topic);

        MessageResponseHolder.setMessage(enable ? "Topic enabled successfully" : "Topic disabled successfully");

    }


    @Override
    public List<QuestionResponse> getQuestionOfToTopic(UUID topicId, UUID partId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userRole = authentication.getAuthorities().iterator().next().getAuthority();

        List<Question> questionList = getQuestionOfPartToTopic(topicId, partId);

        List<QuestionResponse> questionResponseList = new ArrayList<>();

        for (Question question : questionList) {

            QuestionResponse questionResponse = new QuestionResponse(question);

            if (questionService.checkQuestionGroup(question.getQuestionId())) {

                List<Question> questionGroupList = questionService.listQuestionGroup(question);

                List<QuestionResponse> questionGroupResponseList = new ArrayList<>();

                for (Question questionGroup : questionGroupList) {
                    QuestionResponse questionGroupResponse;
                    if (userRole.equals(RoleEnum.ADMIN)) {
                        Answer answerCorrect = answerService.correctAnswer(questionGroup);
                        questionGroupResponse = new QuestionResponse(questionGroup, answerCorrect);
                    } else {
                        questionGroupResponse = new QuestionResponse(questionGroup);
                    }
                    questionGroupResponseList.add(questionGroupResponse);
                    questionResponse.setQuestionGroup(questionGroupResponseList);
                }
            } else {
                if (userRole.equals(RoleEnum.ADMIN)) {
                    Answer answerCorrect = answerService.correctAnswer(question);
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

        Question question = questionService.getQuestionById(questionId);

        Topic topic = findTopicById(topicId);

        User user = userService.currentUser();

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

        User user = userService.currentUser();

        CreateListQuestionByExcelFileResponse excelFileDTO = excelService.parseAllPartsDTO(topicId, file);

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

        User user = userService.currentUser();

        CreateListQuestionByExcelFileResponse excelFileDTO;

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

        User user = userService.currentUser();

        CreateListQuestionByExcelFileResponse excelFileDTO = excelService.parseReadingPart5DTO(topicId, file);

        processQuestions(excelFileDTO, topicId, user);

    }

    @Transactional
    protected Question saveQuestionFromExcelTemplate(CreateQuestionByExcelFileResponse createQuestionDTO, User user) {

        Part part = partService.getPartToId(createQuestionDTO.getPartId());

        Question question = Question.builder()
                .questionContent(createQuestionDTO.getQuestionContent())
                .questionScore(createQuestionDTO.getQuestionScore())
                .questionExplainEn(createQuestionDTO.getQuestionExplainEn())
                .questionExplainVn(createQuestionDTO.getQuestionExplainVn())
                .userCreate(user)
                .userUpdate(user)
                .part(part)
                .build();

        return questionRepository.save(question);
    }

    protected void processAnswers(List<SaveListAnswerDTO> listAnswerDTO, Question question, User user) {

        if(listAnswerDTO == null || listAnswerDTO.isEmpty()) return;

        if (question.getAnswers() == null)
            question.setAnswers(new ArrayList<>());

        listAnswerDTO.forEach(answerDTO -> {

            Answer answer = Answer.builder()
                    .question(question)
                    .answerContent(answerDTO.getContentAnswer())
                    .correctAnswer(answerDTO.isCorrectAnswer())
                    .userCreate(user)
                    .userUpdate(user)
                    .build();

            answerRepository.save(answer);

            question.getAnswers().add(answer);
        });

    }

    @Transactional
    protected void processContent(String contentImage, String contentAudio, Question question, User user) {
        if (contentImage != null) {

            Content content = contentService.getContentByContentData(contentImage);

            content.setUserUpdate(user);

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            content.setQuestion(question);
            question.getContentCollection().add(content);

            contentRepository.save(content);
        }

        if (contentAudio != null) {

            Content content = contentService.getContentByContentData(contentAudio);

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            content.setQuestion(question);
            question.getContentCollection().add(content);

            contentRepository.save(content);
        }
    }


    @Transactional
    protected void processQuestions(CreateListQuestionByExcelFileResponse excelFileDTO, UUID topicId, User user) {

        for (CreateQuestionByExcelFileResponse createQuestionDTO : excelFileDTO.getQuestions()) {

            // Tạo câu hỏi và lưu nó trước khi xử lý câu trả lời

            Question question = saveQuestionFromExcelTemplate(createQuestionDTO, user);

            // Xử lý câu trả lời
            processAnswers(createQuestionDTO.getListAnswer(), question, user);

            // Tương tự cho questionChild
            if (createQuestionDTO.getListQuestionChild() != null && !createQuestionDTO.getListQuestionChild().isEmpty()) {
                for (CreateQuestionByExcelFileResponse createQuestionChildDTO : createQuestionDTO.getListQuestionChild()) {

                    Question questionChild = saveQuestionFromExcelTemplate(createQuestionChildDTO, user);

                    questionChild.setQuestionGroup(question);

                    questionChild = questionRepository.save(questionChild); // Lưu câu hỏi con trước

                    processAnswers(createQuestionChildDTO.getListAnswer(), questionChild, user);
                    processContent(createQuestionChildDTO.getContentImage(), createQuestionChildDTO.getContentAudio(), questionChild, user);
                }
            }

            // Xử lý Content
            processContent(createQuestionDTO.getContentImage(), createQuestionDTO.getContentAudio(), question, user);

            // Lưu câu hỏi đã cập nhật với Content
            questionRepository.save(question);

            // Xử lý Topic
            Topic topic = findTopicById(topicId);

            Part part = question.getPart();

            if (existPartInTopic(topic, part))
                MessageResponseHolder.setMessage("Part of Question don't have in Topic");
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
    public void addListQuestionToTopic(UUID topicId, SaveListQuestionDTO createQuestionDTOList) {

        User user = userService.currentUser();

        for (SaveQuestionDTO createQuestionDTO : createQuestionDTOList.getListQuestion()) {
            Question question = new Question();
            question.setQuestionContent(createQuestionDTO.getQuestionContent());
            question.setQuestionScore(createQuestionDTO.getQuestionScore());
            question.setUserCreate(user);
            question.setUserUpdate(user);

            Part part = partService.getPartToId(createQuestionDTO.getPartId());
            question.setPart(part);
            questionRepository.save(question);


            if (createQuestionDTO.getListAnswer() != null && !createQuestionDTO.getListAnswer().isEmpty()) {
                for (SaveListAnswerDTO createListAnswerDTO : createQuestionDTO.getListAnswer()) {
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
                for (SaveQuestionDTO createQuestionChildDTO : createQuestionDTO.getListQuestionChild()) {
                    Question questionChild = new Question();
                    questionChild.setQuestionGroup(question);
                    questionChild.setQuestionContent(createQuestionChildDTO.getQuestionContent());
                    questionChild.setQuestionScore(createQuestionChildDTO.getQuestionScore());
                    questionChild.setPart(question.getPart());
                    questionChild.setUserCreate(user);
                    questionChild.setUserUpdate(user);

                    questionRepository.save(questionChild);

                    for (SaveListAnswerDTO createListAnswerDTO : createQuestionChildDTO.getListAnswer()) {
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

                    questionRepository.save(questionChild);
                }

            }

            if (createQuestionDTO.getContentImage() != null && !createQuestionDTO.getContentImage().isEmpty()) {

                Blob blobResponse = fileStorageService.save(createQuestionDTO.getContentImage());

                String fileName = blobResponse.getName();

                Content content = new Content(question, GetExtension.typeFile(fileName), fileName);
                content.setUserCreate(user);
                content.setUserUpdate(user);

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);

                contentRepository.save(content);
            }
            if (createQuestionDTO.getContentAudio() != null && !createQuestionDTO.getContentAudio().isEmpty()) {

                Blob blobResponse = fileStorageService.save(createQuestionDTO.getContentAudio());

                String fileName = blobResponse.getName();

                Content content = new Content(question, GetExtension.typeFile(fileName), fileName);
                content.setUserCreate(user);
                content.setUserUpdate(user);

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);

                contentRepository.save(content);
            }

            questionRepository.save(question);

            Topic topic = findTopicById(topicId);

            if (existPartInTopic(topic, part))
                MessageResponseHolder.setMessage("Part of Question don't have in Topic");
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
    public QuestionResponse addQuestionToTopic(UUID topicId, SaveQuestionDTO createQuestionDTO) {

        User user = userService.currentUser();

        Part part = partService.getPartToId(createQuestionDTO.getPartId());

        Question question = Question.builder()
                .questionContent(createQuestionDTO.getQuestionContent())
                .questionScore(createQuestionDTO.getQuestionScore())
                .userCreate(user)
                .userUpdate(user)
                .part(part)
                .build();

        question = questionRepository.saveAndFlush(question);

        if (createQuestionDTO.getListAnswer() != null && !createQuestionDTO.getListAnswer().isEmpty()) {
            for (SaveListAnswerDTO createListAnswerDTO : createQuestionDTO.getListAnswer()) {
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
            for (SaveQuestionDTO createQuestionChildDTO : createQuestionDTO.getListQuestionChild()) {
                Question questionChild = new Question();
                questionChild.setQuestionGroup(question);
                questionChild.setQuestionContent(createQuestionChildDTO.getQuestionContent());
                questionChild.setQuestionScore(createQuestionChildDTO.getQuestionScore());
                questionChild.setPart(question.getPart());
                questionChild.setUserCreate(user);
                questionChild.setUserUpdate(user);

                questionRepository.save(questionChild);

                for (SaveListAnswerDTO createListAnswerDTO : createQuestionChildDTO.getListAnswer()) {
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

                questionRepository.save(questionChild);
            }

        }

        if (createQuestionDTO.getContentImage() != null && !createQuestionDTO.getContentImage().isEmpty()) {

            Blob blobResponse = fileStorageService.save(createQuestionDTO.getContentImage());

            String fileName = blobResponse.getName();

            Content content = new Content(question, GetExtension.typeFile(fileName), fileName);
            content.setUserCreate(user);
            content.setUserUpdate(user);

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            question.getContentCollection().add(content);
            contentRepository.save(content);

        }
        if (createQuestionDTO.getContentAudio() != null && !createQuestionDTO.getContentAudio().isEmpty()) {

            Blob blobResponse = fileStorageService.save(createQuestionDTO.getContentAudio());

            String fileName = blobResponse.getName();

            Content content = new Content(question, GetExtension.typeFile(fileName), fileName);
            content.setUserCreate(user);
            content.setUserUpdate(user);

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            question.getContentCollection().add(content);
            contentRepository.save(content);

        }

        question = questionRepository.saveAndFlush(question);

        Topic topic = findTopicById(topicId);

        if(existPartInTopic(topic, part)) {
            MessageResponseHolder.setMessage("Part of Question don't have in Topic");
            return null;
        }
        else {
            topic.setUserUpdate(user);
            topic.setUpdateAt(LocalDateTime.now());
            topic.getQuestions().add(question);
            topicRepository.save(topic);

            Question question1 = questionService.getQuestionById(question.getQuestionId());
            QuestionResponse questionResponse = new QuestionResponse(question1);

            if (questionService.checkQuestionGroup(question1.getQuestionId())) {
                List<Question> questionGroupList = questionService.listQuestionGroup(question1);
                List<QuestionResponse> questionGroupResponseList = new ArrayList<>();

                for (Question questionGroup : questionGroupList) {
                    QuestionResponse questionGroupResponse;

                    Answer answerCorrect = answerService.correctAnswer(questionGroup);
                    questionGroupResponse = new QuestionResponse(questionGroup, answerCorrect);
                    questionGroupResponseList.add(questionGroupResponse);
                    questionResponse.setQuestionGroup(questionGroupResponseList);
                }
            } else {
                Answer answerCorrect = answerService.correctAnswer(question1);
                questionResponse.setAnswerCorrect(answerCorrect.getAnswerId());
            }

            MessageResponseHolder.setMessage("Add Question to Topic successfully");
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

        BooleanExpression wherePattern = QTopic.topic.isNotNull();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {

            boolean isUser = authentication.getAuthorities().stream().anyMatch(
                    auth -> auth.getAuthority().equals("ROLE_" + RoleEnum.USER.name())
                                );

            if(isUser) wherePattern = wherePattern.and(QTopic.topic.enable.eq(Boolean.TRUE));

        }

        if (filterRequest.getPackId() != null)
            wherePattern = wherePattern.and(QTopic.topic.pack.packId.eq(filterRequest.getPackId()));

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty())
            wherePattern = wherePattern.and(QTopic.topic.topicName.lower().like("%" + filterRequest.getSearch().toLowerCase() + "%"));


        if (filterRequest.getType() != null && !filterRequest.getType().isEmpty())
            wherePattern = wherePattern.and(QTopic.topic.topicType.lower().like(filterRequest.getType().toLowerCase()));

        long totalElements = Optional.ofNullable(
                            queryFactory
                                    .select(QTopic.topic.count())
                                    .from(QTopic.topic)
                                    .where(wherePattern)
                                    .fetchOne()
                ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterRequest.getSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        OrderSpecifier<?> orderSpecifier;

        if ("name".equalsIgnoreCase(filterRequest.getSortBy())) {
            if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
                orderSpecifier = QTopic.topic.topicName.lower().desc();
            else
                orderSpecifier = QTopic.topic.topicName.lower().asc();

        } else if ("updateAt".equalsIgnoreCase(filterRequest.getSortBy())) {
            if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
                orderSpecifier = QTopic.topic.updateAt.desc();
            else
                orderSpecifier = QTopic.topic.updateAt.asc();

        } else {
            if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
                orderSpecifier = QTopic.topic.updateAt.desc();
            else
                orderSpecifier = QTopic.topic.updateAt.asc();
        }

        JPAQuery<Topic> query = queryFactory.selectFrom(QTopic.topic)
                                            .where(wherePattern)
                                            .orderBy(orderSpecifier)
                                            .offset(filterResponse.getOffset())
                                            .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(TopicResponse::new).toList()
        );

        return filterResponse;
    }
}
