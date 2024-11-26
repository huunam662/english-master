package com.example.englishmaster_be.dto.status;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class UpdateStatusDTO {
    private UUID id;
    private String statusName;
    private boolean flag;
    private UUID typeId;
}
