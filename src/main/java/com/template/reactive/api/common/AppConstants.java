package com.template.reactive.api.common;

public final class AppConstants {
    public static final String ERROR_MESSAGE_FORMAT = "{\"message\": \"%s\"}";

    public static final String DEFAULT_ERROR_MESSAGE = String.format(ERROR_MESSAGE_FORMAT, "An error occurred, please see logs for details.");

    public static final String DATETIME_PATTERN = "dd-MM-yyyy'T'HH:mm:ss.SSSZ";

    private AppConstants() { throw new IllegalStateException("Utility class."); }
}