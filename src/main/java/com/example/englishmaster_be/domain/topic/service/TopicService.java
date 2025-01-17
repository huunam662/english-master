package com.example.englishmaster_be.domain.topic.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.content.service.IContentService;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicContentResponse;
import com.example.englishmaster_be.domain.excel_fill.service.IExcelFillService;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.pack.service.IPackService;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.status.service.IStatusService;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.mapper.AnswerMapper;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicQuestionListRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterRequest;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.PartMapper;
import com.example.englishmaster_be.mapper.TopicMapper;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.model.answer.AnswerRepository;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.model.pack.PackEntity;
import com.example.englishmaster_be.model.pack.PackRepository;
import com.example.englishmaster_be.model.pack.QPackEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.part.PartRepository;
import com.example.englishmaster_be.model.part.QPartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.question.QuestionRepository;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.model.topic.QTopicEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.topic.TopicRepository;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.util.FileUtil;
import com.example.englishmaster_be.value.LinkValue;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicService implements ITopicService {

    LinkValue linkValue;

    FileUtil fileUtil;

    JPAQueryFactory jpaQueryFactory;

    TopicRepository topicRepository;

    PartRepository partRepository;

    QuestionRepository questionRepository;

    AnswerRepository answerRepository;

    ContentRepository contentRepository;

    PackRepository packRepository;

    IQuestionService questionService;

    IStatusService statusService;

    IUserService userService;

    IPackService packService;

    IPartService partService;

    IUploadService uploadService;

    IExcelFillService excelService;

    IAnswerService answerService;

    IContentService contentService;

    ITopicService topicService;


    @Transactional
    @Override
    @SneakyThrows
    public TopicEntity saveTopic(TopicRequest topicRequest) {

        UserEntity user = userService.currentUser();

        PackEntity pack = packService.getPackById(topicRequest.getPackId());

        TopicEntity topic;

        if(topicRequest.getTopicId() != null)
            topic = getTopicById(topicRequest.getTopicId());
        else{
            topic = TopicEntity.builder()
                    .userCreate(user)
                    .build();
        }

        TopicMapper.INSTANCE.flowToTopicEntity(topicRequest, topic);

        if(topicRequest.getTopicImage() != null && !topicRequest.getTopicImage().isEmpty()){

            if(topic.getTopicImage() != null && !topic.getTopicImage().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(topic.getTopicImage())
                                .build()
                );

            topic.setTopicImage(topicRequest.getTopicImage());
        }

        if(topicRequest.getListPart() != null){

            List<PartEntity> partList = topicRequest.getListPart().stream()
                    .filter(Objects::nonNull)
                    .map(partService::getPartToId)
                    .toList();

            topic.setParts(partList);
        }

        topic.setStatus(statusService.getStatusByName("ACTIVE"));
        topic.setUserUpdate(user);
        topic.setPack(pack);
        topic.setEnable(Boolean.TRUE);

        return topicRepository.save(topic);
    }

    @Transactional
    @Override
    public ExcelTopicResponse updateTopicByExcelFile(UUID topicId, MultipartFile file) {

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        ExcelTopicContentResponse excelTopicContentResponse = excelService.readTopicContentFromExcel(file);

        PackEntity packEntity = jpaQueryFactory.selectFrom(QPackEntity.packEntity).where(
                QPackEntity.packEntity.packName.equalsIgnoreCase(excelTopicContentResponse.getPackName())
        ).fetchOne();

        if(packEntity == null) {

            packEntity = PackEntity.builder()
                    .packId(UUID.randomUUID())
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .packName(excelTopicContentResponse.getPackName())
                    .build();

            packEntity = packRepository.save(packEntity);
        }

        TopicMapper.INSTANCE.flowToTopicEntity(excelTopicContentResponse, topicEntity);

        topicEntity.setPack(packEntity);

        topicEntity = topicRepository.save(topicEntity);

        if(topicEntity.getParts() == null)
            topicEntity.setParts(new ArrayList<>());

        int partNamesSize = excelTopicContentResponse.getPartNamesList().size();

        for(int i = 0; i < partNamesSize; i++) {

            String partNameAtI = excelTopicContentResponse.getPartNamesList().get(i);

            String partTypeAtI = excelTopicContentResponse.getPartTypesList().get(i);

            PartEntity partEntity = jpaQueryFactory.selectFrom(QPartEntity.partEntity).where(
                    QPartEntity.partEntity.partName.equalsIgnoreCase(partNameAtI)
                            .and(QPartEntity.partEntity.partType.equalsIgnoreCase(partTypeAtI))
            ).fetchOne();

            if (partEntity == null)
                partEntity = PartEntity.builder()
                        .partId(UUID.randomUUID())
                        .contentData("")
                        .contentType(fileUtil.mimeTypeFile(""))
                        .partName(partNameAtI)
                        .partType(partTypeAtI)
                        .partDescription(String.join(": ", List.of(partNameAtI, partTypeAtI)))
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();

            if(partEntity.getTopics() == null)
                partEntity.setTopics(List.of(topicEntity));
            else if(!partEntity.getTopics().contains(topicEntity))
                partEntity.getTopics().add(topicEntity);

            partEntity = partRepository.save(partEntity);

            if(!topicEntity.getParts().contains(partEntity))
                topicEntity.getParts().add(partEntity);
        }

        return TopicMapper.INSTANCE.toExcelTopicResponse(topicEntity);

    }

    @Transactional
    @SneakyThrows
    @Override
    public ExcelTopicResponse saveTopicByExcelFile(MultipartFile file) {

        return excelService.importTopicExcel(file);
    }

    @Transactional
    @Override
    public TopicEntity uploadFileImage(UUID topicId, MultipartFile contentData) {

        if(contentData == null || contentData.isEmpty())
            throw new BadRequestException("File required non empty or null content");

        UserEntity user = userService.currentUser();

        TopicEntity topic = getTopicById(topicId);

        FileResponse fileResponse = uploadService.upload(contentData);

        topic.setTopicImage(fileResponse.getUrl());
        topic.setUserUpdate(user);
        topic.setUpdateAt(LocalDateTime.now());

        return topicRepository.save(topic);
    }

    @Override
    public TopicEntity getTopicById(UUID topicId) {

        return topicRepository.findByTopicId(topicId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Topic not found with ID: " + topicId)
                );
    }

    @Override
    public TopicEntity getTopicByName(String topicName) {

        return topicRepository.findByTopicName(topicName).orElseThrow(
                () -> new IllegalArgumentException("Topic not found with name: " + topicName)
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
    public List<TopicEntity> getTopicsByStartTime(LocalDate startTime) {

        LocalDateTime startTimeParse = startTime.atStartOfDay();

        return topicRepository.findByStartTime(startTimeParse);
    }

    @Override
    public List<String> getImageCdnLinkTopic() {

        List<String> listLinkCdn = topicRepository.findAllTopicImages();

        MessageResponseHolder.setMessage("Found " + listLinkCdn.size() + " links");

        return listLinkCdn.stream()
                .filter(linkCdn -> linkCdn != null && !linkCdn.isEmpty())
                .map(linkCdn -> linkValue.getLinkFileShowImageBE() + linkCdn)
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

        String statusName = enable ? "ACTIVE" : "DEACTIVATE";

        StatusEntity statusUpdate = statusService.getStatusByName(statusName);

        topic.setStatus(statusUpdate);

        topicRepository.save(topic);

        MessageResponseHolder.setMessage(enable ? "TopicEntity enabled successfully" : "TopicEntity disabled successfully");

    }


    @Override
    public List<QuestionPartResponse> getQuestionOfToTopicPart(UUID topicId, String partName) {

        return questionService.getAllPartQuestions(partName, topicId);
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
    public ExcelQuestionListResponse addQuestionAllPartsToTopicByExcelFile(UUID topicId, MultipartFile file) {

        return excelService.importQuestionAllPartsExcel(topicId, file);
    }

    @Override
    public ExcelTopicResponse addAllPartsForTopicByExcelFile(UUID topicId, MultipartFile file) {

        return excelService.importAllPartsForTopicExcel(topicId, file);
    }

    @Transactional
    protected QuestionEntity saveQuestionFromExcelTemplate(ExcelQuestionResponse questionByExcelFileResponse, UserEntity user) {

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

            content = contentRepository.save(content);

            if(!question.getContentCollection().contains(content))
                question.getContentCollection().add(content);

            contentRepository.save(content);
        }

        if (contentAudio != null) {

            ContentEntity content = contentService.getContentByContentData(contentAudio);

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            question.getContentCollection().add(content);

            contentRepository.save(content);
        }
    }


    @Transactional
    protected void processQuestions(ExcelQuestionListResponse listQuestionByExcelFileResponse, UUID topicId, UserEntity user) {

        for (ExcelQuestionResponse questionByExcelFileResponse : listQuestionByExcelFileResponse.getQuestions()) {

            // Tạo câu hỏi và lưu nó trước khi xử lý câu trả lời

            QuestionEntity question = saveQuestionFromExcelTemplate(questionByExcelFileResponse, user);

            // Xử lý câu trả lời
            processAnswers(AnswerMapper.INSTANCE.toAnswerRequestList(questionByExcelFileResponse.getAnswers()), question, user);

            // Tương tự cho questionChild
            if (questionByExcelFileResponse.getQuestionsChildren() != null && !questionByExcelFileResponse.getQuestionsChildren().isEmpty()) {
                for (ExcelQuestionResponse createQuestionChildDTO : questionByExcelFileResponse.getQuestionsChildren()) {

                    QuestionEntity questionChild = saveQuestionFromExcelTemplate(createQuestionChildDTO, user);

                    questionChild.setQuestionGroupParent(question);

                    questionChild = questionRepository.save(questionChild); // Lưu câu hỏi con trước

                    processAnswers(AnswerMapper.INSTANCE.toAnswerRequestList(createQuestionChildDTO.getAnswers()), questionChild, user);

                    processContent(
                            null,
                            null,
                            questionChild,
                            user
                    );
                }
            }

            // Xử lý ContentEntity
            processContent(null, null, question, user);

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
    public ExcelQuestionListResponse addQuestionForTopicAndPartByExcelFile(UUID topicId, int partNumber, MultipartFile file) {

        return excelService.importQuestionForTopicAndPart(topicId, partNumber, file);
    }

    @Transactional
    @Override
    public void addListQuestionToTopic(UUID topicId, TopicQuestionListRequest listQuestionRequest) {

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

                ContentEntity content = ContentEntity.builder()
                        .contentData(questionRequest.getContentImage())
                        .contentType(fileUtil.mimeTypeFile(questionRequest.getContentImage()))
                        .userCreate(user)
                        .userUpdate(user)
                        .build();

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);

                contentRepository.save(content);
            }
            if (questionRequest.getContentAudio() != null && !questionRequest.getContentAudio().isEmpty()) {

                ContentEntity content = ContentEntity.builder()
                        .contentData(questionRequest.getContentAudio())
                        .contentType(fileUtil.mimeTypeFile(questionRequest.getContentAudio()))
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

        Boolean isAdmin = user.getRole().getRoleName().equals(RoleEnum.ADMIN);

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

            ContentEntity content = ContentEntity.builder()
                    .contentData(questionRequest.getContentImage())
                    .contentType(fileUtil.mimeTypeFile(questionRequest.getContentImage()))
                    .userCreate(user)
                    .userUpdate(user)
                    .build();

            question.getContentCollection().add(content);

            contentRepository.save(content);

        }

        if (questionRequest.getContentAudio() != null && !questionRequest.getContentAudio().isEmpty()) {

            ContentEntity content = ContentEntity.builder()
                    .contentData(questionRequest.getContentImage())
                    .contentType(fileUtil.mimeTypeFile(questionRequest.getContentAudio()))
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
            QuestionResponse questionResponse = QuestionMapper.INSTANCE.toQuestionResponse(question1, topic, part, isAdmin);

            if (questionService.checkQuestionGroup(question1.getQuestionId())) {
                List<QuestionEntity> questionGroupList = questionService.listQuestionGroup(question1);
                List<QuestionResponse> questionGroupResponseList = new ArrayList<>();

                for (QuestionEntity questionGroup : questionGroupList) {

                    AnswerEntity answerCorrect = answerService.correctAnswer(questionGroup);
                    QuestionResponse questionGroupResponse = QuestionMapper.INSTANCE.toQuestionResponse(questionGroup, topic, part, isAdmin);
                    questionGroupResponse.setAnswerCorrectId(answerCorrect.getAnswerId());
                    questionGroupResponseList.add(questionGroupResponse);
                }

                questionResponse.setQuestionsChildren(questionGroupResponseList);
            } else {
                AnswerEntity answerCorrect = answerService.correctAnswer(question1);
                questionResponse.setAnswerCorrectId(answerCorrect.getAnswerId());
            }

            MessageResponseHolder.setMessage("Add QuestionEntity to TopicEntity successfully");

            return questionResponse;
        }
    }

    @Override
    public FilterResponse<?> getAllTopic(TopicFilterRequest filterRequest) {

        FilterResponse<TopicResponse> filterResponse = FilterResponse.<TopicResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getPageSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getPageSize())
                .build();

        BooleanExpression wherePattern = QTopicEntity.topicEntity.isNotNull();

        UserEntity currentUser = userService.currentUser();

        boolean isUser = currentUser.getRole().getRoleName().equals(RoleEnum.USER);

        if(isUser) wherePattern = wherePattern.and(QTopicEntity.topicEntity.enable.eq(Boolean.TRUE));

        if (filterRequest.getPackId() != null)
            wherePattern = wherePattern.and(QTopicEntity.topicEntity.pack.packId.eq(filterRequest.getPackId()));

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty())
            wherePattern = wherePattern.and(QTopicEntity.topicEntity.topicName.lower().like("%" + filterRequest.getSearch().toLowerCase() + "%"));

        if (filterRequest.getType() != null && !filterRequest.getType().isEmpty())
            wherePattern = wherePattern.and(QTopicEntity.topicEntity.topicType.lower().like(filterRequest.getType().toLowerCase()));

        long totalElements = Optional.ofNullable(
                            jpaQueryFactory
                                    .select(QTopicEntity.topicEntity.count())
                                    .from(QTopicEntity.topicEntity)
                                    .where(wherePattern)
                                    .fetchOne()
                ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterRequest.getPageSize());
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

        JPAQuery<TopicEntity> query = jpaQueryFactory.selectFrom(QTopicEntity.topicEntity)
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
