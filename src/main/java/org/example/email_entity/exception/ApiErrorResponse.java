package org.example.email_entity.exception;

import java.util.Map;

public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> details
) {
}
