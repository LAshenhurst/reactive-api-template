package com.template.reactive.api.helper;

import com.template.reactive.api.common.AppConstants;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TimeHelper {
    private static final DateTimeFormatter dt = DateTimeFormatter.ofPattern(AppConstants.DATETIME_PATTERN)
            .withZone(ZoneId.systemDefault());

    public static String parseInstant(Instant instant) { return dt.format(instant); }

    private TimeHelper() { throw new IllegalStateException("Utility class."); }
}