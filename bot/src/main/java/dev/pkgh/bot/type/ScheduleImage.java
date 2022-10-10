package dev.pkgh.bot.type;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.awt.image.RenderedImage;
import java.time.LocalDateTime;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Data
public final class ScheduleImage {

    /** Rendered image of schedule */
    RenderedImage image;

    /** Is image cached? */
    String group;

    /** Time, when image was captured */
    LocalDateTime gatheredAt;

}
