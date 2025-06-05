package com.example.englishmaster_be.config.app.error;

import com.example.englishmaster_be.advice.exception.handler.GlobalExceptionHandler;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppErrorController implements ErrorController {

    GlobalExceptionHandler globalExceptionHandler;

    @GetMapping("/error")
    public ModelAndView handleError(Exception ex) {

        return globalExceptionHandler.noResourceFoundHandler(ex);
    }
}
