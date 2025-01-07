package com.example.englishmaster_be.common.constant.excel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ExcelHeaderContentConstant {

    Audio("Audio"),
    Image("Image"),
    Score("Score"),
    STT("STT"),
    Question_Content("Question Content"),
    Question_Content_Child("Question Content Child"),
    A("A"), B("B"), C("C"), D("D"),
    Result("Result");

    String headerName;

    ExcelHeaderContentConstant(String headerName){
        this.headerName = headerName;
    }

}
