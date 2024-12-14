package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.common.constaint.error.ErrorEnum;
import com.example.englishmaster_be.model.request.Answer.AnswerBasicRequest;
import com.example.englishmaster_be.model.request.Question.GroupQuestionRequest;
import com.example.englishmaster_be.model.request.Question.QuestionRequest;
import com.example.englishmaster_be.model.request.UploadMultiFileRequest;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.helper.GetExtensionHelper;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.repository.AnswerRepository;
import com.example.englishmaster_be.repository.ContentRepository;
import com.example.englishmaster_be.repository.PartRepository;
import com.example.englishmaster_be.repository.QuestionRepository;
import com.example.englishmaster_be.service.*;
import com.example.englishmaster_be.entity.*;
import com.google.cloud.storage.Blob;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionServiceImpl implements IQuestionService {

    GetExtensionHelper getExtensionHelper;

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
    public QuestionEntity saveQuestion(QuestionRequest questionRequest) {

        UserEntity user = userService.currentUser();

        PartEntity part = partService.getPartToId(questionRequest.getPartId());

        QuestionEntity question;

        if(questionRequest.getQuestionId() != null){

            question = getQuestionById(questionRequest.getQuestionId());

            question.setQuestionContent(questionRequest.getQuestionContent());
            question.setQuestionScore(questionRequest.getQuestionScore());
            question.setUserUpdate(user);
            question.setHasHints(questionRequest.isHasHints());

            questionRepository.save(question);

            if (questionRequest.getListAnswer() != null && !questionRequest.getListAnswer().isEmpty()) {
                for (AnswerBasicRequest listAnswerRequest : questionRequest.getListAnswer()) {
                    AnswerEntity answer = answerService.getAnswerById(listAnswerRequest.getAnswerId());
                    answer.setQuestion(question);
                    answer.setAnswerContent(listAnswerRequest.getAnswerContent());
                    answer.setCorrectAnswer(listAnswerRequest.getCorrectAnswer());
                    answer.setUserUpdate(user);

                    answerRepository.save(answer);
                }
            }

            if (questionRequest.getListQuestionChild() != null && !questionRequest.getListQuestionChild().isEmpty()) {
                for (QuestionRequest questionRequestChild : questionRequest.getListQuestionChild()) {
                    QuestionEntity questionChild = getQuestionById(questionRequestChild.getQuestionId());
                    questionChild.setQuestionContent(questionRequestChild.getQuestionContent());
                    questionChild.setQuestionScore(questionRequestChild.getQuestionScore());
                    questionChild.setUserUpdate(user);

                    for (AnswerBasicRequest createListAnswerDTO : questionRequestChild.getListAnswer()) {
                        AnswerEntity answer = answerService.getAnswerById(createListAnswerDTO.getAnswerId());
                        answer.setQuestion(questionChild);
                        answer.setAnswerContent(createListAnswerDTO.getAnswerContent());
                        answer.setCorrectAnswer(createListAnswerDTO.getCorrectAnswer());
                        answer.setUserUpdate(user);

                        answerRepository.save(answer);

                    }
                    questionRepository.save(questionChild);
                }
            }

            if (questionRequest.getContentImage() != null && !questionRequest.getContentImage().isEmpty()) {
                for (ContentEntity content : question.getContentCollection()) {
                    if (content.getContentType().equals("IMAGE")) {
                        question.getContentCollection().remove(content);
                        contentService.deleteContent(content.getContentId());
                        fileStorageService.delete(content.getContentData());
                    }
                }
                String filename = fileStorageService.nameFile(questionRequest.getContentImage());
                ContentEntity content = ContentEntity.builder()
                        .question(question)
                        .contentType(getExtensionHelper.typeFile(filename))
                        .contentData(filename)
                        .userCreate(user)
                        .userUpdate(user)
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .build();

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);
                contentRepository.save(content);
                fileStorageService.save(questionRequest.getContentImage());
            }
            if (questionRequest.getContentAudio() != null && !questionRequest.getContentAudio().isEmpty()) {
                for (ContentEntity content : question.getContentCollection()) {
                    if (content.getContentType().equals("AUDIO")) {
                        question.getContentCollection().remove(content);
                        contentService.deleteContent(content.getContentId());
                        fileStorageService.delete(content.getContentData());
                    }
                }
                String filename = fileStorageService.nameFile(questionRequest.getContentAudio());
                ContentEntity content = ContentEntity.builder()
                        .question(question)
                        .contentType(getExtensionHelper.typeFile(filename))
                        .contentData(filename)
                        .userCreate(user)
                        .userUpdate(user)
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .build();

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);
                contentRepository.save(content);
                fileStorageService.save(questionRequest.getContentAudio());
            }

            questionRepository.save(question);

            return getQuestionById(questionRequest.getQuestionId());
        }
        else{

            question = QuestionMapper.INSTANCE.toQuestionEntity(questionRequest);
            question.setCreateAt(LocalDateTime.now());
            question.setUserCreate(user);
            question.setUserUpdate(user);
            question.setPart(part);
            question.setHasHints(questionRequest.isHasHints());

            return questionRepository.save(question);
        }
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

        Page<QuestionEntity> questionPage= questionRepository.findAllByQuestionGroupParentAndPart(null, part, pageable);

        return questionPage.getContent();
    }

    @Override
    public int countQuestionToQuestionGroup(QuestionEntity question) {
        int total = 0;
        for(QuestionEntity questionChild: listQuestionGroup(question)){
            total++;
        }
        return total;
    }

    @Override
    public boolean checkQuestionGroup(UUID questionId) {

        QuestionEntity question = getQuestionById(questionId);

        return questionRepository.existsByQuestionGroupParent(question);
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
    public QuestionEntity uploadFileQuestion(UUID questionId, UploadMultiFileRequest uploadMultiFileDTO) {

        UserEntity user = userService.currentUser();

        QuestionEntity question = getQuestionById(questionId);

        if(question.getContentCollection() == null)
            question.setContentCollection(new ArrayList<>());

        for(var file : uploadMultiFileDTO.getContentData()){

            if(file == null || file.isEmpty()) continue;

            Blob blobResponse = fileStorageService.save(file);

            String fileName = blobResponse.getName();

            ContentEntity content = ContentEntity.builder()
                    .contentData(fileName)
                    .contentType(getExtensionHelper.getExtension(fileName))
                    .userCreate(user)
                    .userUpdate(user)
                    .question(question)
                    .build();

            question.getContentCollection().add(content);
        }

        return questionRepository.save(question);
    }

    @Transactional
    @Override
    public QuestionEntity updateFileQuestion(UUID questionId, String oldFileName, MultipartFile newFile) {

        if(newFile == null || newFile.isEmpty())
            throw new CustomException(ErrorEnum.NULL_OR_EMPTY_FILE);

        UserEntity user = userService.currentUser();

        QuestionEntity question = getQuestionById(questionId);

        ContentEntity content = question.getContentCollection().stream().filter(
                contentItem -> contentItem.getContentData().equals(oldFileName)
        ).findFirst().orElseThrow(
                () -> new BadRequestException("ContentEntity not found with content image name: " + oldFileName)
        );

        fileStorageService.delete(content.getContentData());

        Blob blobResponse = fileStorageService.save(newFile);

        String fileNameNew = blobResponse.getName();

        content.setContentData(fileNameNew);
        content.setContentType(getExtensionHelper.typeFile(fileNameNew));
        content.setUpdateAt(LocalDateTime.now());
        content.setUserUpdate(user);

        question.setUpdateAt(LocalDateTime.now());
        question.setUserUpdate(user);

        return questionRepository.save(question);
    }

    @Transactional
    @Override
    public QuestionEntity createGroupQuestion(GroupQuestionRequest createGroupQuestionDTO) {

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

        return listQuestionGroup(question);
    }
}
