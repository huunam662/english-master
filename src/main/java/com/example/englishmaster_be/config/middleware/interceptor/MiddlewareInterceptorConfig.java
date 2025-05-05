package com.example.englishmaster_be.config.middleware.interceptor;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Nonnull;

@Component
public class MiddlewareInterceptorConfig implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @Nonnull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @Nonnull Object handler
    ) throws Exception
    {
        HandlerInterceptor.super.preHandle(request, response, handler);

        System.out.println("-> preHandle in Interceptor");

        if(handler instanceof HandlerMethod handlerMethod) {

            DefaultMessage messageResponse = handlerMethod.getMethodAnnotation(DefaultMessage.class);

            System.out.println("messageResponse: " + messageResponse);

            if(messageResponse != null)
                MessageResponseHolder.setMessage(messageResponse.value());
        }

        return true;
    }

    @Override
    public void postHandle(
            @NonNull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull Object handler,
            ModelAndView modelAndView
    ) throws Exception
    {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);

        System.out.println("-> postHandle in Interceptor");
    }

    @Override
    public void afterCompletion(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull Object handler,
            Exception ex
    ) throws Exception
    {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);

        System.out.println("-> afterCompletion in Interceptor");

        MessageResponseHolder.clear();

        System.out.println("-> messageResponse after clear by thread holder: " + MessageResponseHolder.getMessage());
    }
}
