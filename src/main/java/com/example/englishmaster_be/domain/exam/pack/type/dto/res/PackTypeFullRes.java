package com.example.englishmaster_be.domain.exam.pack.type.dto.res;

import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PackTypeFullRes {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserRes createdBy;
    private UserRes updatedBy;
}
