package com.example.englishmaster_be.common.constant.sort;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SortByTypeFieldsEnum {

    None,

    TypeName,

    NameSlug

}
