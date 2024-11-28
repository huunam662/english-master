package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusResponse {

    UUID statusId;

    UUID typeId;

    String statusName;

    boolean flag;

    public StatusResponse(Status status) {

        if(Objects.isNull(status)) return;

        this.statusId = status.getStatusId();
        this.statusName = status.getStatusName();
        this.flag = status.isFlag();

        if(Objects.nonNull(status.getType()))
            this.typeId = status.getType().getTypeId();
    }
}
