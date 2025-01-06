package com.example.englishmaster_be.common.constant.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorEnum {

    SEND_EMAIL_FAILURE("Failed to send confirmation email", HttpStatus.INTERNAL_SERVER_ERROR),
    PART_NOT_FOUND("Part not found", HttpStatus.NOT_FOUND),
    MOCK_TEST_NOT_FOUND("Mock test not found", HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND("Answer not found", HttpStatus.NOT_FOUND),
    ANSWER_BY_CORRECT_QUESTION_NOT_FOUND("Question don't have correct answer", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("You don't have permission", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED("Account is disabled", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED("Your token cannot be authenticated", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("Your token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Your token is malformed and cannot be processed", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("Your token is unsupported", HttpStatus.UNAUTHORIZED),
    CAN_NOT_CREATE_TOPIC_BY_EXCEL("Can not be created by excel_fill", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_1_BY_EXCEL("Can not be create Part 1 by excel_fill file_storage", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_2_BY_EXCEL("Can not be create Part 2 by excel_fill file_storage", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_3_BY_EXCEL("Can not be create Part 3 by excel_fill file_storage",  HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_4_BY_EXCEL("Can not be create Part 4 by excel_fill file_storage",  HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_5_BY_EXCEL("Can not be create Part 5 by excel_fill file_storage",  HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_6_BY_EXCEL("Can not be create Part 6 by excel_fill file_storage",  HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_7_BY_EXCEL("Can not be create Part 7 by excel_fill file_storage",  HttpStatus.BAD_REQUEST),
    CONTENT_NOT_FOUND("Content not found",  HttpStatus.BAD_REQUEST),
    FILE_IMPORT_IS_NOT_EXCEL("You have been imported doesn't excel_fill file_storage", HttpStatus.BAD_REQUEST),
    STATUS_NOT_FOUND("Status not found", HttpStatus.BAD_REQUEST),
    SHEET_NOT_FOUND_FOR_PART_3("Sheet not found for Part 3", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_1("Sheet not found for Part 1", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_2("Sheet not found for Part 2", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_7("Sheet not found for Part 7", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_4("Sheet not found for Part 4", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_5("Sheet not found for Part 5", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_6("Sheet not found for Part 6", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_TOPIC("Sheet not found for topic", HttpStatus.NOT_FOUND),
    UPLOAD_FILE_FAILURE("Server upload has been error", HttpStatus.BAD_REQUEST),
    CODE_EXISTED_IN_TOPIC("Code existed in topic", HttpStatus.BAD_REQUEST),
    NULL_OR_EMPTY_FILE("File is null or empty", HttpStatus.BAD_REQUEST);

    String message;
    HttpStatus statusCode;

    ErrorEnum(String message, HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}

