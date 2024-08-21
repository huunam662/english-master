package com.example.englishmaster_be.Model.Response;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
@RedisHash("otp")
@Data
public class Otp implements Serializable {
    private String otp;
    private String email;
    private String status;
}
