package com.example.englishmaster_be.Common.Enums;


public enum SortByFeedbackFieldsEnum {

    FeedbackId,
    Name,
    Content,
    Description,
    CreateAt,
    UpdateAt;

    public static SortByFeedbackFieldsEnum fromString(String value) {
        try{
            return com.example.englishmaster_be.Common.Enums.SortByFeedbackFieldsEnum.valueOf(value.trim().toUpperCase());
        }
        catch(IllegalArgumentException e){
            return null;
        }
    }
}
