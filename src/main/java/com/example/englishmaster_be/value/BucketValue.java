package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BucketValue {

    @Value("${englishmaster.connect.student-nodejs}")
    private String bucketNameStudentNodejs;

    @Value("${firebase.adminsdk.path}")
    private String adminSdkPath;
}
