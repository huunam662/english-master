package com.example.englishmaster_be.domain.topic.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.excel.util.ExcelUtil;
import com.example.englishmaster_be.domain.pack.dto.IPackKeyProjection;
import com.example.englishmaster_be.domain.pack_type.model.PackTypeEntity;
import com.example.englishmaster_be.domain.pack_type.service.IPackTypeService;
import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.question.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyRes;
import com.example.englishmaster_be.domain.topic.mapper.TopicMapper;
import com.example.englishmaster_be.domain.topic.repository.jdbc.TopicJdbcRepository;
import com.example.englishmaster_be.domain.topic.repository.spec.TopicSpecification;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.topic_type.service.ITopicTypeService;
import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.pack.service.IPackService;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.upload.meu.service.IUploadService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.domain.topic.dto.request.TopicReq;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterReq;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicRes;
import com.example.englishmaster_be.domain.answer.repository.jpa.AnswerRepository;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.part.repository.jpa.PartRepository;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.topic.repository.jpa.TopicRepository;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.question.util.QuestionUtil;
import com.example.englishmaster_be.domain.topic.util.TopicUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicService implements ITopicService {

    TopicRepository topicRepository;

    PartRepository partRepository;

    QuestionRepository questionRepository;

    AnswerRepository answerRepository;

    IUserService userService;

    IPackService packService;

    IPartService partService;

    IUploadService uploadService;
    TopicJdbcRepository topicJdbcRepository;
    ITopicService topicService;
    IPackTypeService packTypeService;
    ITopicTypeService topicTypeService;

    @Transactional
    @Override
    @SneakyThrows
    public TopicEntity createTopic(TopicReq topicRequest) {

        UserEntity user = userService.currentUser();

        PackEntity pack = packService.getPackById(topicRequest.getPackId());

        TopicEntity topic = TopicEntity.builder()
                .userCreate(user)
                .userUpdate(user)
                .pack(pack)
                .enable(true)
                .numberQuestion(0)
                .build();

        TopicMapper.INSTANCE.flowToTopicEntity(topicRequest, topic);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            topic.setWorkTime(LocalTime.parse(topicRequest.getWorkTime(), formatter));
        }
        catch (Exception e){

            log.error(e.getMessage());

            throw new ErrorHolder(Error.BAD_REQUEST, "Work time must be pattern HH:mm:ss");
        }

        if(topicRequest.getTopicImage() != null && !topicRequest.getTopicImage().isEmpty()){

            if(topic.getTopicImage() != null && !topic.getTopicImage().isEmpty())
                try {
                    uploadService.delete(topic.getTopicImage());
                } catch (Exception e){
                    log.error("{} -> code {}", e.getMessage(), Error.CONFLICT.getStatusCode());
                }

            topic.setTopicImage(topicRequest.getTopicImage());
        }

        if(topicRequest.getListPart() != null){

            Set<PartEntity> partList = topicRequest.getListPart().stream()
                    .filter(Objects::nonNull)
                    .map(partService::getPartToId)
                    .collect(Collectors.toSet());

            topic.setParts(partList);

            topic.setNumberQuestion(
                    QuestionUtil.totalQuestionChildOf(partList, topic)
            );
        }

        return topicRepository.save(topic);
    }

    @Transactional
    @Override
    public TopicEntity updateTopic(UUID topicId, TopicReq topicRequest) {

        UserEntity user = userService.currentUser();

        PackEntity pack = packService.getPackById(topicRequest.getPackId());

        TopicEntity topic = getTopicById(topicId);

        TopicMapper.INSTANCE.flowToTopicEntity(topicRequest, topic);
        topic.setUserUpdate(user);
        topic.setPack(pack);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            topic.setWorkTime(LocalTime.parse(topicRequest.getWorkTime(), formatter));
        }
        catch (Exception e){
            throw new ErrorHolder(Error.BAD_REQUEST, "Work time must be pattern HH:mm:ss");
        }

        if(topicRequest.getTopicImage() != null && !topicRequest.getTopicImage().isEmpty()){

            if(topic.getTopicImage() != null && !topic.getTopicImage().isEmpty())
                try {
                    uploadService.delete(topic.getTopicImage());
                } catch (Exception e){
                    log.error("{} -> code {}", e.getMessage(), Error.CONFLICT.getStatusCode());
                }

            topic.setTopicImage(topicRequest.getTopicImage());
        }

        if(topicRequest.getListPart() != null){

            Set<PartEntity> partList = topicRequest.getListPart().stream()
                    .filter(Objects::nonNull)
                    .map(partService::getPartToId)
                    .collect(Collectors.toSet());

            topic.setParts(partList);

            topic.setNumberQuestion(
                    QuestionUtil.totalQuestionChildOf(partList, topic)
            );
        }

        return topicRepository.save(topic);
    }

    @Override
    public TopicEntity  getTopicById(UUID topicId) {

        return topicRepository.findByTopicId(topicId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Topic not found with ID: " + topicId, false)
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


        Pageable pageable = PageRequest.of(0, 7, Sort.by(Sort.Order.asc("partName")));

        Page<PartEntity> page = partRepository.findByTopics(topicId, pageable);

        return page.getContent().stream().map(
                partItem -> {

//                    int totalQuestion = totalQuestion(partItem, topicId);

                    PartResponse partResponse = PartMapper.INSTANCE.toPartResponse(partItem);
//                    partResponse.setTotalQuestion(totalQuestion);

                    return partResponse;
                }
        ).toList();
    }

    @Override
    public List<QuestionEntity> getQuestionOfPartToTopic(UUID topicId, UUID partId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        PartEntity part = partService.getPartToId(partId);

        List<QuestionEntity> listQuestionParent = questionRepository.findByTopicsAndPart(topic.getTopicId(), part.getPartId());

        List<UUID> parentIds = listQuestionParent.stream().map(QuestionEntity::getQuestionId).toList();

        List<QuestionEntity> questionChilds = questionRepository.findAllQuestionChildOfParentIn(parentIds);

        List<UUID> childIds = questionChilds.stream().map(QuestionEntity::getQuestionId).toList();

        List<AnswerEntity> answersChild = answerRepository.findAnswersInQuestionIds(childIds);

        Map<UUID, List<QuestionEntity>> questionParentChildGroup = questionChilds.stream().collect(
                Collectors.groupingBy(QuestionEntity::getQuestionGroupId)
        );

        Map<UUID, List<AnswerEntity>> answerParentChildGroup = answersChild.stream().collect(
                Collectors.groupingBy(AnswerEntity::getQuestionChildId)
        );

        for(QuestionEntity questionParent : listQuestionParent){
            if(questionParent == null) continue;
            List<QuestionEntity> questionChildsList = questionParentChildGroup.getOrDefault(questionParent.getQuestionId(), Collections.emptyList());
            for(QuestionEntity questionChild : questionChildsList){
                if(questionChild == null) continue;
                List<AnswerEntity> answersList = answerParentChildGroup.getOrDefault(questionChild.getQuestionId(), Collections.emptyList());
                questionChild.setAnswers(new LinkedHashSet<>(answersList));
            }
            questionParent.setQuestionGroupChildren(new LinkedHashSet<>(questionChildsList));
        }

        Collections.shuffle(listQuestionParent);

        return listQuestionParent;
    }


    @Override
    public void addPartToTopic(UUID topicId, UUID partId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        PartEntity part = partService.getPartToId(partId);

        if (topic.getParts() == null)
            topic.setParts(new LinkedHashSet<>());

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

        throw new ErrorHolder(Error.BAD_REQUEST, "Delete Part to Topic fail: Topic don't have Part");
    }

    @Transactional
    @Override
    public void deleteTopic(UUID topicId) {

        TopicEntity topic = getTopicById(topicId);

        topicRepository.delete(topic);
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


    @Transactional
    @Override
    public void enableTopic(UUID topicId, boolean enable) {

        TopicEntity topic = getTopicById(topicId);

        topic.setEnable(enable);

        topic.setUpdateAt(LocalDateTime.now());

        topicRepository.save(topic);
    }

    @Override
    public List<QuestionPartResponse> getQuestionOfToTopicPart(UUID topicId, UUID partId) {

        Assert.notNull(topicId, "Topic id is required.");
        Assert.notNull(partId, "Part id is required.");

        TopicEntity topic = getTopicById(topicId);

        List<AnswerEntity> answersQuestionChild = answerRepository.findAnswersJoinQuestionPartTopic(topicId, partId);

        TopicUtil.fillAnswerToTopic(topic, answersQuestionChild);

        return QuestionMapper.INSTANCE.toQuestionPartResponseList(topic);
    }

    @Override
    public List<QuestionPartResponse> getQuestionOfToTopicPart(UUID topicId, String partName) {

        Assert.notNull(topicId, "Topic id is required.");
        Assert.notNull(partName, "Part name is required.");

        TopicEntity topic = getTopicById(topicId);

        if (topic.getTopicType().getTopicTypeName().equalsIgnoreCase("speaking")){
            List<QuestionEntity> questionSpeakings = questionRepository.findAllQuestionSpeakingOfTopicAndPart(topicId, partName);
            TopicUtil.fillQuestionSpeakingOrWritingToTopic(topic, questionSpeakings);
        }
        else if (topic.getTopicType().getTopicTypeName().equalsIgnoreCase("writing")){
            List<QuestionEntity> questionWritings = questionRepository.findAllQuestionWritingOfTopicAndPart(topicId, partName);
            TopicUtil.fillQuestionSpeakingOrWritingToTopic(topic, questionWritings);
        }
        else{
            List<AnswerEntity> answersQuestionChild = answerRepository.findAnswersJoinQuestionPartTopic(topicId, partName);
            TopicUtil.fillAnswerToTopic(topic, answersQuestionChild);
        }

        return QuestionMapper.INSTANCE.toQuestionPartResponseList(topic);
    }


    @Override
    public FilterResponse<?> filterTopics(TopicFilterReq filterRequest) {

        if(filterRequest.getPage() < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Page must be begin at 1.");

        int page = filterRequest.getPage() - 1;

        Pageable pageable = PageRequest.of(page, filterRequest.getPageSize());

        Specification<TopicEntity> spec = TopicSpecification.filterTopics(filterRequest);

        Page<TopicEntity> pageResult = topicRepository.findAll(spec, pageable);

        List<TopicEntity> topicResult = pageResult.getContent().stream().distinct().toList();

        return FilterResponse.<TopicRes>builder()
                .pageNumber(pageResult.getNumber() + 1)
                .pageSize(pageResult.getSize())
                .totalPages((long) pageResult.getTotalPages())
                .offset(pageable.getOffset())
                .contentLength(topicResult.size())
                .content(TopicMapper.INSTANCE.toTopicResponseList(topicResult))
                .build();
    }

    @Override
    public List<QuestionPartResponse> getQuestionPartListOfTopic(UUID topicId) {

        Assert.notNull(topicId, "Topic id is required.");

        TopicEntity topic = getTopicById(topicId);

        if (topic.getTopicType().getTopicTypeName().equalsIgnoreCase("speaking")){
            List<QuestionEntity> questionSpeakings = questionRepository.findAllQuestionSpeakingOfTopic(topicId);
            TopicUtil.fillQuestionSpeakingOrWritingToTopic(topic, questionSpeakings);
        }
        else if (topic.getTopicType().getTopicTypeName().equalsIgnoreCase("writing")){
            List<QuestionEntity> questionWritings = questionRepository.findAllQuestionWritingOfTopic(topicId);
            TopicUtil.fillQuestionSpeakingOrWritingToTopic(topic, questionWritings);
        }
        else{
            List<AnswerEntity> answersQuestionChild = answerRepository.findAnswersJoinQuestionPartTopic(topicId);
            TopicUtil.fillAnswerToTopic(topic, answersQuestionChild);
        }

        return QuestionMapper.INSTANCE.toQuestionPartResponseList(topic);
    }

    @Transactional
    @Override
    public TopicKeyRes updateTopicToExcel(MultipartFile file, UUID topicId, String imageUrl, String audioUrl) throws BadRequestException {
        Assert.notNull(topicId, "Topic id is required.");
        Assert.notNull(file, "Excel file is required.");
        UserEntity userCurrent = userService.currentUser();
        if(!topicRepository.existsById(topicId))
            throw new EntityNotFoundException("Topic not found with id.");
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);
            String packTypeName = ExcelUtil.getStringCellValue(sheet.getRow(0).getCell(1));
            UUID packTypeIdToName = packTypeService.getPackTypeIdByName(packTypeName);
            if(packTypeIdToName == null){
                packTypeIdToName = UUID.randomUUID();
                String packTypeDescription = ExcelUtil.getStringCellValue(sheet.getRow(1).getCell(1));
                PackTypeEntity packType = new PackTypeEntity();
                packType.setId(packTypeIdToName);
                packType.setName(packTypeName);
                packType.setDescription(packTypeDescription);
                packType.setCreatedBy(userCurrent);
                packType.setUpdatedBy(userCurrent);
                packTypeService.savePackType(packType);
            }
            String packName = ExcelUtil.getStringCellValue(sheet.getRow(2).getCell(1));
            IPackKeyProjection packKeyProjection = packService.getPackKeyProjection(packName);
            UUID packIdToName = packKeyProjection != null ? packKeyProjection.getPackId() : null;
            if(packIdToName == null || !packKeyProjection.getPackTypeId().equals(packTypeIdToName)){
                packIdToName = UUID.randomUUID();
                PackEntity pack = new PackEntity();
                pack.setPackId(packIdToName);
                pack.setPackName(packName);
                pack.setPackTypeId(packTypeIdToName);
                pack.setUserCreate(userCurrent);
                pack.setUserUpdate(userCurrent);
                packService.savePack(pack);
            }
            String topicTypeName = ExcelUtil.getStringCellValue(sheet.getRow(3).getCell(1));
            UUID topicTypeIdToName = topicTypeService.getTopicTypeIdToName(topicTypeName);
            if(topicTypeIdToName == null){
                topicTypeIdToName = UUID.randomUUID();
                TopicTypeEntity topicType = new TopicTypeEntity();
                topicType.setTopicTypeId(topicTypeIdToName);
                topicType.setTopicTypeName(topicTypeName);
                topicType.setUserCreate(userCurrent);
                topicType.setUserUpdate(userCurrent);
                topicTypeService.saveTopicType(topicType);
            }
            String topicName = ExcelUtil.getStringCellValue(sheet.getRow(4).getCell(1));
            String topicImage = imageUrl != null ? imageUrl : ExcelUtil.getStringCellValue(sheet.getRow(5).getCell(1));
            if(topicImage == null || topicImage.isEmpty()){
                topicImage = topicRepository.findTopicImageById(topicId);
            }
            String topicAudio = audioUrl != null ? audioUrl : ExcelUtil.getStringCellValue(sheet.getRow(6).getCell(1));
            if(topicAudio == null || topicAudio.isEmpty()){
                topicAudio = topicRepository.findTopicAudioById(topicId);
            }
            String topicDescription = ExcelUtil.getStringCellValue(sheet.getRow(7).getCell(1));
            LocalTime workTime;
            try{
                Cell cellWorkTime = sheet.getRow(8).getCell(1);
                if(cellWorkTime.getCellType().equals(CellType.STRING)){
                    workTime = LocalTime.parse(
                            ExcelUtil.getStringCellValue(cellWorkTime),
                            DateTimeFormatter.ofPattern("HH:mm[:ss]")
                    );
                }
                else{
                    workTime = LocalDateTime.parse(
                            ExcelUtil.getStringCellValue(cellWorkTime),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    ).toLocalTime();
                }
            }
            catch (Exception e){
                log.error(e.getMessage());
                workTime = topicRepository.findWorkTimeById(topicId);
            }
            topicJdbcRepository.updateTopic(topicId, packIdToName, topicTypeIdToName, userCurrent.getUserId(), topicName, topicImage, topicAudio, topicDescription, workTime);
            return new TopicKeyRes(topicId);
        }
        catch (Exception e){
            throw new BadRequestException("Cannot update topic to excel.");
        }
    }

}
