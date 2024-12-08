package com.example.englishmaster_be.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum Error {
    PART_NOT_FOUND("Not found Part by id", "Not found Part by id", HttpStatus.NOT_FOUND),
    MOCK_TEST_NOT_FOUND("Mock test not found by id", "Mock test not found by id", HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND("Answer not found by id", "Answer not found by id", HttpStatus.NOT_FOUND),
    ANSWER_BY_CORRECT_QUESTION_NOT_FOUND("Question don't have correct Answer", "Question don't have correct Answer", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("You do not have permission", "You do not have permission", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED("Account is disabled", "Account is disabled", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED("Unauthenticated", "Your token cannot be authenticated ", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("Token Expired", "Your token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid Token", "Your token is malformed and cannot be processed", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("Unsupported Token", "Your token is unsupported", HttpStatus.UNAUTHORIZED),
    CAN_NOT_CREATE_TOPIC_BY_EXCEL("Can not be created by excel", "Can not create Topic by excel", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_1_BY_EXCEL("Can not be create Part 1 by excel file", "Can not be create Part 1 by excel file", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_2_BY_EXCEL("Can not be create Part 2 by excel file", "Can not be create Part 2 by excel file", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_3_BY_EXCEL("Can not be create Part 3 by excel file", "Can not be create Part 3 by excel file", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_4_BY_EXCEL("Can not be create Part 4 by excel file", "Can not be create Part 4 by excel file", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_5_BY_EXCEL("Can not be create Part 5 by excel file", "Can not be create Part 5 by excel file", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_6_BY_EXCEL("Can not be create Part 6 by excel file", "Can not be create Part 6 by excel file", HttpStatus.BAD_REQUEST),
    CAN_NOT_CREATE_PART_7_BY_EXCEL("Can not be create Part 7 by excel file", "Can not be create Part 7 by excel file", HttpStatus.BAD_REQUEST),
    CONTENT_NOT_FOUND("Content not found", "Content not found", HttpStatus.BAD_REQUEST),
    FILE_IMPORT_IS_NOT_EXCEL("Please import excel file", "File import is not excel file", HttpStatus.BAD_REQUEST),
    STATUS_NOT_FOUND("Status not found", "Status not found", HttpStatus.BAD_REQUEST),
    SHEET_NOT_FOUND_FOR_PART_3("Sheet not found for Part 3", "Sheet not found for Part 3", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_1("Sheet not found for Part 1", "Sheet not found for Part 1", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_2("Sheet not found for Part 2", "Sheet not found for Part 2", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_7("Sheet not found for Part 7", "Sheet not found for Part 7", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_4("Sheet not found for Part 4", "Sheet not found for Part 4", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_5("Sheet not found for Part 5", "Sheet not found for Part 5", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_PART_6("Sheet not found for Part 6", "Sheet not found for Part 6", HttpStatus.NOT_FOUND),
    SHEET_NOT_FOUND_FOR_TOPIC("Sheet not found for Topic", "Sheet not found for Topic", HttpStatus.NOT_FOUND),
    UPLOAD_FILE_FAILURE("Server upload has been error", "Upload file failure", HttpStatus.BAD_REQUEST),
    CODE_EXISTED_IN_TOPIC("Code existed in Topic", "Code existed in Topic", HttpStatus.BAD_REQUEST);

    private final String message;
    private final String violation;
    private final HttpStatus statusCode;

    Error(String message, String violation, HttpStatus statusCode) {
        this.message = message;
        this.violation = violation;
        this.statusCode = statusCode;
    }
}

