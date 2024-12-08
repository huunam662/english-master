package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.SaveListAnswerDTO;
import com.example.englishmaster_be.DTO.Question.SaveGroupQuestionDTO;
import com.example.englishmaster_be.DTO.Question.SaveQuestionDTO;
import com.example.englishmaster_be.DTO.Question.UpdateQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.SaveListQuestionDTO;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.DTO.UploadMultiFileDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Mapper.QuestionMapper;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.QuestionGroupResponse;
import com.example.englishmaster_be.Model.Response.QuestionResponse;
import com.example.englishmaster_be.Repository.AnswerRepository;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Repository.PartRepository;
import com.example.englishmaster_be.Repository.QuestionRepository;
import com.example.englishmaster_be.Service.*;
import com.google.cloud.storage.Blob;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionServiceImpl implements IQuestionService {

    QuestionRepository questionRepository;

    ContentRepository contentRepository;

    PartRepository partRepository;

    AnswerRepository answerRepository;

    IUserService userService;

    IPartService partService;

    IContentService contentService;

    IFileStorageService fileStorageService;

    IAnswerService answerService;



    @Transactional
    @Override
    public QuestionResponse saveQuestion(SaveQuestionDTO saveQuestionDTO) {

        User user = userService.currentUser();

        Part part = partService.getPartToId(saveQuestionDTO.getPartId());

        Question question;

        if(saveQuestionDTO instanceof UpdateQuestionDTO updateQuestionDTO){

            question = getQuestionById(updateQuestionDTO.getUpdateQuestionId());

            question.setQuestionContent(updateQuestionDTO.getQuestionContent());
            question.setQuestionScore(updateQuestionDTO.getQuestionScore());
            question.setUserUpdate(user);

            questionRepository.save(question);

            if (updateQuestionDTO.getListAnswer() != null && !updateQuestionDTO.getListAnswer().isEmpty()) {
                for (SaveListAnswerDTO createListAnswerDTO : updateQuestionDTO.getListAnswer()) {
                    Answer answer = answerService.findAnswerToId(createListAnswerDTO.getIdAnswer());
                    answer.setQuestion(question);
                    answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                    answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                    answer.setUserUpdate(user);

                    answerRepository.save(answer);
                }
            }

            if (updateQuestionDTO.getListQuestionChild() != null && !updateQuestionDTO.getListQuestionChild().isEmpty()) {
                for (SaveQuestionDTO createQuestionChildDTO : updateQuestionDTO.getListQuestionChild()) {
                    Question questionChild = getQuestionById(createQuestionChildDTO.getQuestionId());
                    questionChild.setQuestionContent(createQuestionChildDTO.getQuestionContent());
                    questionChild.setQuestionScore(createQuestionChildDTO.getQuestionScore());
                    questionChild.setUserUpdate(user);

                    for (SaveListAnswerDTO createListAnswerDTO : createQuestionChildDTO.getListAnswer()) {
                        Answer answer = answerService.findAnswerToId(createListAnswerDTO.getIdAnswer());
                        answer.setQuestion(questionChild);
                        answer.setAnswerContent(createListAnswerDTO.getContentAnswer());
                        answer.setCorrectAnswer(createListAnswerDTO.isCorrectAnswer());
                        answer.setUserUpdate(user);

                        answerRepository.save(answer);

                    }
                    questionRepository.save(questionChild);
                }
            }

            if (updateQuestionDTO.getContentImage() != null && !updateQuestionDTO.getContentImage().isEmpty()) {
                for (Content content : question.getContentCollection()) {
                    if (content.getContentType().equals("IMAGE")) {
                        question.getContentCollection().remove(content);
                        contentService.delete(content.getContentId());
                        fileStorageService.delete(content.getContentData());
                    }
                }
                String filename = fileStorageService.nameFile(updateQuestionDTO.getContentImage());
                Content content = new Content(question, GetExtension.typeFile(filename), filename);
                content.setUserUpdate(user);
                content.setUserCreate(user);

                if (question.getContentCollection() == null) {
                    question.setContentCollection(new ArrayList<>());
                }
                question.getContentCollection().add(content);
                contentRepository.save(content);
                fileStorageService.save(updateQuestionDTO.getContentImage());
            }
            if (updateQuestionDTO.getContentAudio() != null && !updateQuestionDTO.getContentAudio().isEmpty()) {
                for (Content content : question.getContentCollection()) {
                    if (content.getContentType().equals("AUDIO")) {
                        question.getContentCollection().remove(content);
                        contentService.delete(content.getContentId());
                        fileStorageService.delete(content.getContentData());
                    }
                }
                String filename = fileStorageService.nameFile(updateQuestionDTO.getContentAudio());
                Content content = new Content(question, GetExtension.typeFile(filename), filename);
                content.setUserUpdate(user);
                content.setUserCreate(user);

                if (question.getContentCollection() == null) {
                    question.setContentCollection(new ArrayList<>());
                }
                question.getContentCollection().add(content);
                contentRepository.save(content);
                fileStorageService.save(updateQuestionDTO.getContentAudio());
            }

            questionRepository.save(question);

            Question question1 = getQuestionById(updateQuestionDTO.getUpdateQuestionId());
            QuestionResponse questionResponse = new QuestionResponse(question1);

            if (checkQuestionGroup(question1.getQuestionId())) {

                List<Question> questionGroupList = listQuestionGroup(question1);
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

            return questionResponse;
        }
        else{

            question = QuestionMapper.INSTANCE.toQuestionEntity(saveQuestionDTO);
            question.setCreateAt(LocalDateTime.now());
            question.setUserCreate(user);
            question.setUserUpdate(user);
            question.setPart(part);
            question = questionRepository.save(question);

            return QuestionMapper.INSTANCE.toQuestionResponse(question);
        }
    }

    @Override
    public Question getQuestionById(UUID questionId) {
        return questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Question not found with ID: " + questionId)
                );
    }


    @Override
    public List<Question> getTop10Question(int index, UUID partId) {

        Part part = partRepository.findByPartId(partId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Part not found with ID: " + partId)
                );

        Pageable pageable = PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt")));

        Page<Question> questionPage= questionRepository.findAllByQuestionGroupAndPart(null, part, pageable);

        return questionPage.getContent();
    }

    @Override
    public int countQuestionToQuestionGroup(Question question) {
        int total = 0;
        for(Question questionChild: listQuestionGroup(question)){
            total++;
        }
        return total;
    }

    @Override
    public boolean checkQuestionGroup(UUID questionId) {

        Question question = getQuestionById(questionId);

        boolean isExistedQuestionGroup = questionRepository.existsByQuestionGroup(question);

        if(!isExistedQuestionGroup) throw new BadRequestException("Question doesn't have Question group");

        return true;
    }

    @Override
    public List<Question> listQuestionGroup(Question question) {
        return questionRepository.findAllByQuestionGroup(question);
    }

    @Override
    public void deleteQuestion(UUID questionId) {

        Question question = getQuestionById(questionId);

        questionRepository.delete(question);
    }

    @Transactional
    @Override
    public QuestionResponse uploadFileQuestion(UUID questionId, UploadMultiFileDTO uploadMultiFileDTO) {

        User user = userService.currentUser();

        Question question = getQuestionById(questionId);

        if(question.getContentCollection() == null)
            question.setContentCollection(new ArrayList<>());

        for(var file : uploadMultiFileDTO.getContentData()){

            if(file == null || file.isEmpty()) continue;

            Blob blobResponse = fileStorageService.save(file);

            String fileName = blobResponse.getName();

            Content content = Content.builder()
                    .contentData(fileName)
                    .contentType(GetExtension.getExtension(fileName))
                    .userCreate(user)
                    .userUpdate(user)
                    .question(question)
                    .build();

            question.getContentCollection().add(content);
        }

        question = questionRepository.save(question);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @Transactional
    @Override
    public QuestionResponse updateFileQuestion(UUID questionId, String oldFileName, MultipartFile newFile) {

        User user = userService.currentUser();

        Question question = getQuestionById(questionId);

        Content content = question.getContentCollection().stream().filter(
                contentItem -> contentItem.getContentData().equals(oldFileName)
        ).findFirst().orElseThrow(
                () -> new BadRequestException("Content not found with content image name: " + oldFileName)
        );

        fileStorageService.delete(content.getContentData());

        Blob blobResponse = fileStorageService.save(newFile);

        String fileNameNew = blobResponse.getName();

        content.setContentData(fileNameNew);
        content.setContentType(GetExtension.typeFile(fileNameNew));
        content.setUpdateAt(LocalDateTime.now());
        content.setUserUpdate(user);

        question.setUpdateAt(LocalDateTime.now());
        question.setUserUpdate(user);

        question = questionRepository.save(question);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @Transactional
    @Override
    public QuestionResponse createGroupQuestion(SaveGroupQuestionDTO createGroupQuestionDTO) {

        Question question = QuestionMapper.INSTANCE.toQuestionEntity(createGroupQuestionDTO);

        Question questionGroup = getQuestionById(createGroupQuestionDTO.getQuestionGroupId());

        User user = userService.currentUser();

        question.setQuestionGroup(questionGroup);
        question.setPart(questionGroup.getPart());
        question.setUserCreate(user);
        question.setUserUpdate(user);

        question = questionRepository.save(question);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @Override
    public List<QuestionGroupResponse> getQuestionGroupToQuestion(UUID questionId) {

        Question question = getQuestionById(questionId);

        List<Question> questionGroupList = listQuestionGroup(question);

        return QuestionMapper.INSTANCE.toQuestionGroupResponseList(questionGroupList);
    }
}
