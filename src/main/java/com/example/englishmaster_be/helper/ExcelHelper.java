package com.example.englishmaster_be.helper;

import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {

    public static boolean isExcelFile(MultipartFile file) {

        String contentType = file.getContentType();

        String fileName = file.getOriginalFilename();

        return contentType == null ||
                (!contentType.equals("application/vnd.ms-excel") &&
                        !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) ||
                (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")));
    }

}
