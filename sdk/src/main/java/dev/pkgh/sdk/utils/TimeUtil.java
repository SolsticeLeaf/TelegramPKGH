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

    /** Default pattern */
    public @NonNull String getFormattedDate() {
        return getFormattedDate("yyyy-dd-MM HH:mm:ss z");
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
