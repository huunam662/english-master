package com.example.englishmaster_be.common.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ResourceKeyRes {

    private UUID resourceId;

    public ResourceKeyRes(UUID resourceId) {
        this.resourceId = resourceId;
    }
}
