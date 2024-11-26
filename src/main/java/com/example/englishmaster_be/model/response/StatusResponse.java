package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusResponse {
    UUID statusId;
    String statusName;
    UUID typeId;
    boolean flag;

    public StatusResponse(Status status) {
        this.statusId = status.getStatusId();
        this.statusName = status.getStatusName();
        this.flag = status.isFlag();
        this.typeId = status.getType().getTypeId();
    }
}
