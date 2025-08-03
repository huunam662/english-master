package com.example.englishmaster_be.common.constant.speaking_test;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
=======
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum LevelSpeakerType {

    Needs_Improvement("Needs Improvement"),
    Basic_Communicator("Basic Communicator"),
    Average_Communicator("Average Communicator"),
    Good_Communicator("Good Communicator"),
    Native_Communicator("Native Communicator");

    String level;

    LevelSpeakerType(String level){
        this.level = level;
    }

    public static final LevelSpeakerType DEFAULT = Needs_Improvement;

    public static LevelSpeakerType fromLevel(String level){
        if(level == null || level.isEmpty()) return DEFAULT;
        return Arrays.stream(values()).filter(l -> l.level.equalsIgnoreCase(level))
                .findFirst().orElse(DEFAULT);
    }

<<<<<<< HEAD
    @JsonValue
=======
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
    @Override
    public String toString() {
        return this.level;
    }
<<<<<<< HEAD

    public static class Converter implements AttributeConverter<LevelSpeakerType, String>{

        @Override
        public String convertToDatabaseColumn(LevelSpeakerType attribute) {
            return attribute == null ? DEFAULT.getLevel() : attribute.getLevel();
        }

        @Override
        public LevelSpeakerType convertToEntityAttribute(String dbData) {
            return LevelSpeakerType.fromLevel(dbData);
        }
    }
=======
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
}
