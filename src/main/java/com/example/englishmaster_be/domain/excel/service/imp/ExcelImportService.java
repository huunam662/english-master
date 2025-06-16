package com.example.englishmaster_be.domain.excel.service.imp;

import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.excel.dto.response.*;
import com.example.englishmaster_be.domain.pack.dto.IPackKeyProjection;
import com.example.englishmaster_be.domain.pack_type.dto.projection.IPackTypeKeyProjection;
import com.example.englishmaster_be.domain.topic.dto.projection.ITopicField1Projection;
import com.example.englishmaster_be.domain.topic.dto.projection.ITopicKeyProjection;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyResponse;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.topic_type.dto.response.ITopicTypeKeyProjection;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.topic_type.repository.jdbc.TopicTypeJdbcRepository;
import com.example.englishmaster_be.domain.topic_type.repository.jpa.ITopicTypeRepository;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
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
import com.example.englishmaster_be.domain.excel.util.ExcelUtil;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.topic.repository.factory.TopicQueryFactory;
import com.example.englishmaster_be.domain.topic.repository.jpa.TopicRepository;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
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
public class ExcelImportService implements IExcelImportService {


    PartRepository partRepository;

    TopicRepository topicRepository;

    PackRepository packRepository;

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
            String time = ExcelUtil.getStringCellValue(sheetTopic.getRow(7).getCell(1));
            LocalTime workTime = LocalTime.parse(
                    ExcelUtil.getStringCellValue(sheetTopic.getRow(7).getCell(1)),
                    DateTimeFormatter.ofPattern("HH:mm[:ss]")
            );

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
    public ExcelTopicPartIdsResponse importQuestionAtAllPartForTopicFromExcel(UUID topicId, MultipartFile file) {

        UserEntity userImport = userService.currentUser();

        ITopicField1Projection topicField = topicRepository.findTopicTypeById(topicId);

        if(topicField == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Topic not found.");

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            int numberOfSheets = workbook.getNumberOfSheets();

            if(topicField.getTopicType().equalsIgnoreCase(TopicType.READING.getType())){
                fillTopicPartsQuestionsAnswersReadingToDb(topicId, userImport, workbook, file);
            }
            else if(topicField.getTopicType().equalsIgnoreCase(TopicType.SPEAKING.getType())){
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
    @SneakyThrows
    protected void fillTopicPartsQuestionsAnswersReadingToDb(UUID topicId, UserEntity userImport, Workbook workbook, MultipartFile file){

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
                    int jScoreQuestionChild = jQuestionResult + 1;
                    int jImageQuestionChild = jScoreQuestionChild + 1;

                    QuestionEntity questionChild = QuestionEntity.builder()
                            .questionId(UUID.randomUUID())
                            .partId(partId)
                            .questionGroupParent(questionParent)
                            .questionType(QuestionType.Question_Child)
                            .isQuestionParent(false)
                            .numberChoice(1)
                            .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                            .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
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
    public TopicKeyResponse importTopicPartsQuestionsAnswersReading(MultipartFile file) {

        Assert.notNull(file, "File is required.");

        UserEntity userImport = userService.currentUser();

        TopicKeyResponse topicKey = importTopicFromExcel(file);

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            fillTopicPartsQuestionsAnswersReadingToDb(topicKey.getTopicId(), userImport, workbook, file);

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

        int numberOfSheets = workbook.getNumberOfSheets();
        int sheetPartStep = 1;
        int numberOfQuestions = 0;
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
            while (rowImage != null){

                int iRowQuestionSpeaking = iRowImage + 1;

                QuestionEntity questionParent = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .questionContent(ExcelUtil.getStringCellValue(sheet.getRow(iRowQuestionSpeaking).getCell(1)))
                        .partId(partId)
                        .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                        .questionType(QuestionType.Speaking)
                        .isQuestionParent(true)
                        .questionScore(0)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .build();
                questionsParentOfPart.add(questionParent);
                numberOfQuestions++;
                rowImage = sheet.getRow(iRowQuestionSpeaking + 1);
            }
            sheetPartStep++;
        }

        topicJdbcRepository.updateTopic(topicId, numberOfQuestions);
        partJdbcRepository.batchInsertPart(partsOfTopic);
        questionJdbcRepository.batchInsertQuestion(questionsParentOfPart);
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
