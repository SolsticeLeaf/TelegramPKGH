package dev.pkgh.bot.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.pkgh.bot.commands.ScheduleCommand;
import dev.pkgh.bot.type.ScheduleImage;
import dev.pkgh.bot.type.ScheduleImageContainer;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.xml.sax.SAXException;
import xyz.winston.parser.core.ScheduleRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@UtilityClass
public final class ScheduleUtil {

    /** Caching schedule is cool! */
    Cache<ScheduleImage, String> groupScheduleCache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    /**
     * Checks if group exists
     * @param string    raw group name
     * @return          yea or nah??!
     */
    public boolean isValidGroup(final @NonNull String string) {
        val entries = groupScheduleCache.asMap().entrySet();

        if(entries.stream().anyMatch(e -> e.getValue().equalsIgnoreCase(string))) {
            return true;
        }

        try {
            loadSchedule(string);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public ScheduleImageContainer loadSchedule(final @NonNull String group) throws IOException, SAXException {
        // If shit is cached. Taking it
        val entries = groupScheduleCache.asMap().entrySet();
        if (entries.stream().anyMatch(entry -> entry.getValue().equalsIgnoreCase(group))) {
            return new ScheduleImageContainer(entries.stream()
                    .filter(entry -> entry.getValue().equalsIgnoreCase(group))
                    .findFirst()
                    .get()
                    .getKey(), true);
        }

        val image = ScheduleRenderer.ofGroup(group).bakeImage();

        // Drawing watermark :D
        val graphics = convertRenderedImage(image).getGraphics();
        graphics.setFont(Font.getFont("Arial"));
        graphics.setColor(Color.BLACK);
        graphics.drawString("@pkghV2_bot | by winston & kiinse", 10, image.getHeight() - 10);
        graphics.dispose();

        val scheduledImage = new ScheduleImage(image, group, LocalDateTime.now());

        // Caching shit
        groupScheduleCache.put(scheduledImage, group);

        return new ScheduleImageContainer(scheduledImage, false);
    }

    public BufferedImage convertRenderedImage(RenderedImage img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage)img;
        }

        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable();
        String[] keys = img.getPropertyNames();
        if (keys!=null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }


}
