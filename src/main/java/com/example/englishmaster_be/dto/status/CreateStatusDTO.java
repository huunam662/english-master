package com.example.englishmaster_be.dto.status;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateStatusDTO {
    String statusName;
    UUID typeId;
    boolean flag;
}
