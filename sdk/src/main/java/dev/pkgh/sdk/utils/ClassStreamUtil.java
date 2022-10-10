package dev.pkgh.sdk.utils;

import dev.pkgh.sdk.logging.Logging;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author winston
 */
@UtilityClass
public final class ClassStreamUtil {

    /**
     * Get input stream by resource
     *
     * @param name  name of resource file
     * @return      input stream instance
     */
    public @Nullable InputStream getByResource(final @NonNull String name) {
        return ClassStreamUtil.class.getClassLoader().getResourceAsStream(name);
    }

    /**
     * Get properties from resources directory
     *
     * @param name  name of resources file
     * @return      loaded properties instance
     */
    public @Nullable Properties loadFromResources(final @NonNull String name) {
        val properties = new Properties();
        try {
            properties.load(getByResource(name));
        } catch (Exception e) {
            Logging.IMP.stacktrace(e, "Exception thrown during processing properties file");
            return null;
        }

        return properties;
    }

}
