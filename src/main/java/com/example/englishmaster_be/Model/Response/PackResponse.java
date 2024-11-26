package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class PackResponse {
    private UUID packId;
    private String packName;

    private String createAt;
    private String updateAt;

    public PackResponse(Pack pack) {
        this.packId = pack.getPackId();
        this.packName = pack.getPackName();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(pack.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(pack.getUpdateAt()));
    }

}
