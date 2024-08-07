package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicDTO;
import com.example.englishmaster_be.Model.Topic;
import com.example.englishmaster_be.Service.IExcelService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class ExcelServiceImpl implements IExcelService {

    @Override
    public CreateTopicByExcelFileDTO parseCreateTopicDTO(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        String topicName = sheet.getRow(1).getCell(1).getStringCellValue();
        String topicImageName = sheet.getRow(2).getCell(1).getStringCellValue();
        String topicDescription = sheet.getRow(3).getCell(1).getStringCellValue();
        String topicType = sheet.getRow(4).getCell(1).getStringCellValue();
        String workTime = sheet.getRow(5).getCell(1).getStringCellValue();
        int numberQuestion = Integer.parseInt(sheet.getRow(6).getCell(1).getStringCellValue());
        String topicPackName = sheet.getRow(7).getCell(1).getStringCellValue();

        CreateTopicByExcelFileDTO createTopicDTO = CreateTopicByExcelFileDTO.builder()
                .topicName(topicName)
                .topicImageName(topicImageName)
                .topicDescription(topicDescription)
                .topicType(topicType)
                .workTime(workTime)
                .numberQuestion(numberQuestion)
                .topicPackName()
                .build();


    }
}
