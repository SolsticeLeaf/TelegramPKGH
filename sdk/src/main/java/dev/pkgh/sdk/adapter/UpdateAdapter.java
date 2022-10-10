package dev.pkgh.sdk.adapter;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author winston
 */
public interface UpdateAdapter {

    /**
     * Basically, handles update
     *
     * @param update    received update
     */
    void handle(final @NotNull Update update);

}
