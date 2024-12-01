package com.example.englishmaster_be.Util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtil {

    public static HttpServletRequest getRequest() {

        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {

        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

}
