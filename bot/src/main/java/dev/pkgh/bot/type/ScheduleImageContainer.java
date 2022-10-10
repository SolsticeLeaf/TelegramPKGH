package dev.pkgh.bot.type;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Data
public final class ScheduleImageContainer {

    /** Schedule Image instance */
    ScheduleImage scheduleImage;

    /** Is image cached */
    boolean cached;

}
