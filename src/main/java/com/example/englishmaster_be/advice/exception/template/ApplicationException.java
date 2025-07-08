package com.example.englishmaster_be.advice.exception.template;

import lombok.NonNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ApplicationException extends ResponseStatusException {

    public ApplicationException(@NonNull HttpStatusCode status, @NonNull String reason) {
        super(status, reason);
    }

    public ApplicationException(@NonNull HttpStatusCode status, @NonNull String reason, Throwable cause) {
        super(status, reason, cause);
    }

    @Override
    @NonNull
    public String getMessage() {
        return super.getReason();
    }
}
