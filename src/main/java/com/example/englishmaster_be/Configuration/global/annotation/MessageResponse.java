package com.example.englishmaster_be.Configuration.global.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageResponse {

    String value() default "";

}
