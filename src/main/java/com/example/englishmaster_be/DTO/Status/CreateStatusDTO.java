package com.example.englishmaster_be.DTO.Status;

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
