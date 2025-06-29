package com.example.englishmaster_be.domain.excel.service.exp;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.answer.repository.jpa.AnswerRepository;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.topic.repository.jpa.TopicRepository;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import jakarta.servlet.ServletOutputStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelExportService implements IExcelExportService{

    AnswerRepository answerRepository;
    QuestionRepository questionRepository;
    TopicRepository topicRepository;

    @Override
    public ByteArrayInputStream exportTopicById(UUID topicId) {

        TopicEntity topic = topicRepository.findAllTopicWithJoinParent(topicId)
                .orElseThrow(() -> new ErrorHolder(Error.RESOURCE_NOT_FOUND));

        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(zipOut)){
            TopicTypeEntity topicType = topic.getTopicType();
            if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.LISTENING_READING.getType())){
                exportReadingAndListeningInformation(List.of(topic), zipOutputStream);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.READING.getType())){
                exportReadingInformation(List.of(topic), zipOutputStream);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.SPEAKING.getType())){
                exportSpeakingInformation(List.of(topic), zipOutputStream);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.WRITING.getType())){
                exportSpeakingInformation(List.of(topic), zipOutputStream);
            }
        }
        catch (IOException e){
            throw new ErrorHolder(Error.BAD_REQUEST, e.getMessage(), false);
        }
        return new ByteArrayInputStream(zipOut.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportAllTopic() {

        int pageTopic = 0;
        int pageTopicSize = 10;
        List<TopicEntity> topicsResult = topicRepository.findAllTopicWithJoinParent(PageRequest.of(pageTopic, pageTopicSize));
        Map<TopicType, List<TopicEntity>> typeTopicGroup = topicsResult.stream().collect(
                Collectors.groupingBy(topic -> TopicType.fromType(topic.getTopicType().getTopicTypeName()))
        );

        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(zipOut)){
            while (!topicsResult.isEmpty()){
                for(TopicType topicType : TopicType.values()){
                    List<TopicEntity> topicsOfType = typeTopicGroup.getOrDefault(topicType, new ArrayList<>());
                    if(topicType.getType().equalsIgnoreCase(TopicType.LISTENING_READING.getType())){
                        exportReadingAndListeningInformation(topicsOfType, zipOutputStream);
                    }
                    else if(topicType.getType().equalsIgnoreCase(TopicType.READING.getType())){
                        exportReadingInformation(topicsOfType, zipOutputStream);
                    }
                    else if(topicType.getType().equalsIgnoreCase(TopicType.SPEAKING.getType())){
                        exportSpeakingInformation(topicsOfType, zipOutputStream);
                    }
                    else if(topicType.getType().equalsIgnoreCase(TopicType.WRITING.getType())){
                        exportSpeakingInformation(topicsOfType, zipOutputStream);
                    }
                }
                topicsResult = topicRepository.findAllTopicWithJoinParent(PageRequest.of(++pageTopic, pageTopicSize));
            }
        }
        catch (IOException e){
            throw new ErrorHolder(Error.BAD_REQUEST, e.getMessage(), false);
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
                if(topicType.getType().equalsIgnoreCase(TopicType.LISTENING_READING.getType())){
                    exportReadingAndListeningInformation(topicsResult, zipOutputStream);
                }
                else if(topicType.getType().equalsIgnoreCase(TopicType.READING.getType())){
                    exportReadingInformation(topicsResult, zipOutputStream);
                }
                else if(topicType.getType().equalsIgnoreCase(TopicType.SPEAKING.getType())){
                    exportSpeakingInformation(topicsResult, zipOutputStream);
                }
                else if(topicType.getType().equalsIgnoreCase(TopicType.WRITING.getType())){
                    exportSpeakingInformation(topicsResult, zipOutputStream);
                }
                topicsResult = topicRepository.findAllTopicWithJoinParent(topicType.getType(), PageRequest.of(++pageTopic, pageTopicSize));
            }
        }
        catch (IOException e){
            throw new ErrorHolder(Error.BAD_REQUEST, e.getMessage(), false);
        }
        return new ByteArrayInputStream(zipOut.toByteArray());
    }

    protected void exportSpeakingInformation(List<TopicEntity> topicsResult, ZipOutputStream zipOutputStream) throws IOException {
        List<UUID> topicIds = topicsResult.stream().map(TopicEntity::getTopicId).toList();
        List<QuestionEntity> questionSpeakings = questionRepository.findAllQuestionSpeakingOfTopics(topicIds);
        Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup = questionSpeakings.stream().collect(
                Collectors.groupingBy(QuestionEntity::getPart)
        );
        Map<TopicEntity, List<PartEntity>> topicPartsGroup = partQuestionsParentGroup.keySet().stream().collect(
                Collectors.groupingBy(PartEntity::getTopic)
        );
        for(TopicEntity topic : topicsResult){
            try(
                    Workbook workbook = new XSSFWorkbook();
                    ByteArrayOutputStream workbookOut = new ByteArrayOutputStream();
            ){
                List<PartEntity> partsTopic = topicPartsGroup.getOrDefault(topic, new ArrayList<>());
                partsTopic = partsTopic.stream().sorted(Comparator.comparing(PartEntity::getPartName)).toList();
                writeTopicInformationToSheet(topic, partsTopic, workbook);
                for(PartEntity part : partsTopic){
                    exportSpeakingOfTopic(part, partQuestionsParentGroup, workbook);
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
            Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPartOfSpeaking = workbook.createSheet(part.getPartName());
        Row partRow = sheetPartOfSpeaking.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionParentsOfPart = partQuestionsParentGroup.getOrDefault(part, new ArrayList<>())
                .stream().sorted(Comparator.comparing(QuestionEntity::getQuestionScore)).toList();
        for(QuestionEntity questionSpeaking : questionParentsOfPart){
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
        Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup = answerTopicList.stream().collect(
                Collectors.groupingBy(AnswerEntity::getQuestion)
        );
        Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup = questionChildAnswersGroup.keySet().stream().collect(
                Collectors.groupingBy(QuestionEntity::getQuestionGroupParent)
        );
        Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup = questionParentChildsGroup.keySet().stream().collect(
                Collectors.groupingBy(QuestionEntity::getPart)
        );
        Map<TopicEntity, List<PartEntity>> topicPartsGroup = partQuestionsParentGroup.keySet().stream().collect(
                Collectors.groupingBy(PartEntity::getTopic)
        );
        for(TopicEntity topic : topicsResult){
            try(
                    Workbook workbook = new XSSFWorkbook();
                    ByteArrayOutputStream workbookOut = new ByteArrayOutputStream();
            ){
                List<PartEntity> partsTopic = topicPartsGroup.getOrDefault(topic, new ArrayList<>());
                partsTopic = partsTopic.stream().sorted(Comparator.comparing(PartEntity::getPartName)).toList();
                writeTopicInformationToSheet(topic, partsTopic, workbook);
                for(PartEntity part : partsTopic){
                    exportReadingOfTopic(part, partQuestionsParentGroup, questionParentChildsGroup, questionChildAnswersGroup, workbook);
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
            Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup,
            Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup,
            Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPartOfReading = workbook.createSheet(part.getPartName());
        Row partRow = sheetPartOfReading.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionParentsOfPart = partQuestionsParentGroup.getOrDefault(part, new ArrayList<>());
        for(QuestionEntity questionParent : questionParentsOfPart){
            Row imageQuestionParentRow = sheetPartOfReading.createRow(++nextRowNew);
            imageQuestionParentRow.createCell(0).setCellValue("Image");
            imageQuestionParentRow.createCell(1).setCellValue(questionParent.getContentImage());
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
            List<QuestionEntity> questionChildsOfParent = questionParentChildsGroup.getOrDefault(questionParent, new ArrayList<>());
            int questionChildsSize = questionChildsOfParent.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = questionChildsOfParent.get(i);
                Row questionChildRow = sheetPartOfReading.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                questionChildRow.createCell(jStartAnswer + 6).setCellValue(questionChild.getContentImage());
                List<AnswerEntity> answersOfChild = questionChildAnswersGroup.getOrDefault(questionChild, new ArrayList<>());
                for(AnswerEntity answer : answersOfChild){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }

    protected void exportReadingAndListeningInformation(List<TopicEntity> topicsResult, ZipOutputStream zipOutputStream) throws IOException {
        List<UUID> topicIds = topicsResult.stream().map(TopicEntity::getTopicId).toList();
        List<AnswerEntity> answerTopicList = answerRepository.findAnswersJoinQuestionPartTopicIn(topicIds);
        Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup = answerTopicList.stream().collect(
                Collectors.groupingBy(AnswerEntity::getQuestion)
        );
        Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup = questionChildAnswersGroup.keySet().stream().collect(
                Collectors.groupingBy(QuestionEntity::getQuestionGroupParent)
        );
        Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup = questionParentChildsGroup.keySet().stream().collect(
                Collectors.groupingBy(QuestionEntity::getPart)
        );
        Map<TopicEntity, List<PartEntity>> topicPartsGroup = partQuestionsParentGroup.keySet().stream().collect(
                Collectors.groupingBy(PartEntity::getTopic)
        );
        List<String> part1Or2 = List.of("part 1", "part 2");
        List<String> part3Or4 = List.of("part 3", "part 4");
        List<String> part6Or7 = List.of("part 6", "part 7");
        for(TopicEntity topic : topicsResult){
            try(
                    Workbook workbook = new XSSFWorkbook();
                    ByteArrayOutputStream workbookOut = new ByteArrayOutputStream();
            ){
                List<PartEntity> partsTopic = topicPartsGroup.getOrDefault(topic, new ArrayList<>());
                partsTopic = partsTopic.stream().sorted(Comparator.comparing(PartEntity::getPartName)).toList();
                writeTopicInformationToSheet(topic, partsTopic, workbook);
                for(PartEntity part : partsTopic){
                    String partNameLower = part.getPartName().toLowerCase();
                    if(part1Or2.contains(partNameLower)){
                        writeReadingAndListeningOfPart1Or2(part, partQuestionsParentGroup, questionParentChildsGroup, workbook);
                    }
                    else if(part3Or4.contains(partNameLower)){
                        writeReadingAndListeningOfPart3Or4(part, partQuestionsParentGroup, questionParentChildsGroup, questionChildAnswersGroup, workbook);
                    }
                    else if(partNameLower.equalsIgnoreCase("part 5")){
                        writeReadingAndListeningOfPart5(part, partQuestionsParentGroup, questionParentChildsGroup, questionChildAnswersGroup, workbook);
                    }
                    else if(part6Or7.contains(partNameLower)){
                        writeReadingAndListeningOfPart6Or7(part, partQuestionsParentGroup, questionParentChildsGroup, questionChildAnswersGroup, workbook);
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
        Row topicDescriptionRow = sheetTopic.createRow(6);
        topicDescriptionRow.createCell(0).setCellValue("Mô tả đề thi");
        topicDescriptionRow.createCell(1).setCellValue(topic.getTopicDescription());
        Row topicWorkTimeRow = sheetTopic.createRow(7);
        topicWorkTimeRow.createCell(0).setCellValue("Thời gian làm bài");
        topicWorkTimeRow.createCell(1).setCellValue(topic.getWorkTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        Row partTopicRow = sheetTopic.createRow(8);
        partTopicRow.createCell(0).setCellValue("Part");
        String partTopicValue = partsTopic.stream().map(part -> String.format("%s: %s", part.getPartName(), part.getPartType())).collect(Collectors.joining(", "));
        partTopicRow.createCell(1).setCellValue(partTopicValue);
    }

    protected void writeReadingAndListeningOfPart1Or2(
            PartEntity part,
            Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup,
            Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart1Or2 = workbook.createSheet(part.getPartName());
        Row partRow = sheetPart1Or2.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionParentsOfPart = partQuestionsParentGroup.getOrDefault(part, new ArrayList<>());
        for(QuestionEntity questionParent : questionParentsOfPart){
            Row audioQuestionParentRow = sheetPart1Or2.createRow(++nextRowNew);
            audioQuestionParentRow.createCell(0).setCellValue("Audio");
            audioQuestionParentRow.createCell(1).setCellValue(questionParent.getContentAudio());
            Row questionChildHeaderRow = sheetPart1Or2.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Result");
            questionChildHeaderRow.createCell(2).setCellValue("Score");
            questionChildHeaderRow.createCell(3).setCellValue("Image");
            questionChildHeaderRow.createCell(4).setCellValue("Audio");
            List<QuestionEntity> questionChildsOfParent = questionParentChildsGroup.getOrDefault(questionParent, new ArrayList<>());
            int questionChildsSize = questionChildsOfParent.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = questionChildsOfParent.get(i);
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
            Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup,
            Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup,
            Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart3Or4 = workbook.createSheet(part.getPartName());
        Row partRow = sheetPart3Or4.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionParentsOfPart = partQuestionsParentGroup.getOrDefault(part, new ArrayList<>());
        for(QuestionEntity questionParent : questionParentsOfPart){
            Row audioQuestionParentRow = sheetPart3Or4.createRow(++nextRowNew);
            audioQuestionParentRow.createCell(0).setCellValue("Audio");
            audioQuestionParentRow.createCell(1).setCellValue(questionParent.getContentAudio());
            Row imageQuestionParentRow = sheetPart3Or4.createRow(++nextRowNew);
            imageQuestionParentRow.createCell(0).setCellValue("Image");
            imageQuestionParentRow.createCell(1).setCellValue(questionParent.getContentImage());
            Row questionChildHeaderRow = sheetPart3Or4.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Question Content");
            questionChildHeaderRow.createCell(2).setCellValue("A");
            questionChildHeaderRow.createCell(3).setCellValue("B");
            questionChildHeaderRow.createCell(4).setCellValue("C");
            questionChildHeaderRow.createCell(5).setCellValue("D");
            questionChildHeaderRow.createCell(6).setCellValue("Result");
            questionChildHeaderRow.createCell(7).setCellValue("Score");
            List<QuestionEntity> questionChildsOfParent = questionParentChildsGroup.getOrDefault(questionParent, new ArrayList<>());
            int questionChildsSize = questionChildsOfParent.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = questionChildsOfParent.get(i);
                Row questionChildRow = sheetPart3Or4.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                List<AnswerEntity> answersOfChild = questionChildAnswersGroup.getOrDefault(questionChild, new ArrayList<>());
                for(AnswerEntity answer : answersOfChild){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }

    protected void writeReadingAndListeningOfPart5(
            PartEntity part,
            Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup,
            Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup,
            Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart5 = workbook.createSheet(part.getPartName());
        Row partRow = sheetPart5.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionParentsOfPart = partQuestionsParentGroup.getOrDefault(part, new ArrayList<>());
        for(QuestionEntity questionParent : questionParentsOfPart){
            Row questionChildHeaderRow = sheetPart5.createRow(++nextRowNew);
            questionChildHeaderRow.createCell(0).setCellValue("STT");
            questionChildHeaderRow.createCell(1).setCellValue("Question Content");
            questionChildHeaderRow.createCell(2).setCellValue("A");
            questionChildHeaderRow.createCell(3).setCellValue("B");
            questionChildHeaderRow.createCell(4).setCellValue("C");
            questionChildHeaderRow.createCell(5).setCellValue("D");
            questionChildHeaderRow.createCell(6).setCellValue("Result");
            questionChildHeaderRow.createCell(7).setCellValue("Score");
            List<QuestionEntity> questionChildsOfParent = questionParentChildsGroup.getOrDefault(questionParent, new ArrayList<>());
            int questionChildsSize = questionChildsOfParent.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = questionChildsOfParent.get(i);
                Row questionChildRow = sheetPart5.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                List<AnswerEntity> answersOfChild = questionChildAnswersGroup.getOrDefault(questionChild, new ArrayList<>());
                for(AnswerEntity answer : answersOfChild){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }

    protected void writeReadingAndListeningOfPart6Or7(
            PartEntity part,
            Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup,
            Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup,
            Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup,
            Workbook workbook
    ){
        int nextRowNew = 0;
        Sheet sheetPart6Or7 = workbook.createSheet(part.getPartName());
        Row partRow = sheetPart6Or7.createRow(nextRowNew);
        partRow.createCell(0).setCellValue(part.getPartName());
        partRow.createCell(1).setCellValue(part.getPartType());
        List<QuestionEntity> questionParentsOfPart = partQuestionsParentGroup.getOrDefault(part, new ArrayList<>());
        for(QuestionEntity questionParent : questionParentsOfPart) {
            Row questionContentParentRow = sheetPart6Or7.createRow(++nextRowNew);
            questionContentParentRow.createCell(0).setCellValue("Question Content");
            questionContentParentRow.createCell(1).setCellValue(questionParent.getQuestionContent());
            if(part.getPartName().equalsIgnoreCase("part 7")){
                Row imageQuestionParentRow = sheetPart6Or7.createRow(++nextRowNew);
                imageQuestionParentRow.createCell(0).setCellValue("Image");
                imageQuestionParentRow.createCell(1).setCellValue(questionParent.getContentImage());
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
            List<QuestionEntity> questionChildsOfParent = questionParentChildsGroup.getOrDefault(questionParent, new ArrayList<>());
            int questionChildsSize = questionChildsOfParent.size();
            for(int i = 0; i < questionChildsSize; i++){
                QuestionEntity questionChild = questionChildsOfParent.get(i);
                Row questionChildRow = sheetPart6Or7.createRow(++nextRowNew);
                questionChildRow.createCell(0).setCellValue(i + 1);
                questionChildRow.createCell(1).setCellValue(questionChild.getQuestionContent());
                int jStartAnswer = 2;
                questionChildRow.createCell(jStartAnswer + 4).setCellValue(questionChild.getQuestionResult());
                questionChildRow.createCell(jStartAnswer + 5).setCellValue(questionChild.getQuestionScore());
                List<AnswerEntity> answersOfChild = questionChildAnswersGroup.getOrDefault(questionChild, new ArrayList<>());
                for(AnswerEntity answer : answersOfChild){
                    questionChildRow.createCell(jStartAnswer++).setCellValue(answer.getAnswerContent());
                }
            }
        }
    }
}
