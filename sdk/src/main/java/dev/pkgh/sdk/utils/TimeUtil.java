package dev.pkgh.sdk.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author winston
 */
@UtilityClass
public final class TimeUtil {

    public static String STANDARD_PATTERN = "yyyy-dd-MM HH:mm:ss";

    /** Default pattern */
    public @NonNull String getFormattedDate() {
        return getFormattedDate(STANDARD_PATTERN);
    }

    /**
     * Get a formatted date-time by pattern
     * @param format    format pattern
     * @return          formatted date-time
     */
    public @NonNull String getFormattedDate(final @NonNull String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

}
