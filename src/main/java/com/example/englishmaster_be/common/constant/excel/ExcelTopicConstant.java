package com.example.englishmaster_be.common.constant.excel;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ExcelTopicConstant {

    Name("Tên đề thi"),
    Image_Path("Ảnh đại diện"),
    Description("Mô tả"),
    Type("Loại đề thi"),
    Work_Time("Thời gian làm bài"),
    Number_Question("Số lượng câu hỏi"),
    Exam_Set("Bộ đề thi"),
    Part("Part");

    String headerName;

    ExcelTopicConstant(String headerName) {
        this.headerName = headerName;
    }

}
