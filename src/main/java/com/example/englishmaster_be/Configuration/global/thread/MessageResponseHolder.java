package com.example.englishmaster_be.Configuration.global.thread;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageResponseHolder {

    static ThreadLocal<String> messageHolder = new ThreadLocal<>();

    public static void setMessage(String message) {
        messageHolder.set(message);
    }

    public static String getMessage() {
        return messageHolder.get();
    }

    public static void clear() {
        messageHolder.remove();
    }

}
