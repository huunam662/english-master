package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

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

    public UUID getPackId() {
        return packId;
    }

    public void setPackId(UUID packId) {
        this.packId = packId;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
