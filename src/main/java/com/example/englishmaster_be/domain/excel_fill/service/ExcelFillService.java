package com.example.englishmaster_be.domain.excel_fill.service;

import com.example.englishmaster_be.common.constant.ImportExcelType;
import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.common.constant.PartType;
import com.example.englishmaster_be.common.constant.excel.ExcelQuestionConstant;
import com.example.englishmaster_be.domain.excel_fill.dto.response.*;
import com.example.englishmaster_be.domain.pack.dto.IPackKeyProjection;
import com.example.englishmaster_be.domain.pack_type.dto.projection.IPackTypeKeyProjection;
import com.example.englishmaster_be.domain.topic.dto.projection.ITopicKeyProjection;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyResponse;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.topic_type.dto.response.ITopicTypeKeyProjection;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.topic_type.repository.jdbc.TopicTypeJdbcRepository;
import com.example.englishmaster_be.domain.topic_type.repository.jpa.ITopicTypeRepository;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.domain.excel_fill.mapper.ExcelContentMapper;
import com.example.englishmaster_be.domain.topic.mapper.TopicMapper;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.answer.repository.jdbc.AnswerJdbcRepository;
import com.example.englishmaster_be.domain.answer.repository.jpa.AnswerRepository;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.pack.repository.jdbc.PackJdbcRepository;
import com.example.englishmaster_be.domain.pack.repository.factory.PackQueryFactory;
import com.example.englishmaster_be.domain.pack.repository.jpa.PackRepository;
import com.example.englishmaster_be.domain.pack_type.model.PackTypeEntity;
import com.example.englishmaster_be.domain.pack_type.repository.jdbc.PackTypeJdbcRepository;
import com.example.englishmaster_be.domain.pack_type.repository.jpa.PackTypeRepository;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.part.repository.jdbc.PartJdbcRepository;
import com.example.englishmaster_be.domain.part.repository.factory.PartQueryFactory;
import com.example.englishmaster_be.domain.part.repository.jpa.PartRepository;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.question.repository.jdbc.QuestionJdbcRepository;
import com.example.englishmaster_be.domain.topic.repository.jdbc.TopicJdbcRepository;
import com.example.englishmaster_be.domain.excel_fill.util.ExcelUtil;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.topic.repository.factory.TopicQueryFactory;
import com.example.englishmaster_be.domain.topic.repository.jpa.TopicRepository;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.shared.util.FileUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelFillService implements IExcelFillService {

    PartQueryFactory partQueryFactory;

    PackQueryFactory packQueryFactory;

    TopicQueryFactory topicQueryFactory;

    PartRepository partRepository;

    QuestionRepository questionRepository;

    AnswerRepository answerRepository;

    TopicRepository topicRepository;

    PackRepository packRepository;

    IPartService partService;

    ITopicService topicService;

    IUserService userService;

    PackTypeRepository packTypeRepository;

    PackTypeJdbcRepository packTypeJdbcRepository;
    PackJdbcRepository packJdbcRepository;
    TopicJdbcRepository topicJdbcRepository;
    PartJdbcRepository partJdbcRepository;
    QuestionJdbcRepository questionJdbcRepository;
    AnswerJdbcRepository answerJdbcRepository;
    ITopicTypeRepository topicTypeRepository;
    TopicTypeJdbcRepository topicTypeJdbcRepository;



    @Transactional
    @Override
    @SneakyThrows
    public TopicKeyResponse importTopicFromExcel(MultipartFile file) {

        UserEntity userImport = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheetTopic = workbook.getSheetAt(0);

            if(sheetTopic == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "Sheet topic information does not existed.");

            String packTypeName = ExcelUtil.getStringCellValue(sheetTopic.getRow(0).getCell(1));
            String packTypeDescription = ExcelUtil.getStringCellValue(sheetTopic.getRow(1).getCell(1));

            IPackTypeKeyProjection packTypeKey = packTypeRepository.findPackTypeIdByName(packTypeName);

            UUID packTypeId = packTypeKey != null ? packTypeKey.getPackTypeId() : null;

            // Nếu pack type không tồn tại thì thêm mới
            if(packTypeId == null){
                packTypeId = UUID.randomUUID();
                packTypeJdbcRepository.insertPackType(
                        PackTypeEntity.builder()
                                .id(packTypeId)
                                .name(packTypeName)
                                .description(packTypeDescription)
                                .createdBy(userImport)
                                .updatedBy(userImport)
                                .build()
                );
            }

            String packExamName = ExcelUtil.getStringCellValue(sheetTopic.getRow(2).getCell(1));

            IPackKeyProjection packKey = packRepository.findPackIdByName(packExamName);

            UUID packId = packKey != null ? packKey.getPackId() : null;

            // Nếu pack không thuộc pack type thì thêm mới
            if(packId == null || !packTypeId.equals(packKey.getPackTypeId())){
                packId = UUID.randomUUID();
                packJdbcRepository.insertPack(
                        PackEntity.builder()
                                .packId(packId)
                                .packName(packExamName)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .packTypeId(packTypeId)
                                .build()
                );
            }

            String topicTypeName = ExcelUtil.getStringCellValue(sheetTopic.getRow(3).getCell(1));
            ITopicTypeKeyProjection topicTypeKey = topicTypeRepository.findIdByTypeName(topicTypeName);
            UUID topicTypeId = topicTypeKey != null ? topicTypeKey.getTopicTypeId() : null;
            if(topicTypeKey == null){
                topicTypeId = UUID.randomUUID();
                topicTypeJdbcRepository.insertTopicType(
                        TopicTypeEntity.builder()
                                .topicTypeId(topicTypeId)
                                .topicTypeName(topicTypeName)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .build()
                );
            }

            String topicName = ExcelUtil.getStringCellValue(sheetTopic.getRow(4).getCell(1));
            String topicImage = ExcelUtil.getStringCellValue(sheetTopic.getRow(5).getCell(1));
            String topicDescription = ExcelUtil.getStringCellValue(sheetTopic.getRow(6).getCell(1));
            LocalTime workTime = LocalDateTime.parse(
                    ExcelUtil.getStringCellValue(sheetTopic.getRow(7).getCell(1)),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]")
            ).toLocalTime();

            ITopicKeyProjection topicKey = topicRepository.findTopicIdByName(topicName);

            UUID topicId = topicKey != null ? topicKey.getTopicId() : null;

            // Nếu topic không thuộc pack thì thêm mới
            if(topicId == null || !packId.equals(topicKey.getPackId())){
                topicId = UUID.randomUUID();
                topicJdbcRepository.insertTopic(
                        TopicEntity.builder()
                                .topicId(topicId)
                                .topicName(topicName)
                                .topicImage(topicImage)
                                .topicDescription(topicDescription)
                                .numberQuestion(0)
                                .enable(true)
                                .workTime(workTime)
                                .topicDescription(topicDescription)
                                .packId(packId)
                                .topicTypeId(topicTypeId)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .build()
                );
            }

            return TopicKeyResponse.builder()
                    .topicId(topicId)
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicResponse importAllPartsForTopicExcel(UUID topicId, MultipartFile file) {

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int sheetNumber = 0;

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            if (sheet == null)
                throw new BadRequestException(String.format("Sheet %d does not exist", sheetNumber));

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            List<String> partNamesList = excelTopicContentResponse.getPartNamesList();
            List<String> partTypesList = excelTopicContentResponse.getPartTypesList();

            if (partNamesList == null || partNamesList.isEmpty())
                throw new BadRequestException(String.format("Part name list is not exist or empty in sheet %d", sheetNumber));

            if (partTypesList == null || partTypesList.isEmpty())
                throw new BadRequestException(String.format("Part type list is not exist or empty in sheet %d", sheetNumber));

            if (topicEntity.getParts() == null)
                topicEntity.setParts(new HashSet<>());

            int partNamesSize = partNamesList.size();

            for (int i = 0; i < partNamesSize; i++) {

                String partName = partNamesList.get(i);
                String partType = partTypesList.get(i);

                PartEntity partEntity = partQueryFactory.findPartByNameAndType(partName, partType).orElse(null);

                if (partEntity == null) {

                    partEntity = PartEntity.builder()
                            .partId(UUID.randomUUID())
                            .partName(partName)
                            .partType(partType)
                            .partDescription(String.join(": ", List.of(partName, partType)))
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    partEntity = partRepository.save(partEntity);
                }

                if (!topicEntity.getParts().contains(partEntity))
                    topicEntity.getParts().add(partEntity);
            }

            topicEntity.setUserUpdate(currentUser);

            topicEntity = topicRepository.save(topicEntity);
        }

        return ExcelContentMapper.INSTANCE.toExcelTopicResponse(topicEntity);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicContentResponse readTopicContentFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int sheetNumber = 0;

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            if (sheet == null)
                throw new BadRequestException(String.format("Sheet %d does not exist", sheetNumber));

            return ExcelUtil.collectTopicContentWith(sheet);
        }

    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicResponse importTopicExcel(MultipartFile file) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int sheetNumber = 0;

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", sheetNumber));

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            PackEntity packEntity = packQueryFactory.findPackByName(excelTopicContentResponse.getPackName())
                    .orElse(null);

            if (packEntity == null) {

                packEntity = PackEntity.builder()
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .packName(excelTopicContentResponse.getPackName())
                        .build();

                packEntity = packRepository.save(packEntity);
            }

            TopicEntity topicEntity = topicQueryFactory.findTopicByNameAndPack(excelTopicContentResponse.getTopicName(), packEntity)
                    .orElse(null);

            if (topicEntity == null)
                topicEntity = TopicEntity.builder()
                        .pack(packEntity)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();

            TopicMapper.INSTANCE.flowToTopicEntity(excelTopicContentResponse, topicEntity);

            topicEntity = topicRepository.save(topicEntity);

            if (topicEntity.getParts() == null)
                topicEntity.setParts(new HashSet<>());

            topicEntity.setTopicImage(excelTopicContentResponse.getTopicImage());

            int partNamesSize = excelTopicContentResponse.getPartNamesList().size();

            for (int i = 0; i < partNamesSize; i++) {

                String partNameAtI = excelTopicContentResponse.getPartNamesList().get(i);

                String partTypeAtI = excelTopicContentResponse.getPartTypesList().get(i);

                PartEntity partEntity = partQueryFactory.findPartByNameAndType(partNameAtI, partTypeAtI)
                        .orElse(null);

                if (partEntity == null) {

                    partEntity = PartEntity.builder()
                            .contentData("")
                            .contentType(FileUtil.mimeTypeFile(""))
                            .partName(partNameAtI)
                            .partType(partTypeAtI)
                            .partDescription(String.join(": ", List.of(partNameAtI, partTypeAtI)))
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    partEntity = partRepository.save(partEntity);
                }

                if (!topicEntity.getParts().contains(partEntity))
                    topicEntity.getParts().add(partEntity);
            }

//            if (topicEntity.getTopicName().contains("TOEIC")) {
//                topicEntity.setTopicType("TOEIC");
//            }
//            if (topicEntity.getTopicName().contains("IELTS")) {
//                topicEntity.setTopicType("IELTS");
//            }

            topicEntity = topicRepository.save(topicEntity);

            return ExcelContentMapper.INSTANCE.toExcelTopicResponse(topicEntity);

        }
//        catch (Exception e) {
//            throw new CustomException(ErrorEnum.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
//        }

    }


    @Transactional
    @Override
    @SneakyThrows
    public ExcelPartIdsResponse importAllPartsForTopicFromExcel(UUID topicId, MultipartFile file) {

        Assert.notNull(topicId, "Topic id is required.");

        TopicEntity topic = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Map<String, String> partNamesAndTypes = new HashMap<>();

            int sheetPartsStep = 1;
            int numberOfSheets = workbook.getNumberOfSheets();
            while(sheetPartsStep < numberOfSheets){
                Sheet sheetPart = workbook.getSheetAt(sheetPartsStep);
                String partName = ExcelUtil.getStringCellValue(sheetPart.getRow(0).getCell(0));
                String partType = ExcelUtil.getStringCellValue(sheetPart.getRow(0).getCell(1));
                partNamesAndTypes.put(partName, partType);
                sheetPartsStep++;
            }

            List<String> partNames = partNamesAndTypes.keySet().stream().toList();

            List<String> partNamesOfTopic = partRepository.findPartNamesByTopicIdAndIn(topicId, partNames);

            List<PartEntity> partsTopic = partNames.stream()
                    .filter(partName -> !partNamesOfTopic.contains(partName))
                    .map(partName -> {

                        String partType = partNamesAndTypes.get(partName);
                        return PartEntity.builder()
                                .partId(UUID.randomUUID())
                                .topicId(topicId)
                                .topic(topic)
                                .partType(partType)
                                .partDescription(String.format("%s: %s", partName, partType))
                                .userCreate(currentUser)
                                .userUpdate(currentUser)
                                .build();
                    })
                    .toList();

            partJdbcRepository.batchInsertPart(partsTopic);

            return ExcelPartIdsResponse.builder()
                    .partIds(partRepository.findPartIdsByTopicId(topicId))
                    .build();
        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsResponse importListeningPart12FromExcel(UUID topicId, int part, MultipartFile file) {

        Assert.notNull(topicId, "Topic id is required.");
        Assert.notNull(file, "File excel to import is required.");
        if(part != 1 && part != 2)
            throw new ErrorHolder(Error.BAD_REQUEST, "Part number for import must 1 or 2.");

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            fillListeningPart12FromExcelToDb(topicId, userImport, workbook, part);

            return ExcelTopicPartIdsResponse.builder()
                    .topicId(topicId)
                    .partIds(partRepository.findPartIdsByTopicId(topicId))
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }

    @Transactional
    protected void fillListeningPart12FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part){

        Sheet sheet = workbook.getSheetAt(part);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        List<PartEntity> partsOfTopic = new ArrayList<>();
        List<QuestionEntity> questionsParentOfPart = new ArrayList<>();
        List<QuestionEntity> questionsChildOfParent = new ArrayList<>();
        List<AnswerEntity> answersOfQuestionChild = new ArrayList<>();

        UUID partId = partRepository.findPartIdByTopicIdAndPartName(topicId, partName);
        if(partId == null){
            partId = UUID.randomUUID();
            partsOfTopic.add(
                    PartEntity.builder()
                            .partId(partId)
                            .partName(partName)
                            .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                            .userCreate(userImport)
                            .userUpdate(userImport)
                            .topicId(topicId)
                            .build()
            );
        }

        int countOfQuestionsTopic = topicRepository.countQuestionsByTopicId(topicId);
        int iRowAudio = iRowPart + 1;
        Row rowAudio = sheet.getRow(iRowAudio);
        while (rowAudio != null){

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .partId(partId)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(true)
                    .questionScore(0)
                    .questionType(QuestionType.Question_Parent)
                    .contentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)))
                    .build();
            questionsParentOfPart.add(questionParent);

            // bỏ qua header question
            int nextRow = iRowAudio + 2;
            Row rowQuestionChild = sheet.getRow(nextRow);
            while (rowQuestionChild != null){

                if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).equalsIgnoreCase("audio")){
                    iRowAudio = nextRow;
                    break;
                }

                int jQuestionResult = 1;
                int jQuestionScore = jQuestionResult + 1;
                int jContentImage = jQuestionScore + 1;
                int jContentAudio = jContentImage + 1;

                QuestionEntity questionChild = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .partId(partId)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .isQuestionParent(false)
                        .questionType(QuestionType.Question_Child)
                        .numberChoice(1)
                        .questionGroupParent(questionParent)
                        .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                        .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                        .contentImage(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jContentImage)))
                        .contentAudio(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jContentAudio)))
                        .build();
                questionsChildOfParent.add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
                countOfQuestionsTopic++;

                for(String answerContent : List.of("A", "B", "C", "D")){
                    answersOfQuestionChild.add(
                            AnswerEntity.builder()
                                    .answerId(UUID.randomUUID())
                                    .answerContent(answerContent)
                                    .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                    .userCreate(userImport)
                                    .userUpdate(userImport)
                                    .question(questionChild)
                                    .build()
                    );
                }
                nextRow++;
                rowQuestionChild = sheet.getRow(nextRow);
            }

            rowAudio = rowQuestionChild;
        }

        partJdbcRepository.batchInsertPart(partsOfTopic);
        questionJdbcRepository.batchInsertQuestion(questionsParentOfPart);
        questionJdbcRepository.batchInsertQuestion(questionsChildOfParent);
        answerJdbcRepository.batchInsertAnswer(answersOfQuestionChild);
        topicJdbcRepository.updateTopic(topicId, countOfQuestionsTopic);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsResponse importListeningPart34FromExcel(UUID topicId, int part, MultipartFile file) {

        Assert.notNull(topicId, "Topic id is required.");
        Assert.notNull(file, "File excel to import is required.");
        if(part != 3 && part != 4)
            throw new ErrorHolder(Error.BAD_REQUEST, "Part number for import must 3 or 4.");

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            fillListeningPart34FromExcelToDb(topicId, userImport, workbook, part);

            return ExcelTopicPartIdsResponse.builder()
                    .topicId(topicId)
                    .partIds(partRepository.findPartIdsByTopicId(topicId))
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }

    @Transactional
    protected void fillListeningPart34FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part){

        Sheet sheet = workbook.getSheetAt(part);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        List<PartEntity> partsOfTopic = new ArrayList<>();
        List<QuestionEntity> questionsParentOfPart = new ArrayList<>();
        List<QuestionEntity> questionsChildOfParent = new ArrayList<>();
        List<AnswerEntity> answersOfQuestionChild = new ArrayList<>();

        UUID partId = partRepository.findPartIdByTopicIdAndPartName(topicId, partName);
        if(partId == null){
            partId = UUID.randomUUID();
            partsOfTopic.add(
                    PartEntity.builder()
                            .partId(partId)
                            .partName(partName)
                            .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                            .userCreate(userImport)
                            .userUpdate(userImport)
                            .topicId(topicId)
                            .build()
            );
        }

        int countOfQuestionsTopic = topicRepository.countQuestionsByTopicId(topicId);
        int iRowAudio = iRowPart + 1;
        Row rowAudio = sheet.getRow(iRowAudio);
        while (rowAudio != null){

            int iRowImage = iRowAudio + 1;
            Row rowImage = sheet.getRow(iRowImage);
            if(rowImage == null) break;

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .partId(partId)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(true)
                    .questionScore(0)
                    .questionType(QuestionType.Question_Parent)
                    .contentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)))
                    .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                    .build();
            questionsParentOfPart.add(questionParent);

            // bỏ qua header question
            int nextRow = iRowImage + 2;
            Row rowQuestionChild = sheet.getRow(nextRow);
            while (rowQuestionChild != null){

                if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).equalsIgnoreCase("audio")) {
                    iRowAudio = nextRow;
                    break;
                }

                int jQuestionContent = 1;
                int jStartAnswer = jQuestionContent + 1;
                int jQuestionResult = jStartAnswer + 4;
                int jQuestionScore = jQuestionResult + 1;

                QuestionEntity questionChild = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .partId(partId)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .isQuestionParent(false)
                        .questionType(QuestionType.Question_Child)
                        .numberChoice(1)
                        .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                        .questionGroupParent(questionParent)
                        .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                        .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                        .build();
                questionsChildOfParent.add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
                countOfQuestionsTopic++;

                for(int i = jStartAnswer; i < jQuestionResult; i++){
                    String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                    answersOfQuestionChild.add(
                            AnswerEntity.builder()
                                    .answerId(UUID.randomUUID())
                                    .answerContent(answerContent)
                                    .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                    .userCreate(userImport)
                                    .userUpdate(userImport)
                                    .question(questionChild)
                                    .build()
                    );
                }
                nextRow++;
                rowQuestionChild = sheet.getRow(nextRow);
            }

            rowAudio = rowQuestionChild;
        }

        partJdbcRepository.batchInsertPart(partsOfTopic);
        questionJdbcRepository.batchInsertQuestion(questionsParentOfPart);
        questionJdbcRepository.batchInsertQuestion(questionsChildOfParent);
        answerJdbcRepository.batchInsertAnswer(answersOfQuestionChild);
        topicJdbcRepository.updateTopic(topicId, countOfQuestionsTopic);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionListeningPart12Excel(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (part != 1 && part != 2)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid Part value. It must be either 1 or 2");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 1 or PART 2 !");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_1_TOEIC.getName()) && part == 1
                            || !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_2_TOEIC.getName()) && part == 2
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 1 or PART 2.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity;

            if (partNameAtFirstRow.equalsIgnoreCase(PartType.PART_1_TOEIC.getName()))
                partEntity = partService.getPartToName(PartType.PART_1_TOEIC.getName(), partType, topicEntity);
            else partEntity = partService.getPartToName(PartType.PART_2_TOEIC.getName(), partType, topicEntity);

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowAudioPath = iRowPartName + 1;

            int iRowTotalScore = iRowAudioPath + 1;

            ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, iRowAudioPath, null, iRowTotalScore, part);

            QuestionEntity questionParent = QuestionEntity.builder()
                    .part(partEntity)
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .questionScore(excelQuestionContentResponse.getTotalScore())
                    .isQuestionParent(Boolean.TRUE)
                    .questionType(QuestionType.Question_Parent)
                    .build();

            questionParent = questionRepository.save(questionParent);

            questionParent.setContentAudio(excelQuestionContentResponse.getAudioPath());

            if (questionParent.getQuestionGroupChildren() == null)
                questionParent.setQuestionGroupChildren(new HashSet<>());

            int iRowHeaderTable = iRowTotalScore + 1;

            ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

            // bỏ qua 3 dòng vì đã đọc được audio path, total score và check structure header table
            countRowWillFetch -= 3;

            int totalRowInSheet = sheet.getLastRowNum();

            while (countRowWillFetch >= 0) {

                int iRowBodyTable = totalRowInSheet - countRowWillFetch;

                Row rowBodyTable = sheet.getRow(iRowBodyTable);

                int jColQuestionResult = 1;
                int jColQuestionScore = 2;

                String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionResult);
                int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                QuestionEntity questionChildren = QuestionEntity.builder()
                        .questionGroupParent(questionParent)
                        .part(partEntity)
                        .isQuestionParent(Boolean.FALSE)
                        .questionResult(resultTrueQuestion)
                        .questionType(QuestionType.Single_Choice)
                        .numberChoice(1)
                        .questionScore(scoreTrueQuestion)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();

                if (part == 1) {

                    int jColQuestionImage = 3;

                    String imageQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionImage);

                    questionChildren.setContentImage(imageQuestion);

                }

                questionChildren = questionRepository.save(questionChildren);
                topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                if (questionChildren.getAnswers() == null)
                    questionChildren.setAnswers(new HashSet<>());

                String[] keys = new String[]{
                        "A", "B", "C", "D"
                };

                int keysLength = keys.length;

                for (int i = 0; i < keysLength; i++) {

                    AnswerEntity answer = AnswerEntity.builder()
                            .question(questionChildren)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .answerContent(keys[i])
                            .correctAnswer(keys[i].equalsIgnoreCase(resultTrueQuestion))
                            .build();

                    answer = answerRepository.save(answer);

                    questionChildren.getAnswers().add(answer);

                }

                questionParent.getQuestionGroupChildren().add(questionChildren);

                countRowWillFetch--;

            }

            topicRepository.save(topicEntity);

            ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

            excelQuestionResponse.setTopicId(topicId);

            List<ExcelQuestionResponse> excelQuestionResponseList = List.of(excelQuestionResponse);

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();

        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsResponse importReadingPart5FromExcel(UUID topicId, MultipartFile file) {

        Assert.notNull(topicId, "Topic id is required.");
        Assert.notNull(file, "File excel to import is required.");

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            fillReadingPart5FromExcelToDb(topicId, userImport, workbook);

            return ExcelTopicPartIdsResponse.builder()
                    .topicId(topicId)
                    .partIds(partRepository.findPartIdsByTopicId(topicId))
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }

    @Transactional
    protected void fillReadingPart5FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook){

        Sheet sheet = workbook.getSheetAt(5);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet 5 not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part 5"))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet 5 must be name is Part 5.");

        List<PartEntity> partsOfTopic = new ArrayList<>();
        List<QuestionEntity> questionsParentOfPart = new ArrayList<>();
        List<QuestionEntity> questionsChildOfParent = new ArrayList<>();
        List<AnswerEntity> answersOfQuestionChild = new ArrayList<>();

        UUID partId = partRepository.findPartIdByTopicIdAndPartName(topicId, partName);
        if(partId == null){
            partId = UUID.randomUUID();
            partsOfTopic.add(
                    PartEntity.builder()
                            .partId(partId)
                            .partName(partName)
                            .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                            .userCreate(userImport)
                            .userUpdate(userImport)
                            .topicId(topicId)
                            .build()
            );
        }

        QuestionEntity questionParent = QuestionEntity.builder()
                .questionId(UUID.randomUUID())
                .partId(partId)
                .userCreate(userImport)
                .userUpdate(userImport)
                .isQuestionParent(true)
                .questionScore(0)
                .questionType(QuestionType.Question_Parent)
                .build();
        questionsParentOfPart.add(questionParent);

        int countOfQuestionsTopic = topicRepository.countQuestionsByTopicId(topicId);

        // bỏ qua header question
        int nextRow = iRowPart + 2;
        Row rowQuestionChild = sheet.getRow(nextRow);
        while (rowQuestionChild != null){

            int jQuestionContent = 1;
            int jStartAnswer = jQuestionContent + 1;
            int jQuestionResult = jStartAnswer + 4;
            int jQuestionScore = jQuestionResult + 1;

            QuestionEntity questionChild = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .partId(partId)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(false)
                    .questionType(QuestionType.Question_Child)
                    .numberChoice(1)
                    .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                    .questionGroupParent(questionParent)
                    .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                    .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                    .build();
            questionsChildOfParent.add(questionChild);
            questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
            countOfQuestionsTopic++;

            for(int i = jStartAnswer; i < jQuestionResult; i++){
                String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                answersOfQuestionChild.add(
                        AnswerEntity.builder()
                                .answerId(UUID.randomUUID())
                                .answerContent(answerContent)
                                .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .question(questionChild)
                                .build()
                );
            }
            nextRow++;
            rowQuestionChild = sheet.getRow(nextRow);
        }

        partJdbcRepository.batchInsertPart(partsOfTopic);
        questionJdbcRepository.batchInsertQuestion(questionsParentOfPart);
        questionJdbcRepository.batchInsertQuestion(questionsChildOfParent);
        answerJdbcRepository.batchInsertAnswer(answersOfQuestionChild);
        topicJdbcRepository.updateTopic(topicId, countOfQuestionsTopic);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionReadingPart5Excel(UUID topicId, MultipartFile file) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        int part = 5;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 5!");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_5_TOEIC.getName())
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 5.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity = partService.getPartToName(PartType.PART_5_TOEIC.getName(), partType, topicEntity);

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowTotalScore = iRowPartName + 1;

            ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, null, null, iRowTotalScore, part);

            QuestionEntity questionParent = QuestionEntity.builder()
                    .part(partEntity)
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .questionScore(excelQuestionContentResponse.getTotalScore())
                    .isQuestionParent(Boolean.TRUE)
                    .questionType(QuestionType.Question_Parent)
                    .build();

            questionParent = questionRepository.save(questionParent);

            if (questionParent.getQuestionGroupChildren() == null)
                questionParent.setQuestionGroupChildren(new HashSet<>());

            int iRowHeaderTable = iRowTotalScore + 1;

            ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

            // bỏ qua 2 dòng vì đã đọc được total score và check structure header table
            countRowWillFetch -= 2;

            int totalRowInSheet = sheet.getLastRowNum();

            while (countRowWillFetch >= 0) {

                int iRowBodyTable = totalRowInSheet - countRowWillFetch;

                Row rowBodyTable = sheet.getRow(iRowBodyTable);

                int jColQuestionContent = 1;
                int jColResultTrueAnswer = 6;
                int jColQuestionScore = 7;

                String questionContent = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionContent);
                String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                QuestionEntity questionChildren = QuestionEntity.builder()
                        .questionContent(questionContent)
                        .part(partEntity)
                        .questionGroupParent(questionParent)
                        .isQuestionParent(Boolean.FALSE)
                        .questionType(QuestionType.Single_Choice)
                        .numberChoice(1)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .questionScore(scoreTrueQuestion)
                        .questionResult(resultTrueQuestion)
                        .build();

                questionChildren = questionRepository.save(questionChildren);
                topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                if (questionChildren.getAnswers() == null)
                    questionChildren.setAnswers(new HashSet<>());

                int jA_Begin = 2;
                int jD_Last = 5;

                for (int j = jA_Begin; j <= jD_Last; j++) {

                    String answerContent = ExcelUtil.getStringCellValue(rowBodyTable, j);

                    AnswerEntity answerEntity = AnswerEntity.builder()
                            .answerContent(answerContent)
                            .correctAnswer(answerContent.equalsIgnoreCase(resultTrueQuestion))
                            .question(questionChildren)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    answerEntity = answerRepository.save(answerEntity);

                    questionChildren.getAnswers().add(answerEntity);
                }

                questionParent.getQuestionGroupChildren().add(questionChildren);

                countRowWillFetch--;

            }

            topicRepository.save(topicEntity);

            ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

            excelQuestionResponse.setTopicId(topicId);

            List<ExcelQuestionResponse> excelQuestionResponseList = List.of(excelQuestionResponse);

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();
        }
//        catch (Exception e) {
//            throw new CustomException(ErrorEnum.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
//        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionListeningPart34Excel(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (part != 3 && part != 4)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid Part value. It must be either 3 or 4");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 3 or PART 4!");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_3_TOEIC.getName()) && part == 3
                            || !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_4_TOEIC.getName()) && part == 4
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 3 or PART 4.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity;

            if (partNameAtFirstRow.equalsIgnoreCase(PartType.PART_3_TOEIC.getName()))
                partEntity = partService.getPartToName(PartType.PART_3_TOEIC.getName(), partType, topicEntity);
            else partEntity = partService.getPartToName(PartType.PART_4_TOEIC.getName(), partType, topicEntity);

            List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowAudioPath = iRowPartName + 1;

            while (countRowWillFetch >= 0) {

                int iRowImage = iRowAudioPath + 1;
                int iRowTotalScore = iRowImage + 1;

                ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, iRowAudioPath, iRowImage, iRowTotalScore, part);

                QuestionEntity questionParent = QuestionEntity.builder()
                        .part(partEntity)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .questionScore(excelQuestionContentResponse.getTotalScore())
                        .isQuestionParent(Boolean.TRUE)
                        .questionType(QuestionType.Question_Parent)
                        .build();

                questionParent = questionRepository.save(questionParent);


                questionParent.setContentAudio(excelQuestionContentResponse.getAudioPath());

                questionParent.setContentImage(excelQuestionContentResponse.getImagePath());

                if (questionParent.getQuestionGroupChildren() == null)
                    questionParent.setQuestionGroupChildren(new HashSet<>());

                int iRowHeaderTable = iRowTotalScore + 1;

                ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

                int iRowBodyTable = iRowHeaderTable + 1;

                //bỏ qua 4 dòng vị đã dọc audio, image, score, và check structure header table
                countRowWillFetch -= 4;

                while (true) {

                    Row rowBodyTable = sheet.getRow(iRowBodyTable);

                    if (rowBodyTable == null || countRowWillFetch < 0) {
                        countRowWillFetch = -1;
                        break;
                    }

                    Cell cellQuestionContent = rowBodyTable.getCell(0);

                    if (
                            cellQuestionContent.getCellType().equals(CellType.STRING)
                                    && cellQuestionContent.getStringCellValue().equalsIgnoreCase(ExcelQuestionConstant.Audio.getHeaderName())
                    ) {

                        iRowAudioPath = iRowBodyTable;
                        break;
                    }

                    int jColQuestionContent = 1;
                    int jColResultTrueAnswer = 6;
                    int jColQuestionScore = 7;

                    String questionContent = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionContent);
                    String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                    int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                    QuestionEntity questionChildren = QuestionEntity.builder()
                            .questionContent(questionContent)
                            .part(partEntity)
                            .questionGroupParent(questionParent)
                            .isQuestionParent(Boolean.FALSE)
                            .questionType(QuestionType.Single_Choice)
                            .numberChoice(1)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .questionScore(scoreTrueQuestion)
                            .questionResult(resultTrueQuestion)
                            .build();

                    questionChildren = questionRepository.save(questionChildren);
                    topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                    if (questionChildren.getAnswers() == null)
                        questionChildren.setAnswers(new HashSet<>());

                    int jA_Begin = 2;
                    int jD_Last = 5;

                    for (int j = jA_Begin; j <= jD_Last; j++) {

                        String answerContent = ExcelUtil.getStringCellValue(rowBodyTable, j);

                        AnswerEntity answerEntity = AnswerEntity.builder()
                                .answerContent(answerContent)
                                .correctAnswer(answerContent.equalsIgnoreCase(resultTrueQuestion))
                                .question(questionChildren)
                                .userCreate(currentUser)
                                .userUpdate(currentUser)
                                .build();

                        answerEntity = answerRepository.save(answerEntity);

                        questionChildren.getAnswers().add(answerEntity);
                    }

                    questionParent.getQuestionGroupChildren().add(questionChildren);

                    countRowWillFetch--;

                    iRowBodyTable++;
                }

                topicRepository.save(topicEntity);

                ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

                excelQuestionResponse.setTopicId(topicId);

                excelQuestionResponseList.add(excelQuestionResponse);
            }

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();

        }
//        catch (Exception e) {
//
//            throw new CustomException(part == 3 ? ErrorEnum.CAN_NOT_CREATE_PART_3_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_4_BY_EXCEL);
//        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsResponse importReadingPart67FromExcel(UUID topicId, int part, MultipartFile file) {

        Assert.notNull(topicId, "Topic id is required.");
        Assert.notNull(file, "File excel to import is required.");
        if(part != 6 && part != 7)
            throw new ErrorHolder(Error.BAD_REQUEST, "Part number for import must 6 or 7.");

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            fillReadingPart67FromExcelToDb(topicId, userImport, workbook, part);

            return ExcelTopicPartIdsResponse.builder()
                    .topicId(topicId)
                    .partIds(partRepository.findPartIdsByTopicId(topicId))
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }

    @Transactional
    protected void fillReadingPart67FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part){

        Sheet sheet = workbook.getSheetAt(part);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        List<PartEntity> partsOfTopic = new ArrayList<>();
        List<QuestionEntity> questionsParentOfPart = new ArrayList<>();
        List<QuestionEntity> questionsChildOfParent = new ArrayList<>();
        List<AnswerEntity> answersOfQuestionChild = new ArrayList<>();

        UUID partId = partRepository.findPartIdByTopicIdAndPartName(topicId, partName);
        if(partId == null){
            partId = UUID.randomUUID();
            partsOfTopic.add(
                    PartEntity.builder()
                            .partId(partId)
                            .partName(partName)
                            .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                            .userCreate(userImport)
                            .userUpdate(userImport)
                            .topicId(topicId)
                            .build()
            );
        }

        int countOfQuestionsTopic = topicRepository.countQuestionsByTopicId(topicId);
        int iRowParentContent = iRowPart + 1;
        Row rowParentContent = sheet.getRow(iRowParentContent);
        while (rowParentContent != null){

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .partId(partId)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(true)
                    .questionContent(ExcelUtil.getStringCellValue(rowParentContent.getCell(1)))
                    .questionScore(0)
                    .questionType(QuestionType.Question_Parent)
                    .build();
            if(part == 7) {
                questionParent.setContentImage(ExcelUtil.getStringCellValue(sheet.getRow(iRowParentContent + 1).getCell(1)));
            }
            questionsParentOfPart.add(questionParent);

            // bỏ qua header question
            int nextRow = part == 6 ? iRowParentContent + 2 : iRowParentContent + 3;
            Row rowQuestionChild = sheet.getRow(nextRow);
            while (rowQuestionChild != null){

                if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).toLowerCase().contains("question")){
                    iRowParentContent = nextRow;
                    break;
                }

                int jQuestionContent = 1;
                int jStartAnswer = jQuestionContent + 1;
                int jQuestionResult = jStartAnswer + 4;
                int jQuestionScore = jQuestionResult + 1;

                QuestionEntity questionChild = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .partId(partId)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .isQuestionParent(false)
                        .questionType(QuestionType.Question_Child)
                        .numberChoice(1)
                        .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                        .questionGroupParent(questionParent)
                        .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                        .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                        .build();
                questionsChildOfParent.add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
                countOfQuestionsTopic++;

                for(int i = jStartAnswer; i < jQuestionResult; i++){
                    String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                    answersOfQuestionChild.add(
                            AnswerEntity.builder()
                                    .answerId(UUID.randomUUID())
                                    .answerContent(answerContent)
                                    .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                    .userCreate(userImport)
                                    .userUpdate(userImport)
                                    .question(questionChild)
                                    .build()
                    );
                }
                nextRow++;
                rowQuestionChild = sheet.getRow(nextRow);
            }

            rowParentContent = rowQuestionChild;
        }

        partJdbcRepository.batchInsertPart(partsOfTopic);
        questionJdbcRepository.batchInsertQuestion(questionsParentOfPart);
        questionJdbcRepository.batchInsertQuestion(questionsChildOfParent);
        answerJdbcRepository.batchInsertAnswer(answersOfQuestionChild);
        topicJdbcRepository.updateTopic(topicId, countOfQuestionsTopic);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionReadingPart67Excel(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (part != 6 && part != 7)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid Part value. It must be either 6 or 7");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 6 or PART 7!");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_6_TOEIC.getName()) && part == 6
                            || !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_7_TOEIC.getName()) && part == 7
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 6 or PART 7.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity;

            if (partNameAtFirstRow.equalsIgnoreCase(PartType.PART_6_TOEIC.getName()))
                partEntity = partService.getPartToName(PartType.PART_6_TOEIC.getName(), partType, topicEntity);
            else partEntity = partService.getPartToName(PartType.PART_7_TOEIC.getName(), partType, topicEntity);

            List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowQuestionContent = iRowPartName + 1;

            while (countRowWillFetch >= 0) {

                Integer iRowImage = part == 7 ? iRowQuestionContent + 1 : null;
                int iRowTotalScore = part == 7 ? iRowImage + 1 : iRowQuestionContent + 1;

                ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, iRowQuestionContent, iRowImage, iRowTotalScore, part);

                QuestionEntity questionParent = QuestionEntity.builder()
                        .part(partEntity)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .questionScore(excelQuestionContentResponse.getTotalScore())
                        .isQuestionParent(Boolean.TRUE)
                        .questionType(QuestionType.Question_Parent)
                        .questionContent(excelQuestionContentResponse.getQuestionContent())
                        .build();

                questionParent = questionRepository.save(questionParent);

                if (part == 7) {

                    questionParent.setContentImage(excelQuestionContentResponse.getImagePath());
                }

                if (questionParent.getQuestionGroupChildren() == null)
                    questionParent.setQuestionGroupChildren(new HashSet<>());

                int iRowHeaderTable = iRowTotalScore + 1;

                ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

                int iRowBodyTable = iRowHeaderTable + 1;

                //bỏ qua 2 dòng nếu là part 6 hay 3 dòng nếu là part 7 vị đã dọc question content, image, score, và check structure header table
                countRowWillFetch -= part == 7 ? 3 : 2;

                while (true) {

                    Row rowBodyTable = sheet.getRow(iRowBodyTable);

                    if (rowBodyTable == null || countRowWillFetch < 0) {
                        countRowWillFetch = -1;
                        break;
                    }

                    Cell cellQuestionContent = rowBodyTable.getCell(0);

                    if (
                            cellQuestionContent.getCellType().equals(CellType.STRING)
                                    && cellQuestionContent.getStringCellValue().equalsIgnoreCase(ExcelQuestionConstant.Question_Content.getHeaderName())
                    ) {

                        iRowQuestionContent = iRowBodyTable;
                        break;
                    }

                    int jColQuestionContentChild = 1;
                    int jColResultTrueAnswer = 6;
                    int jColQuestionScore = 7;

                    String questionContentChild = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionContentChild);
                    String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                    int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                    QuestionEntity questionChildren = QuestionEntity.builder()
                            .questionContent(questionContentChild)
                            .part(partEntity)
                            .questionGroupParent(questionParent)
                            .isQuestionParent(Boolean.FALSE)
                            .questionType(QuestionType.Single_Choice)
                            .numberChoice(1)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .questionScore(scoreTrueQuestion)
                            .questionResult(resultTrueQuestion)
                            .build();

                    questionChildren = questionRepository.save(questionChildren);
                    topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                    if (questionChildren.getAnswers() == null)
                        questionChildren.setAnswers(new HashSet<>());

                    int jA_Begin = 2;
                    int jD_Last = 5;

                    for (int j = jA_Begin; j <= jD_Last; j++) {

                        String answerContent = ExcelUtil.getStringCellValue(rowBodyTable, j);

                        AnswerEntity answerEntity = AnswerEntity.builder()
                                .answerContent(answerContent)
                                .correctAnswer(answerContent.equalsIgnoreCase(resultTrueQuestion))
                                .question(questionChildren)
                                .userCreate(currentUser)
                                .userUpdate(currentUser)
                                .build();

                        answerEntity = answerRepository.save(answerEntity);

                        questionChildren.getAnswers().add(answerEntity);
                    }

                    questionParent.getQuestionGroupChildren().add(questionChildren);

                    countRowWillFetch--;

                    iRowBodyTable++;
                }

                topicRepository.save(topicEntity);

                ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

                excelQuestionResponse.setTopicId(topicId);

                excelQuestionResponseList.add(excelQuestionResponse);
            }

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();
        }
//        catch (Exception e) {
//
//            throw new CustomException(part == 6 ? ErrorEnum.CAN_NOT_CREATE_PART_6_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_7_BY_EXCEL);
//        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public List<ExcelQuestionResponse> importQuestionForTopicAndPart(UUID topicId, int partNumber, MultipartFile file) {

        ExcelUtil.checkPartInScope(partNumber);

        List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

        if (List.of(1, 2).contains(partNumber)) {

            ExcelQuestionListResponse excelQuestionListResponse = importQuestionListeningPart12Excel(topicId, file, partNumber);

            excelQuestionResponseList = excelQuestionListResponse.getQuestions();
        } else if (List.of(3, 4).contains(partNumber)) {

            ExcelQuestionListResponse excelQuestionListResponse = importQuestionListeningPart34Excel(topicId, file, partNumber);

            excelQuestionResponseList = excelQuestionListResponse.getQuestions();
        } else if (partNumber == 5) {

            ExcelQuestionListResponse excelQuestionP5ListResponse = importQuestionReadingPart5Excel(topicId, file);

            excelQuestionResponseList = excelQuestionP5ListResponse.getQuestions();
        } else if (List.of(6, 7).contains(partNumber)) {

            ExcelQuestionListResponse excelQuestionListResponse = importQuestionReadingPart67Excel(topicId, file, partNumber);

            excelQuestionResponseList = excelQuestionListResponse.getQuestions();
        }

        return excelQuestionResponseList;
    }

    @Transactional
    @SneakyThrows
    protected void fillQuestionForTopicAnyPartFromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part) {

        if (part == 1 || part == 2)
            fillListeningPart12FromExcelToDb(topicId, userImport, workbook, part);
        else if (part == 3 || part == 4)
            fillListeningPart34FromExcelToDb(topicId, userImport, workbook, part);
        else if (part == 5)
            fillReadingPart5FromExcelToDb(topicId, userImport, workbook);
        else if (part == 6 || part == 7)
            fillReadingPart67FromExcelToDb(topicId, userImport, workbook, part);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsResponse importQuestionForTopicAnyPartFromExcel(UUID topicId, int part, MultipartFile file) {

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            fillQuestionForTopicAnyPartFromExcelToDb(topicId, userImport, workbook, part);
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }

        return ExcelTopicPartIdsResponse.builder()
                .topicId(topicId)
                .partIds(partRepository.findPartIdsByTopicId(topicId))
                .build();
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsResponse importQuestionAtAllPartForTopicFromExcel(UUID topicId, MultipartFile file, ImportExcelType typeImport) {

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            int numberOfSheets = workbook.getNumberOfSheets();

            if(typeImport.equals(ImportExcelType.FUNNY_TEST)){
                fillTopicPartsQuestionsAnswersFunnyTestToDb(topicId, userImport, workbook, file);
            }
            else if(typeImport.equals(ImportExcelType.SPEAKING)){
                fillSpeakingPartsToDb(topicId, userImport, workbook);
            }
            else{
                for(int part = 1; part < numberOfSheets; part++)
                    fillQuestionForTopicAnyPartFromExcelToDb(topicId, userImport, workbook, part);
            }
        }

        return ExcelTopicPartIdsResponse.builder()
                .topicId(topicId)
                .partIds(partRepository.findPartIdsByTopicId(topicId))
                .build();
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionAllPartsExcel(UUID topicId, MultipartFile file) {

        ExcelTopicContentResponse excelTopicContentResponse;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);
        }

        List<ExcelQuestionResponse> excelQuestionResponseList = excelTopicContentResponse.getPartNamesList().stream()
                .map(
                        partName -> Integer.valueOf(partName.split(" ")[1])
                )
                .flatMap(
                        partNumber -> importQuestionForTopicAndPart(topicId, partNumber, file).stream()
                ).toList();

        return ExcelQuestionListResponse.builder()
                .questions(excelQuestionResponseList)
                .build();
    }

    @Transactional
    @SneakyThrows
    protected void fillTopicPartsQuestionsAnswersFunnyTestToDb(UUID topicId, UserEntity userImport, Workbook workbook, MultipartFile file){

        int numberOfSheet = workbook.getNumberOfSheets();

        List<PartEntity> partsOfTopic = new ArrayList<>();
        List<QuestionEntity> questionsParentOfPart = new ArrayList<>();
        List<QuestionEntity> questionsChildOfParent = new ArrayList<>();
        List<AnswerEntity> answersOfQuestionChild = new ArrayList<>();

        int countOfQuestionsTopic = 0;

        int sheetPartStep = 1;
        // Thêm part, question, answer cho topic
        while(sheetPartStep < numberOfSheet){

            Sheet sheetPart = workbook.getSheetAt(sheetPartStep);

            int iRowPart = 0;
            Row rowPart = sheetPart.getRow(iRowPart);
            if(rowPart == null) break;

            String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));
            UUID partId = partRepository.findPartIdByTopicIdAndPartName(topicId, partName);
            if(partId == null){
                partId = UUID.randomUUID();
                partsOfTopic.add(
                        PartEntity.builder()
                                .partId(partId)
                                .partName(ExcelUtil.getStringCellValue(rowPart.getCell(0)))
                                .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .topicId(topicId)
                                .build()
                );
            }

            int iRowAudioQuestionParent = iRowPart + 1;

            Row rowAudio = sheetPart.getRow(iRowAudioQuestionParent);
            while (rowAudio != null){

                int iRowImageQuestionParent = iRowAudioQuestionParent + 1;
                Row rowImage = sheetPart.getRow(iRowImageQuestionParent);
                if(rowImage == null) break;

                QuestionEntity questionParent = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .partId(partId)
                        .isQuestionParent(true)
                        .questionType(QuestionType.Question_Parent)
                        .questionScore(0)
                        .contentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)))
                        .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .build();
                questionsParentOfPart.add(questionParent);

                // Bỏ qua header table
                int nextRow = iRowImageQuestionParent + 2;
                Row rowQuestionChild = sheetPart.getRow(nextRow);
                while (rowQuestionChild != null){

                    if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).equalsIgnoreCase("audio"))
                        break;

                    int jQuestionContent = 1;
                    int jStartAnswer = jQuestionContent + 1;
                    int jQuestionResult = jStartAnswer + 4;
                    int jAudioQuestionChild = jQuestionResult + 1;
                    int jImageQuestionChild = jAudioQuestionChild + 1;
                    int jScoreQuestionChild = jImageQuestionChild + 1;

                    QuestionEntity questionChild = QuestionEntity.builder()
                            .questionId(UUID.randomUUID())
                            .partId(partId)
                            .questionGroupParent(questionParent)
                            .questionType(QuestionType.Question_Child)
                            .isQuestionParent(false)
                            .numberChoice(1)
                            .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                            .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                            .contentAudio(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jAudioQuestionChild)))
                            .contentImage(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jImageQuestionChild)))
                            .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jScoreQuestionChild)))
                            .userCreate(userImport)
                            .userUpdate(userImport)
                            .build();
                    questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
                    questionsChildOfParent.add(questionChild);
                    countOfQuestionsTopic++;

                    for(int i = jStartAnswer; i < jQuestionResult; i++){
                        String answerChildContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                        answersOfQuestionChild.add(
                                AnswerEntity.builder()
                                        .answerId(UUID.randomUUID())
                                        .question(questionChild)
                                        .answerContent(answerChildContent)
                                        .correctAnswer(answerChildContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                        .userCreate(userImport)
                                        .userUpdate(userImport)
                                        .build()
                        );
                    }
                    nextRow++;
                    rowQuestionChild = sheetPart.getRow(nextRow);
                }

                rowAudio = sheetPart.getRow(nextRow);
                iRowAudioQuestionParent = nextRow;
            }

            sheetPartStep++;
        }

        partJdbcRepository.batchInsertPart(partsOfTopic);
        questionJdbcRepository.batchInsertQuestion(questionsParentOfPart);
        questionJdbcRepository.batchInsertQuestion(questionsChildOfParent);
        answerJdbcRepository.batchInsertAnswer(answersOfQuestionChild);
        topicJdbcRepository.updateTopic(topicId, countOfQuestionsTopic);
    }

    @Transactional
    @Override
    public TopicKeyResponse importTopicPartsQuestionsAnswersFunnyTest(MultipartFile file) {

        Assert.notNull(file, "File is required.");

        UserEntity userImport = userService.currentUser();

        TopicKeyResponse topicKey = importTopicFromExcel(file);

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            fillTopicPartsQuestionsAnswersFunnyTestToDb(topicKey.getTopicId(), userImport, workbook, file);

            return TopicKeyResponse.builder()
                    .topicId(topicKey.getTopicId())
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }

    @Transactional
    protected void fillSpeakingPartsToDb(UUID topicId, UserEntity userImport, Workbook workbook){

        List<PartEntity> partsOfTopic = new ArrayList<>();
        List<QuestionEntity> questionsParentOfPart = new ArrayList<>();
        List<QuestionEntity> questionsChildOfParent = new ArrayList<>();

        int numberOfSheets = workbook.getNumberOfSheets();
        int sheetPartStep = 1;
        while(sheetPartStep < numberOfSheets){
            Sheet sheet = workbook.getSheetAt(sheetPartStep);
            int iRowPart = 0;
            Row rowPart = sheet.getRow(iRowPart);
            if(rowPart == null) break;

            String partName = ExcelUtil.getStringCellValue(sheet.getRow(0).getCell(0));
            UUID partId = partRepository.findPartIdByTopicIdAndPartName(topicId, partName);
            if(partId == null){
                partId = UUID.randomUUID();
                partsOfTopic.add(
                        PartEntity.builder()
                                .partId(partId)
                                .partName(ExcelUtil.getStringCellValue(rowPart.getCell(0)))
                                .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .topicId(topicId)
                                .build()
                );
            }

            int iRowImage = iRowPart + 1;
            Row rowImage = sheet.getRow(iRowImage);
            int orderParent = 1;
            while (rowImage != null){

                int iRowTitle = iRowImage + 1;
                int iRowDuration = iRowTitle + 1;

                QuestionEntity questionParent = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .questionContent(ExcelUtil.getStringCellValue(sheet.getRow(iRowTitle).getCell(1)))
                        .durationRecord(
                                LocalDateTime.parse(
                                        ExcelUtil.getStringCellValue(sheet.getRow(iRowDuration).getCell(1)),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]")
                                ).toLocalTime()
                        )
                        .partId(partId)
                        .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                        .questionType(QuestionType.Question_Parent)
                        .isQuestionParent(true)
                        .questionScore(orderParent)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .build();
                questionsParentOfPart.add(questionParent);

                // bỏ qua header question
                int nextRow = iRowDuration + 2;
                Row rowQuestionChild = sheet.getRow(nextRow);
                int orderChild = 1;
                while (rowQuestionChild != null){

                    if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).equalsIgnoreCase("image")){
                        iRowImage = nextRow;
                        break;
                    }

                    QuestionEntity questionChild = QuestionEntity.builder()
                            .questionId(UUID.randomUUID())
                            .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(1)))
                            .partId(partId)
                            .questionGroupParent(questionParent)
                            .isQuestionParent(false)
                            .numberChoice(0)
                            .questionType(QuestionType.Question_Child)
                            .questionScore(orderChild)
                            .userCreate(userImport)
                            .userUpdate(userImport)
                            .build();
                    questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
                    questionsChildOfParent.add(questionChild);

                    nextRow++;
                    orderChild++;
                    rowQuestionChild = sheet.getRow(nextRow);
                }
                rowImage = rowQuestionChild;
                orderParent++;
            }
            sheetPartStep++;
        }

        partJdbcRepository.batchInsertPart(partsOfTopic);
        questionJdbcRepository.batchInsertQuestion(questionsParentOfPart);
        questionJdbcRepository.batchInsertQuestion(questionsChildOfParent);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsResponse importSpeakingAllPartsForTopicFromExcel(UUID topicId, MultipartFile file) {

        Assert.notNull(topicId, "Topic id is required.");

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            fillSpeakingPartsToDb(topicId, userImport, workbook);

            return ExcelTopicPartIdsResponse.builder()
                    .topicId(topicId)
                    .partIds(partRepository.findPartIdsByTopicId(topicId))
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }
}
