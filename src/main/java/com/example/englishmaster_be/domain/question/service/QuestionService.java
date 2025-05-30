package com.example.englishmaster_be.domain.question.service;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.answer.dto.request.Answer1Request;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.content.service.IContentService;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartKeyResponse;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.question.dto.request.*;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.question.dto.response.*;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.mapper.AnswerMapper;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.part.PartQueryFactory;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.question.QuestionQueryFactory;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.model.answer.AnswerRepository;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.model.part.PartRepository;
import com.example.englishmaster_be.model.question.QuestionRepository;
import com.example.englishmaster_be.helper.FileHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "QUESTION-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService implements IQuestionService {

    FileHelper fileUtil;

    QuestionQueryFactory questionQueryFactory;

    QuestionRepository questionRepository;

    ContentRepository contentRepository;

    PartRepository partRepository;

    AnswerRepository answerRepository;

    IUserService userService;

    IPartService partService;

    ITopicService topicService;

    IContentService contentService;

    IAnswerService answerService;

    IUploadService uploadService;

    @Transactional
    @SneakyThrows
    @Override
    public QuestionEntity saveQuestion(QuestionRequest questionRequest) {
        UserEntity currentUser = userService.currentUser();
        PartEntity part = partService.getPartToId(questionRequest.getPartId());


            return createNewQuestion(questionRequest, currentUser, part);
    }


    public QuestionEntity updateQuestion(QuestionUpdateRequest questionupdateRequest){
        UserEntity currentUser = userService.currentUser();

            return updateExistingQuestion(questionupdateRequest.getQuestionId(), questionupdateRequest, currentUser);
        }


    private QuestionEntity updateExistingQuestion(UUID questionId, QuestionUpdateRequest questionRequest, UserEntity currentUser) {
        QuestionEntity question = getQuestionById(questionId);

        updateQuestionProperties(question, questionRequest, currentUser);
        questionRepository.save(question);

        updateAnswersIfNeeded(questionRequest, question, currentUser);

        updateChildQuestionsIfNeeded(questionRequest, currentUser);

        updateQuestionMedia(question, questionRequest, currentUser);

        questionRepository.save(question);
        return getQuestionById(questionId);
    }

    private QuestionEntity createNewQuestion(QuestionRequest questionRequest, UserEntity currentUser, PartEntity part) {
        QuestionEntity question = QuestionMapper.INSTANCE.toQuestionEntity(questionRequest);

        question.setCreateAt(LocalDateTime.now());
        question.setUserCreate(currentUser);
        question.setUserUpdate(currentUser);
        question.setPart(part);
        question.setHasHints(questionRequest.isHasHints());

        QuestionEntity createdQuestion = questionRepository.save(question);

        if (questionRequest.getContentImage() != null && !questionRequest.getContentImage().isEmpty()) {
            addContentToQuestion(
                    createdQuestion,
                    questionRequest.getContentImage(),
                    fileUtil.mimeTypeFile(questionRequest.getContentImage()),
                    currentUser
            );
        }

        if (questionRequest.getContentAudio() != null && !questionRequest.getContentAudio().isEmpty()) {
            addContentToQuestion(
                    createdQuestion,
                    questionRequest.getContentAudio(),
                    fileUtil.mimeTypeFile(questionRequest.getContentAudio()),
                    currentUser
            );
        }

        return questionRepository.save(createdQuestion);
    }

    private void updateQuestionProperties(QuestionEntity question, QuestionUpdateRequest request, UserEntity user) {
        question.setQuestionContent(request.getQuestionContent());
        question.setQuestionScore(request.getQuestionScore());
        question.setUserUpdate(user);
        question.setHasHints(request.isHasHints());
    }

    private void updateAnswersIfNeeded(QuestionUpdateRequest request, QuestionEntity question, UserEntity user) {
        if (request.getListAnswer() != null && !request.getListAnswer().isEmpty()) {
            for (AnswerBasicRequest answerRequest : request.getListAnswer()) {
                AnswerEntity answer = answerService.getAnswerById(answerRequest.getAnswerId());
                answer.setQuestion(question);
                answer.setAnswerContent(answerRequest.getAnswerContent());
                answer.setCorrectAnswer(answerRequest.getCorrectAnswer());
                answer.setUserUpdate(user);

                answerRepository.save(answer);
            }
        }
    }

    private void updateChildQuestionsIfNeeded(QuestionUpdateRequest request, UserEntity user) {
        if (request.getListQuestionChild() != null && !request.getListQuestionChild().isEmpty()) {
            for (QuestionUpdateRequest childRequest : request.getListQuestionChild()) {
                UUID childQuestionId = null; // Thêm logic xác định ID ở đây

                if (childQuestionId != null) {
                    QuestionEntity childQuestion = getQuestionById(childQuestionId);
                    updateQuestionProperties(childQuestion, childRequest, user);

                    for (AnswerBasicRequest answerRequest : childRequest.getListAnswer()) {
                        AnswerEntity answer = answerService.getAnswerById(answerRequest.getAnswerId());
                        answer.setQuestion(childQuestion);
                        answer.setAnswerContent(answerRequest.getAnswerContent());
                        answer.setCorrectAnswer(answerRequest.getCorrectAnswer());
                        answer.setUserUpdate(user);

                        answerRepository.save(answer);
                    }
                    questionRepository.save(childQuestion);
                }
            }
        }
    }

    private void updateQuestionMedia(QuestionEntity question, QuestionUpdateRequest request, UserEntity user) {
        // Handle image content
        if (request.getContentImage() != null && !request.getContentImage().isEmpty()) {
            removeExistingContentByType(question, "image");
            addContentToQuestion(
                    question,
                    request.getContentImage(),
                    fileUtil.mimeTypeFile(request.getContentImage()),
                    user
            );
        }

        if (request.getContentAudio() != null && !request.getContentAudio().isEmpty()) {
            removeExistingContentByType(question, "audio");
            addContentToQuestion(
                    question,
                    request.getContentAudio(),
                    fileUtil.mimeTypeFile(request.getContentAudio()),
                    user
            );
        }
    }

    private void removeExistingContentByType(QuestionEntity question, String contentType) {
        if (question.getContentCollection() != null) {
            for (ContentEntity content : new ArrayList<>(question.getContentCollection())) {
                if (content.getContentType().contains(contentType)) {
                    question.getContentCollection().remove(content);
                    contentService.deleteContent(content.getContentId());
                    try {
                        uploadService.delete(
                                FileDeleteRequest.builder()
                                        .filepath(content.getContentData())
                                        .build()
                        );
                    } catch (FileNotFoundException e) {
                        System.out.println("No Find File" + content.getContentData());
                    } catch (IOException e){
                        System.out.println("Error Delete File" + e.getMessage());
                    }
                }
            }
        }
    }

    private void addContentToQuestion(QuestionEntity question, String contentData, String contentType, UserEntity user) {
        ContentEntity content = ContentEntity.builder()
                .contentData(contentData)
                .contentType(contentType)
                .userCreate(user)
                .userUpdate(user)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        if (question.getContentCollection() == null) {
            question.setContentCollection(new HashSet<>());
        }

        question.getContentCollection().add(content);
        contentRepository.save(content);
    }


    @Override
    public QuestionEntity getQuestionById(UUID questionId) {
        return questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new IllegalArgumentException("QuestionEntity not found with ID: " + questionId)
                );
    }


    @Override
    public List<QuestionEntity> getTop10Question(int index, UUID partId) {

        PartEntity part = partRepository.findByPartId(partId)
                .orElseThrow(
                        () -> new IllegalArgumentException("PartEntity not found with ID: " + partId)
                );

        Pageable pageable = PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt")));

        Page<QuestionEntity> questionPage = questionRepository.findAllByQuestionGroupParentAndPart(null, part, pageable);

        return questionPage.getContent();
    }

    @Override
    public int countQuestionToQuestionGroup(QuestionEntity question) {

        if (question == null) return 0;

        return listQuestionGroup(question).size();
    }

    @Override
    public boolean checkQuestionGroup(UUID questionId) {

        QuestionEntity question = getQuestionById(questionId);

        return questionRepository.existsByQuestionGroupParent(question);
    }

    @Override
    public List<QuestionEntity> getQuestionsParentBy(List<PartEntity> partEntityList, TopicEntity topicEntity) {

        if(partEntityList == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Part list is null");

        if(topicEntity == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Topic entity is null");

        return questionQueryFactory.findAllQuestionsParentBy(topicEntity, partEntityList);
    }

    @Override
    public List<QuestionEntity> listQuestionGroup(QuestionEntity question) {
        return questionRepository.findAllByQuestionGroupParent(question);
    }

    @Override
    public void deleteQuestion(UUID questionId) {

        QuestionEntity question = getQuestionById(questionId);

        questionRepository.delete(question);
    }

    @Transactional
    @Override
    public QuestionEntity uploadFileQuestion(UUID questionId, List<MultipartFile> uploadMultiFileDTO) {

        if (uploadMultiFileDTO == null || uploadMultiFileDTO.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "File upload required");

        UserEntity user = userService.currentUser();

        QuestionEntity question = getQuestionById(questionId);

        if (question.getContentCollection() == null)
            question.setContentCollection(new HashSet<>());

        for (var file : uploadMultiFileDTO) {

            if (file == null || file.isEmpty()) continue;

            FileResponse fileResponse = uploadService.upload(file);

            ContentEntity content = ContentEntity.builder()
                    .contentData(fileResponse.getUrl())
                    .contentType(fileResponse.getType())
                    .userCreate(user)
                    .userUpdate(user)
                    .build();

            question.getContentCollection().add(content);
        }

        return questionRepository.save(question);
    }


    @Transactional
    @Override
    @SneakyThrows
    public QuestionEntity updateFileQuestion(UUID questionId, String oldFileName, MultipartFile newFile) {

        if (newFile == null || newFile.isEmpty())
            throw new ErrorHolder(Error.NULL_OR_EMPTY_FILE);

        UserEntity user = userService.currentUser();

        QuestionEntity question = getQuestionById(questionId);

        ContentEntity content = question.getContentCollection().stream().filter(
                contentItem -> contentItem.getContentData().equals(oldFileName)
        ).findFirst().orElseThrow(
                () -> new ErrorHolder(Error.BAD_REQUEST, "ContentEntity not found with content image name: " + oldFileName, false)
        );

        uploadService.delete(
                FileDeleteRequest.builder()
                        .filepath(content.getContentData())
                        .build()
        );

        FileResponse fileResponse = uploadService.upload(newFile);

        content.setContentData(fileResponse.getUrl());
        content.setContentType(fileResponse.getType());
        content.setUserUpdate(user);

        return questionRepository.save(question);
    }


    @Transactional
    @Override
    public QuestionEntity createGroupQuestion(QuestionGroupRequest createGroupQuestionDTO) {

        QuestionEntity question = QuestionMapper.INSTANCE.toQuestionEntity(createGroupQuestionDTO);

        QuestionEntity questionGroup = getQuestionById(createGroupQuestionDTO.getQuestionGroupId());

        UserEntity user = userService.currentUser();

        question.setQuestionGroupParent(questionGroup);
        question.setPart(questionGroup.getPart());
        question.setUserCreate(user);
        question.setUserUpdate(user);

        return questionRepository.save(question);
    }

    @Override
    public List<QuestionEntity> getQuestionGroupListByQuestionId(UUID questionId) {

        QuestionEntity question = getQuestionById(questionId);

        return question.getQuestionGroupChildren().stream().toList();
    }

    @Override
    public List<QuestionPartResponse> getAllPartQuestions(String partName, UUID topicId) {

        return topicService.getQuestionOfToTopicPart(topicId, partName);
    }


    List<QuestionEntity> filterQuestionByType(List<QuestionEntity> questionEntities, QuestionType type ){
        List<QuestionEntity> filteredQuestionEntities = new ArrayList<>();
        for(QuestionEntity questionEntity: questionEntities){
            if(questionEntity.getQuestionType()== type){
                filteredQuestionEntities.add(questionEntity);
            }
        }
        return filteredQuestionEntities;
    }

    @Transactional
    @Override
    public void createListQuestionsParentOfPart(PartEntity part, List<QuestionParentRequest> questionParentsRequest) {

        UserEntity userCurrent = userService.currentUser();

        if(questionParentsRequest == null || questionParentsRequest.isEmpty())
            return;

        if(part == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Part is null.");

        Set<QuestionEntity> questionParents = QuestionMapper.INSTANCE.toQuestionParentSet(part, questionParentsRequest, userCurrent);

        questionRepository.saveAll(questionParents);
    }

}
