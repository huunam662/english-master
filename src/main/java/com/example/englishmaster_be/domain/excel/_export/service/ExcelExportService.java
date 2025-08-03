package com.example.englishmaster_be.domain.excel._export.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.answer.repository.AnswerRepository;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.question.repository.QuestionRepository;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.repository.TopicRepository;
import com.example.englishmaster_be.domain.exam.topic.topic.util.TopicUtil;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j(topic = "EXCEL-EXPORT-SERVICE")
@Service
public class ExcelExportService implements IExcelExportService{

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;

    public ExcelExportService(AnswerRepository answerRepository, QuestionRepository questionRepository, TopicRepository topicRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    public ByteArrayInputStream exportTopicById(UUID topicId) {

        TopicEntity topic = topicRepository.findAllTopicWithJoinParent(topicId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Topic not found."));

        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(zipOut)){
            TopicTypeEntity topicType = topic.getTopicType();
            if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.READING_LISTENING.getType())){
                exportReadingAndListeningInformation(List.of(topic), zipOutputStream);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.READING.getType())){
                exportReadingInformation(List.of(topic), zipOutputStream);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.SPEAKING.getType())){
                exportSpeakingInformation(List.of(topic), zipOutputStream);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.WRITING.getType())){
                exportWritingInformation(List.of(topic), zipOutputStream);
            }
        }
        catch (IOException e){
            throw new ApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return new ByteArrayInputStream(zipOut.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportAllTopic() {

        int pageTopic = 0;
        int pageTopicSize = 10;
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(zipOut)){
            List<TopicEntity> topicsResult = topicRepository.findAllTopicWithJoinParent(PageRequest.of(pageTopic, pageTopicSize));
            while (!topicsResult.isEmpty()){
                Map<TopicType, List<TopicEntity>> typeTopicGroup = topicsResult.stream().collect(
                        Collectors.groupingBy(topic -> TopicType.fromType(topic.getTopicType().getTopicTypeName()))
                );
                for(TopicType topicType : TopicType.values()){
                    List<TopicEntity> topicsOfType = typeTopicGroup.getOrDefault(topicType, new ArrayList<>());
                    if(topicType.getType().equalsIgnoreCase(TopicType.READING_LISTENING.getType())){
                        exportReadingAndListeningInformation(topicsOfType, zipOutputStream);
                    }
                    else if(topicType.getType().equalsIgnoreCase(TopicType.READING.getType())){
                        exportReadingInformation(topicsOfType, zipOutputStream);
                    }
                    else if(topicType.getType().equalsIgnoreCase(TopicType.SPEAKING.getType())){
                        exportSpeakingInformation(topicsOfType, zipOutputStream);
                    }
                    else if(topicType.getType().equalsIgnoreCase(TopicType.WRITING.getType())){
                        exportWritingInformation(topicsOfType, zipOutputStream);
                    }
                }
                topicsResult = topicRepository.findAllTopicWithJoinParent(PageRequest.of(++pageTopic, pageTopicSize));
            }
        }
        catch (IOException e){
            throw new ApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return new ByteArrayInputStream(zipOut.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportTopicByType(TopicType topicType){

        int pageTopic = 0;
        int pageTopicSize = 10;
        List<TopicEntity> topicsResult = topicRepository.findAllTopicWithJoinParent(topicType.getType(), PageRequest.of(pageTopic, pageTopicSize));
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(zipOut)){
            while (!topicsResult.isEmpty()){
                if(topicType.getType().equalsIgnoreCase(TopicType.READING_LISTENING.getType())){
                    exportReadingAndListeningInformation(topicsResult, zipOutputStream);
                }
                else if(topicType.getType().equalsIgnoreCase(TopicType.READING.getType())){
                    exportReadingInformation(topicsResult, zipOutputStream);
                }
                else if(topicType.getType().equalsIgnoreCase(TopicType.SPEAKING.getType())){
                    exportSpeakingInformation(topicsResult, zipOutputStream);
                }
                else if(topicType.getType().equalsIgnoreCase(TopicType.WRITING.getType())){
                    exportWritingInformation(topicsResult, zipOutputStream);
                }
                topicsResult = topicRepository.findAllTopicWithJoinParent(topicType.getType(), PageRequest.of(++pageTopic, pageTopicSize));
            }
        }
        catch (IOException e){
            throw new ApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return new ByteArrayInputStream(zipOut.toByteArray());
    }

    protected void exportSpeakingInformation(List<TopicEntity> topicsResult, ZipOutputStream zipOutputStream) throws IOException {
        List<UUID> topicIds = topicsResult.stream().map(TopicEntity::getTopicId).toList();
        List<QuestionEntity> questionSpeakings = questionRepository.findAllQuestionSpeakingOfTopics(topicIds);
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/excel/_export/service/ExcelExportService.java
        Map<TopicEntity, List<QuestionEntity>> questionsTopic = questionSpeakings.stream()
                .collect(Collectors.groupingBy(elm -> elm.getPart().getTopic()));
=======
        Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup = questionSpeakings.stream().collect(
                Collectors.groupingBy(QuestionEntity::getPart)
        );
        Map<TopicEntity, List<PartEntity>> topicPartsGroup = partQuestionsParentGroup.keySet().stream().collect(
                Collectors.groupingBy(PartEntity::getTopic)
        );
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/excel/service/exp/ExcelExportService.java
        for(TopicEntity topic : topicsResult){
            try(
                    Workbook workbook = new XSSFWorkbook();
                    ByteArrayOutputStream workbookOut = new ByteArrayOutputStream();
            ){
                List<QuestionEntity> questions = questionsTopic.getOrDefault(topic, new ArrayList<>());
                TopicUtil.fillQuestionSpeakingOrWritingToTopic(topic, questions);
                List<PartEntity> partsTopic = topic.getParts().stream()
                        .sorted(Comparator.comparing(PartEntity::getPartName, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();
                writeTopicInformationToSheet(topic, partsTopic, workbook);
                for(PartEntity part : partsTopic){
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/excel/_export/service/ExcelExportService.java
                    exportSpeakingOfTopic(part, workbook);
=======
                    exportSpeakingOfTopic(part, partQuestionsParentGroup, workbook);
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/excel/service/exp/ExcelExportService.java
                }

                workbook.write(workbookOut);
                workbook.close();

                ZipEntry workbookZip = new ZipEntry(String.format("data_%s_%s_%s.xlsx", topic.getTopicType().getTopicTypeName(), topic.getTopicName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
                zipOutputStream.putNextEntry(workbookZip);
                zipOutputStream.write(workbookOut.toByteArray());
                zipOutputStream.closeEntry();
            }
            catch (IOException e){
                log.error(e.getMessage());
                throw e;
            }
        }
    }

    protected void exportSpeakingOfTopic(
            PartEntity part,
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/excel/_export/service/ExcelExportService.java
=======
            Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup,
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/excel/service/exp/ExcelExportService.java
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPartOfSpeaking = workbook.createSheet(String.format("%s_%s", part.getPartName(), part.getPartId()));
        Row partRow = sheetPartOfSpeaking.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/excel/_export/service/ExcelExportService.java
        for(QuestionEntity questionSpeaking : part.getQuestions()){
            Row imageQuestionRow = sheetPartOfSpeaking.createRow(++nextRowNew);
            imageQuestionRow.createCell(0).setCellValue("Image");
            imageQuestionRow.createCell(1).setCellValue(questionSpeaking.getContentImage());
            Row questionSpeakingContentRow = sheetPartOfSpeaking.createRow(++nextRowNew);
            questionSpeakingContentRow.createCell(0).setCellValue("Question Content");
            questionSpeakingContentRow.createCell(1).setCellValue(questionSpeaking.getQuestionContent());
        }
    }

    protected void exportWritingInformation(List<TopicEntity> topicsResult, ZipOutputStream zipOutputStream) throws IOException {
        List<UUID> topicIds = topicsResult.stream().map(TopicEntity::getTopicId).toList();
        List<QuestionEntity> questionSpeakings = questionRepository.findAllQuestionWritingOfTopics(topicIds);
        Map<TopicEntity, List<QuestionEntity>> questionsTopic = questionSpeakings.stream()
                .collect(Collectors.groupingBy(elm -> elm.getPart().getTopic()));
        for(TopicEntity topic : topicsResult){
            try(
                    Workbook workbook = new XSSFWorkbook();
                    ByteArrayOutputStream workbookOut = new ByteArrayOutputStream();
            ){
                List<QuestionEntity> questions = questionsTopic.getOrDefault(topic, new ArrayList<>());
                TopicUtil.fillQuestionSpeakingOrWritingToTopic(topic, questions);
                List<PartEntity> partsTopic = topic.getParts().stream()
                        .sorted(Comparator.comparing(PartEntity::getPartName, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();
                writeTopicInformationToSheet(topic, partsTopic, workbook);
                for(PartEntity part : partsTopic){
                    exportWritingOfTopic(part, workbook);
                }

                workbook.write(workbookOut);
                workbook.close();

                ZipEntry workbookZip = new ZipEntry(String.format("data_%s_%s_%s.xlsx", topic.getTopicType().getTopicTypeName(), topic.getTopicName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
                zipOutputStream.putNextEntry(workbookZip);
                zipOutputStream.write(workbookOut.toByteArray());
                zipOutputStream.closeEntry();
            }
            catch (IOException e){
                log.error(e.getMessage());
                throw e;
            }
        }
    }

    protected void exportWritingOfTopic(
            PartEntity part,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPartOfSpeaking = workbook.createSheet(String.format("%s_%s", part.getPartName(), part.getPartId()));
        Row partRow = sheetPartOfSpeaking.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        for(QuestionEntity questionSpeaking : part.getQuestions()){
=======
        List<QuestionEntity> questionParentsOfPart = partQuestionsParentGroup.getOrDefault(part, new ArrayList<>())
                .stream().sorted(Comparator.comparing(QuestionEntity::getQuestionScore)).toList();
        for(QuestionEntity questionSpeaking : questionParentsOfPart){
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/excel/service/exp/ExcelExportService.java
            Row imageQuestionRow = sheetPartOfSpeaking.createRow(++nextRowNew);
            imageQuestionRow.createCell(0).setCellValue("Image");
            imageQuestionRow.createCell(1).setCellValue(questionSpeaking.getContentImage());
            Row questionSpeakingContentRow = sheetPartOfSpeaking.createRow(++nextRowNew);
            questionSpeakingContentRow.createCell(0).setCellValue("Question Content");
            questionSpeakingContentRow.createCell(1).setCellValue(questionSpeaking.getQuestionContent());
        }
    }

    protected void exportReadingInformation(List<TopicEntity> topicsResult, ZipOutputStream zipOutputStream) throws IOException {
        List<UUID> topicIds = topicsResult.stream().map(TopicEntity::getTopicId).toList();
        List<AnswerEntity> answerTopicList = answerRepository.findAnswersJoinQuestionPartTopicIn(topicIds);
        Map<TopicEntity, List<AnswerEntity>> answersTopic = answerTopicList.stream()
                .collect(Collectors.groupingBy(elm -> elm.getQuestion().getQuestionGroupParent().getPart().getTopic()));
        for(TopicEntity topic : topicsResult){
            try(
                    Workbook workbook = new XSSFWorkbook();
                    ByteArrayOutputStream workbookOut = new ByteArrayOutputStream();
            ){
                List<AnswerEntity> answers = answersTopic.getOrDefault(topic, new ArrayList<>());
                TopicUtil.fillAnswerToTopic(topic, answers);
                List<PartEntity> partsTopic = topic.getParts().stream().toList();
                writeTopicInformationToSheet(topic, partsTopic, workbook);
                for(PartEntity part : partsTopic){
                    exportReadingOfTopic(part, workbook);
                }

                workbook.write(workbookOut);
                workbook.close();

                ZipEntry workbookZip = new ZipEntry(String.format("data_%s_%s_%s.xlsx", topic.getTopicType().getTopicTypeName(), topic.getTopicName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
                zipOutputStream.putNextEntry(workbookZip);
                zipOutputStream.write(workbookOut.toByteArray());
                zipOutputStream.closeEntry();
            }
            catch (IOException e){
                log.error(e.getMessage());
                throw e;
            }
        }
    }

    protected void exportReadingOfTopic(
            PartEntity part,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPartOfReading = workbook.createSheet(String.format("%s_%s", part.getPartName(), part.getPartId()));
        Row partRow = sheetPartOfReading.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionParentsOfPart = part.getQuestions().stream().toList();
        for(QuestionEntity parent : questionParentsOfPart){
            List<QuestionEntity> childs = parent.getQuestionGroupChildren().stream().toList();
            if(childs.isEmpty()) continue;
            Row imageQuestionParentRow = sheetPartOfReading.createRow(++nextRowNew);
            imageQuestionParentRow.createCell(0).setCellValue("Image");
            imageQuestionParentRow.createCell(1).setCellValue(parent.getContentImage());
            Row questionChildHeaderRow = sheetPartOfReading.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Question Content");
            questionChildHeaderRow.createCell(2).setCellValue("A");
            questionChildHeaderRow.createCell(3).setCellValue("B");
            questionChildHeaderRow.createCell(4).setCellValue("C");
            questionChildHeaderRow.createCell(5).setCellValue("D");
            questionChildHeaderRow.createCell(6).setCellValue("Result");
            questionChildHeaderRow.createCell(7).setCellValue("Score");
            questionChildHeaderRow.createCell(8).setCellValue("Image");
            int questionChildsSize = childs.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = childs.get(i);
                Row questionChildRow = sheetPartOfReading.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                questionChildRow.createCell(jStartAnswer + 6).setCellValue(questionChild.getContentImage());
                List<AnswerEntity> answers = questionChild.getAnswers().stream().toList();
                if(answers.isEmpty()) continue;
                for(AnswerEntity answer : answers){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }

    protected void exportReadingAndListeningInformation(List<TopicEntity> topicsResult, ZipOutputStream zipOutputStream) throws IOException {
        List<UUID> topicIds = topicsResult.stream().map(TopicEntity::getTopicId).toList();
        List<AnswerEntity> answerTopicList = answerRepository.findAnswersJoinQuestionPartTopicIn(topicIds);
        Map<TopicEntity, List<AnswerEntity>> answersTopic = answerTopicList.stream()
                .collect(Collectors.groupingBy(elm -> elm.getQuestion().getQuestionGroupParent().getPart().getTopic()));
        List<String> part1Or2 = List.of("part 1", "part 2");
        List<String> part3Or4 = List.of("part 3", "part 4");
        List<String> part6Or7 = List.of("part 6", "part 7");
        for(TopicEntity topic : topicsResult){
            try(
                    Workbook workbook = new XSSFWorkbook();
                    ByteArrayOutputStream workbookOut = new ByteArrayOutputStream();
            ){
                List<AnswerEntity> answers = answersTopic.getOrDefault(topic, new ArrayList<>());
                TopicUtil.fillAnswerToTopic(topic, answers);
                List<PartEntity> partsTopic = topic.getParts().stream().toList();
                writeTopicInformationToSheet(topic, partsTopic, workbook);
                for(PartEntity part : partsTopic){
                    String partNameLower = part.getPartName().toLowerCase();
                    if(part1Or2.contains(partNameLower)){
                        writeReadingAndListeningOfPart1Or2(part, workbook);
                    }
                    else if(part3Or4.contains(partNameLower)){
                        writeReadingAndListeningOfPart3Or4(part, workbook);
                    }
                    else if(partNameLower.equalsIgnoreCase("part 5")){
                        writeReadingAndListeningOfPart5(part, workbook);
                    }
                    else if(part6Or7.contains(partNameLower)){
                        writeReadingAndListeningOfPart6Or7(part, workbook);
                    }
                }

                workbook.write(workbookOut);
                workbook.close();

                ZipEntry workbookZip = new ZipEntry(String.format("data_%s_%s_%s.xlsx", topic.getTopicType().getTopicTypeName(), topic.getTopicName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
                zipOutputStream.putNextEntry(workbookZip);
                zipOutputStream.write(workbookOut.toByteArray());
                zipOutputStream.closeEntry();
            }
            catch (IOException e){
                log.error(e.getMessage());
                throw e;
            }
        }
    }

    protected void writeTopicInformationToSheet(
            TopicEntity topic,
            List<PartEntity> partsTopic,
            Workbook workbook
    ) {
        Sheet sheetTopic = workbook.createSheet("Information Topic");
        Row packTypeRow = sheetTopic.createRow(0);
        packTypeRow.createCell(0).setCellValue("Loại đề thi");
        packTypeRow.createCell(1).setCellValue(topic.getPack().getPackType().getName());
        Row packTypeDescriptionRow = sheetTopic.createRow(1);
        packTypeDescriptionRow.createCell(0).setCellValue("Mô tả loại đề thi");
        packTypeDescriptionRow.createCell(1).setCellValue(topic.getPack().getPackType().getDescription());
        Row packTopicRow = sheetTopic.createRow(2);
        packTopicRow.createCell(0).setCellValue("Bộ đề thi");
        packTopicRow.createCell(1).setCellValue(topic.getPack().getPackName());
        Row topicTypeRow = sheetTopic.createRow(3);
        topicTypeRow.createCell(0).setCellValue("Kiểu đề thi");
        topicTypeRow.createCell(1).setCellValue(topic.getTopicType().getTopicTypeName());
        Row topicNameRow = sheetTopic.createRow(4);
        topicNameRow.createCell(0).setCellValue("Tên đề thi");
        topicNameRow.createCell(1).setCellValue(topic.getTopicName());
        Row topicImageRow = sheetTopic.createRow(5);
        topicImageRow.createCell(0).setCellValue("Ảnh đại diện");
        topicImageRow.createCell(1).setCellValue(topic.getTopicImage());
        Row topicAudioRow = sheetTopic.createRow(6);
        topicAudioRow.createCell(0).setCellValue("Âm thanh");
        topicAudioRow.createCell(1).setCellValue(topic.getTopicAudio());
        Row topicDescriptionRow = sheetTopic.createRow(7);
        topicDescriptionRow.createCell(0).setCellValue("Mô tả đề thi");
        topicDescriptionRow.createCell(1).setCellValue(topic.getTopicDescription());
        Row topicWorkTimeRow = sheetTopic.createRow(8);
        topicWorkTimeRow.createCell(0).setCellValue("Thời gian làm bài");
        topicWorkTimeRow.createCell(1).setCellValue(topic.getWorkTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        Row partTopicRow = sheetTopic.createRow(9);
        partTopicRow.createCell(0).setCellValue("Part");
        String partTopicValue = partsTopic.stream().map(part -> String.format("%s: %s", part.getPartName(), part.getPartType())).collect(Collectors.joining(", "));
        partTopicRow.createCell(1).setCellValue(partTopicValue);
    }

    protected void writeReadingAndListeningOfPart1Or2(
            PartEntity part,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart1Or2 = workbook.createSheet(String.format("%s_%s", part.getPartName(), part.getPartId()));
        Row partRow = sheetPart1Or2.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionsParent = part.getQuestions().stream().toList();
        for(QuestionEntity parent : questionsParent){
            List<QuestionEntity> childs = parent.getQuestionGroupChildren().stream().toList();
            if(childs.isEmpty()) continue;
            Row audioQuestionParentRow = sheetPart1Or2.createRow(++nextRowNew);
            audioQuestionParentRow.createCell(0).setCellValue("Audio");
            audioQuestionParentRow.createCell(1).setCellValue(parent.getContentAudio());
            Row questionChildHeaderRow = sheetPart1Or2.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Result");
            questionChildHeaderRow.createCell(2).setCellValue("Score");
            questionChildHeaderRow.createCell(3).setCellValue("Image");
            questionChildHeaderRow.createCell(4).setCellValue("Audio");
            int questionChildsSize = childs.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = childs.get(i);
                Row questionChildRow = sheetPart1Or2.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(2).setCellValue(questionChild.getQuestionScore());
                questionChildRow.createCell(3).setCellValue(questionChild.getContentImage());
                questionChildRow.createCell(4).setCellValue(questionChild.getContentAudio());
            }
        }
    }

    protected void writeReadingAndListeningOfPart3Or4(
            PartEntity part,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart3Or4 = workbook.createSheet(String.format("%s_%s", part.getPartName(), part.getPartId()));
        Row partRow = sheetPart3Or4.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionsParent = part.getQuestions().stream().toList();
        for(QuestionEntity parent : questionsParent){
            List<QuestionEntity> childs = parent.getQuestionGroupChildren().stream().toList();
            if(childs.isEmpty()) continue;
            Row audioQuestionParentRow = sheetPart3Or4.createRow(++nextRowNew);
            audioQuestionParentRow.createCell(0).setCellValue("Audio");
            audioQuestionParentRow.createCell(1).setCellValue(parent.getContentAudio());
            Row imageQuestionParentRow = sheetPart3Or4.createRow(++nextRowNew);
            imageQuestionParentRow.createCell(0).setCellValue("Image");
            imageQuestionParentRow.createCell(1).setCellValue(parent.getContentImage());
            Row questionChildHeaderRow = sheetPart3Or4.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Question Content");
            questionChildHeaderRow.createCell(2).setCellValue("A");
            questionChildHeaderRow.createCell(3).setCellValue("B");
            questionChildHeaderRow.createCell(4).setCellValue("C");
            questionChildHeaderRow.createCell(5).setCellValue("D");
            questionChildHeaderRow.createCell(6).setCellValue("Result");
            questionChildHeaderRow.createCell(7).setCellValue("Score");
            int questionChildsSize = childs.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = childs.get(i);
                Row questionChildRow = sheetPart3Or4.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                List<AnswerEntity> answersOfChild = questionChild.getAnswers().stream().toList();
                if(answersOfChild.isEmpty()) continue;
                for(AnswerEntity answer : answersOfChild){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }

    protected void writeReadingAndListeningOfPart5(
            PartEntity part,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart5 = workbook.createSheet(String.format("%s_%s", part.getPartName(), part.getPartId()));
        Row partRow = sheetPart5.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionChildsSort = part.getQuestions().stream()
                .flatMap(questionParent -> questionParent.getQuestionGroupChildren().stream())
                .sorted(Comparator.comparing(QuestionEntity::getQuestionNumber, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        Map<QuestionEntity, List<QuestionEntity>> questionsGroup = questionChildsSort.stream().collect(
                Collectors.groupingBy(QuestionEntity::getQuestionGroupParent)
        );
        for(Map.Entry<QuestionEntity, List<QuestionEntity>> entryElm : questionsGroup.entrySet()){
            List<QuestionEntity> childs = entryElm.getValue();
            if(childs.isEmpty()) continue;
            Row questionChildHeaderRow = sheetPart5.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Question Content");
            questionChildHeaderRow.createCell(2).setCellValue("A");
            questionChildHeaderRow.createCell(3).setCellValue("B");
            questionChildHeaderRow.createCell(4).setCellValue("C");
            questionChildHeaderRow.createCell(5).setCellValue("D");
            questionChildHeaderRow.createCell(6).setCellValue("Result");
            questionChildHeaderRow.createCell(7).setCellValue("Score");
            int questionChildsSize = childs.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = childs.get(i);
                Row questionChildRow = sheetPart5.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                List<AnswerEntity> answersOfChild = questionChild.getAnswers().stream().toList();
                if(answersOfChild.isEmpty()) continue;
                for(AnswerEntity answer : answersOfChild){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }

    protected void writeReadingAndListeningOfPart6Or7(
            PartEntity part,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart6Or7 = workbook.createSheet(String.format("%s_%s", part.getPartName(), part.getPartId()));
        Row partRow = sheetPart6Or7.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionsParent = part.getQuestions().stream().toList();
        for(QuestionEntity parent : questionsParent){
            List<QuestionEntity> childs = parent.getQuestionGroupChildren().stream().toList();
            if(childs.isEmpty()) continue;
            Row questionContentParentRow = sheetPart6Or7.createRow(++nextRowNew);
            questionContentParentRow.createCell(0).setCellValue("Question Content");
            questionContentParentRow.createCell(1).setCellValue(parent.getQuestionContent());
            if(part.getPartName().equalsIgnoreCase("part 7")){
                Row imageQuestionParentRow = sheetPart6Or7.createRow(++nextRowNew);
                imageQuestionParentRow.createCell(0).setCellValue("Image");
                imageQuestionParentRow.createCell(1).setCellValue(parent.getContentImage());
            }
            Row questionChildHeaderRow = sheetPart6Or7.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Question Content Child");
            questionChildHeaderRow.createCell(2).setCellValue("A");
            questionChildHeaderRow.createCell(3).setCellValue("B");
            questionChildHeaderRow.createCell(4).setCellValue("C");
            questionChildHeaderRow.createCell(5).setCellValue("D");
            questionChildHeaderRow.createCell(6).setCellValue("Result");
            questionChildHeaderRow.createCell(7).setCellValue("Score");
            int questionChildsSize = childs.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = childs.get(i);
                Row questionChildRow = sheetPart6Or7.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                List<AnswerEntity> answersOfChild = questionChild.getAnswers().stream().toList();
                if(answersOfChild.isEmpty()) continue;
                for(AnswerEntity answer : answersOfChild){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }
}
