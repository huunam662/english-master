package com.example.englishmaster_be.DTO.Part;

import java.util.UUID;

public class UpdatePartDTO {
    private String partName;
    private String partDiscription;
    private String partType;

    public UpdatePartDTO() {
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartDiscription() {
        return partDiscription;
    }

    public void setPartDiscription(String partDiscription) {
        this.partDiscription = partDiscription;
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }
}
