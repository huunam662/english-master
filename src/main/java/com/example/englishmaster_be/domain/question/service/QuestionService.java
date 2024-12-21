package com.example.englishmaster_be.domain.question.service;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.domain.answer_matching.service.IAnswerMatchingService;
import com.example.englishmaster_be.domain.content.service.IContentService;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.question.dto.response.*;
import com.example.englishmaster_be.domain.question_label.service.IQuestionLabelService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.model.question_label.QuestionLabelEntity;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.model.answer.AnswerRepository;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.model.part.PartRepository;
import com.example.englishmaster_be.model.question.QuestionRepository;
import com.example.englishmaster_be.util.FileUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService implements IQuestionService {

    FileUtil fileUtil;

    QuestionRepository questionRepository;

    ContentRepository contentRepository;

    PartRepository partRepository;

    AnswerRepository answerRepository;

    IUserService userService;

    IPartService partService;

    IContentService contentService;

    IAnswerService answerService;

    IAnswerMatchingService answerMatchingService;

    IUploadService uploadService;

    IQuestionLabelService labelService;


    @Transactional
    @SneakyThrows
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
                    if (content.getContentType().contains("image")) {
                        question.getContentCollection().remove(content);
                        contentService.deleteContent(content.getContentId());
                        uploadService.delete(
                                FileDeleteRequest.builder()
                                        .filepath(content.getContentData())
                                        .build()
                        );
                    }
                }

                ContentEntity content = ContentEntity.builder()
                        .question(question)
                        .contentData(questionRequest.getContentImage())
                        .contentType(fileUtil.typeFile(questionRequest.getContentImage()))
                        .userCreate(user)
                        .userUpdate(user)
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .build();

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);
                contentRepository.save(content);
            }
            if (questionRequest.getContentAudio() != null && !questionRequest.getContentAudio().isEmpty()) {
                for (ContentEntity content : question.getContentCollection()) {
                    if (content.getContentType().contains("audio")) {
                        question.getContentCollection().remove(content);
                        contentService.deleteContent(content.getContentId());
                        uploadService.delete(
                                FileDeleteRequest.builder()
                                        .filepath(content.getContentData())
                                        .build()
                        );
                    }
                }

                ContentEntity content = ContentEntity.builder()
                        .question(question)
                        .contentData(questionRequest.getContentAudio())
                        .contentType(fileUtil.typeFile(questionRequest.getContentAudio()))
                        .userCreate(user)
                        .userUpdate(user)
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .build();

                if (question.getContentCollection() == null)
                    question.setContentCollection(new ArrayList<>());

                question.getContentCollection().add(content);
                contentRepository.save(content);
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

            QuestionEntity createQuestion = questionRepository.save(question);

            ContentEntity content = ContentEntity.builder()
                    .question(createQuestion)
                    .contentType(questionRequest.getContentImage())
                    .contentData(fileUtil.typeFile(questionRequest.getContentImage()))
                    .userCreate(user)
                    .userUpdate(user)
                    .createAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();

            if (question.getContentCollection() == null)
                question.setContentCollection(new ArrayList<>());

            question.getContentCollection().add(content);
            contentRepository.save(content);

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

        if(question == null) return 0;

        return listQuestionGroup(question).size();
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
    public QuestionEntity uploadFileQuestion(UUID questionId, List<MultipartFile> uploadMultiFileDTO) {

        if(uploadMultiFileDTO == null || uploadMultiFileDTO.isEmpty())
            throw new BadRequestException("File upload required");

        UserEntity user = userService.currentUser();

        QuestionEntity question = getQuestionById(questionId);

        if(question.getContentCollection() == null)
            question.setContentCollection(new ArrayList<>());

        for(var file : uploadMultiFileDTO){

            if(file == null || file.isEmpty()) continue;

            FileResponse fileResponse = uploadService.upload(file);

            ContentEntity content = ContentEntity.builder()
                    .contentData(fileResponse.getUrl())
                    .contentType(fileResponse.getType())
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
    @SneakyThrows
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

        return listQuestionGroup(question);
    }

    @Override
    public QuestionDto getAllQuestionFromPart(UUID partId) {
        PartEntity part = partRepository.findByPartId(partId)
                .orElseThrow(
                        () -> new IllegalArgumentException("PartEntity not found with ID: " + partId)
                );

        List<QuestionEntity> questionEntities= questionRepository.findByPart(part);

        if(questionEntities == null)
            return null;

        QuestionDto questionDto = new QuestionDto();

        List<QuestionEntity> questionMultipleChoices= filterQuestionByType(questionEntities,QuestionTypeEnum.Multiple_Choice);
        List<QuestionEntity> questionTFNotgiven= filterQuestionByType(questionEntities,QuestionTypeEnum.T_F_Not_Given);
        List<QuestionEntity> questionMatchings= filterQuestionByType(questionEntities,QuestionTypeEnum.Matching);
        List<QuestionEntity> questionFillInBlanks= filterQuestionByType(questionEntities,QuestionTypeEnum.Fill_In_Blank);
        List<QuestionEntity> questionLabels= filterQuestionByType(questionEntities,QuestionTypeEnum.Label);

        List<QuestionMultipleChoiceDto> questionMultipleChoiceDtos = new ArrayList<>();
        List<QuestionMultipleChoiceDto> questionTFNotgivenDtos = new ArrayList<>();
        List<QuestionMatchingDto> questionMatchingDtos = new ArrayList<>();
        List<QuestionFillInBlankDto> questionFillInBlankDtos = new ArrayList<>();
        List<QuestionLabelDto> questionLabelDtos = new ArrayList<>();

        for(QuestionEntity questionEntity: questionMultipleChoices){
            QuestionMultipleChoiceDto questionMultipleChoiceDto = new QuestionMultipleChoiceDto();

            questionMultipleChoiceDto.setQuestionId(questionEntity.getQuestionId());
            questionMultipleChoiceDto.setQuestion(questionEntity.getQuestionContent());
            questionMultipleChoiceDto.setAnswers(
                    questionEntity.getAnswers().stream().map(AnswerEntity::getAnswerContent).collect(Collectors.toList())
            );
            questionMultipleChoiceDto.setNumberOfChoices(questionEntity.getAnswers().size());

            questionMultipleChoiceDtos.add(questionMultipleChoiceDto);
        }

        for(QuestionEntity questionEntity: questionTFNotgiven){
            QuestionMultipleChoiceDto questionMultipleChoiceDto = new QuestionMultipleChoiceDto();

            questionMultipleChoiceDto.setQuestionId(questionEntity.getQuestionId());
            questionMultipleChoiceDto.setQuestion(questionEntity.getQuestionContent());
            List<String> answers = questionMultipleChoiceDto.getAnswers();
            questionMultipleChoiceDto.setAnswers(answers);

            questionMultipleChoiceDtos.add(questionMultipleChoiceDto);
        }

        for( QuestionEntity questionEntity: questionMatchings){
            QuestionMatchingDto questionMatchingDto = new QuestionMatchingDto();

            questionMatchingDto.setQuestionId(questionEntity.getQuestionId());
            questionMatchingDto.setQuestion(questionEntity.getQuestionContent());
            List<AnswerMatchingBasicResponse> answerMatchingBasicResponses=answerMatchingService.getListAnswerMatchingWithShuffle(questionEntity.getQuestionId());
            questionMatchingDto.setOptions(answerMatchingBasicResponses);
            questionMatchingDtos.add(questionMatchingDto);
        }

        for (QuestionEntity questionEntity: questionFillInBlanks){
            QuestionFillInBlankDto questionFillInBlankDto = new QuestionFillInBlankDto();

            questionFillInBlankDto.setQuestion(questionEntity.getQuestionContent());
            questionFillInBlankDto.setQuestionId(questionEntity.getQuestionId());

            questionFillInBlankDtos.add(questionFillInBlankDto);
        }

        for (QuestionEntity questionEntity: questionLabels){

            QuestionLabelDto questionLabelDto = new QuestionLabelDto();
            questionLabelDto.setQuestion(questionEntity.getQuestionContent());
            questionLabelDto.setQuestionId(questionEntity.getQuestionId());

            ContentEntity contentEntity= contentService.getContentByContentId(questionEntity.getContentCollection().get(0).getContentId());

            questionLabelDto.setImage(contentEntity.getContentData());
            questionLabelDto.setHasHints(questionEntity.getHasHints());

            if(questionEntity.getHasHints()){
                List<QuestionLabelEntity> questionLabelEntities=labelService.getLabelByIdQuestion(questionEntity.getQuestionId());
                questionLabelDto.setLabels(
                        questionLabelEntities.stream().map(QuestionLabelEntity::getLabel).collect(Collectors.toList())
                );
            }
            else if(questionEntity.getQuestionType()== QuestionTypeEnum.Label ){
                if(questionEntity.getHasHints()){
                    List<QuestionLabelEntity> questionLabelEntities = questionEntity.getLabels();
                    if(questionLabelEntities == null){
                        questionLabelDto.setLabels(null);
                    }
                    else{
                        questionLabelDto.setLabels(
                                questionLabelEntities.stream().map(QuestionLabelEntity::getLabel).collect(Collectors.toList())
                        );
                    }
                }
            }

            questionLabelDtos.add(questionLabelDto);
        }

        questionDto.setQuestionMultipleChoices(questionMultipleChoiceDtos);
        questionDto.setQuestionTFNotgivens(questionTFNotgivenDtos);
        questionDto.setQuestionFillInBlank(questionFillInBlankDtos);
        questionDto.setQuestionMatchings(questionMatchingDtos);
        questionDto.setQuestionLabels(questionLabelDtos);
        return questionDto;
    }

    List<QuestionEntity> filterQuestionByType( List<QuestionEntity> questionEntities,QuestionTypeEnum type ){
        List<QuestionEntity> filteredQuestionEntities = new ArrayList<>();
        for(QuestionEntity questionEntity: questionEntities){
            if(questionEntity.getQuestionType()== type){
                filteredQuestionEntities.add(questionEntity);
            }
        }
        return filteredQuestionEntities;
    }
}
