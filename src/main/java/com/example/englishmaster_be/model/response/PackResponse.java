package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackResponse {

    UUID packId;

    String packName;

    String createAt;

    String updateAt;

    public PackResponse(Pack pack) {

        if(Objects.isNull(pack)) return;

        this.packId = pack.getPackId();
        this.packName = pack.getPackName();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(pack.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(pack.getCreateAt()));
        if(Objects.nonNull(pack.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(pack.getUpdateAt()));
    }

}
