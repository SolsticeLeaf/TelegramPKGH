package dev.pkgh.sdk.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

/**
 * @author winston
 */
@UtilityClass
public final class Checker {

    public void nonNull(final @Nullable Object object, final @NonNull String name) {
        if (object == null) {
            throw new IllegalStateException(name + " is null");
        }
    }

}
