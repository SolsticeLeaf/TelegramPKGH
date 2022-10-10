package dev.pkgh.bot.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum Setting {

    /**
     * Automatically notify user when schedule of his group updates
     */
    NOTIFICATOR, // 1 << 0 | 00_00_01

    /**
     * Sends error messages when something went wrong (Setting is used by developers)
     * Note: This shit sends errors even if they raised on other user's session
     */
    ERROR_RECEIVER // 1 << 1 | 00_00_10

    ;

    /** Fixing memory leaks (ghetto method) */
    public static final Setting[] VALUES = values();

    /** Bitmask */
    int mask = 1 << ordinal();

    public boolean hasFlag(final int flags) {
        return (flags & mask) != 0;
    }

    /**
     * Get settings array by bitmask
     * @return settings array
     */
    public static Setting[] getSettings(final int flags) {
        return Arrays.stream(VALUES).filter(sex -> sex.hasFlag(flags)).toArray(Setting[]::new);
    }

}
