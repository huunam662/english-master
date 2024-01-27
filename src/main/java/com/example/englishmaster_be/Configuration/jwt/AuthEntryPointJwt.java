package com.example.englishmaster_be.Configuration.jwt;

import com.example.englishmaster_be.Model.ResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Configuration(proxyBeanMethods = true)
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(200);

        ResponseModel responseModel = new ResponseModel();

        responseModel.setMessage(authException.getMessage());
        responseModel.setStatus("fail");
        responseModel.setViolations(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));


        final ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        mapper.setDateFormat(sdf);
        mapper.writeValue(response.getOutputStream(), responseModel);

    }
}
