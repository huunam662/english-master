package com.example.englishmaster_be.config.app.error;

import com.example.englishmaster_be.advice.exception.handler.GlobalExceptionHandler;
import com.example.englishmaster_be.common.constant.error.Error;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Slf4j(topic = "APP-ERROR-CONTROLLER")
@Controller
public class AppErrorController implements ErrorController {

    @GetMapping("${server.error.path:${error.path:/error}}")
    public ModelAndView handleError(Exception ex) {

        Error error = Error.RESOURCE_NOT_FOUND;

        log.error("{} -> code {}", ex.getMessage(), error.getStatusCode().value());

        ModelAndView mav = new ModelAndView("404/endpoint.404");

        mav.setStatus(error.getStatusCode());

        return mav;
    }
}
