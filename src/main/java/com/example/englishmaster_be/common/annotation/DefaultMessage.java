package com.example.englishmaster_be.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultMessage {

    String value() default "";

}
