package com.template.reactive.api.common;

public final class AppConstants {
    public static final String DEFAULT_ERROR_MESSAGE = "An error occurred, please see logs for details.";

    public static final String ERROR_MESSAGE_FORMAT = "traceId: %s, Message: %s";

    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    public static final String DATETIME_PATTERN = "dd-MM-yyyy'T'HH:mm:ss.SSSZ";

    private AppConstants() { throw new IllegalStateException("Utility class."); }
}