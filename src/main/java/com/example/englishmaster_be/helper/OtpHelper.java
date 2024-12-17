package com.example.englishmaster_be.helper;

import java.util.Random;

public class OtpHelper {

    public static String generateOtpCode(int codeLength){

        String pattern = "0123456789";

        Random random = new Random();

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < codeLength; i++){

            int randomIndex = random.nextInt(pattern.length() - 1);

            char charAt = pattern.charAt(randomIndex);

            sb.append(charAt);
        }

        return sb.toString();
    }

}
