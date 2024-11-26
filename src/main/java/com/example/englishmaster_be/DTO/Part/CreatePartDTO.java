package com.example.englishmaster_be.DTO.Part;




public class CreatePartDTO {
    private String partName;
    private String partDiscription;
    private String partType;

    public CreatePartDTO() {
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
