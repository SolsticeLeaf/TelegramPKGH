package dev.pkgh.bot.sessions;

import dev.pkgh.sdk.adapter.UpdateAdapter;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * @author winston
 */
public interface Session extends UpdateAdapter {

    /**
     * @return User, that is attached to current session
     */
    @NotNull User getUser();

    /**
     * @return Chat, in which session runs
     */
    @NotNull Chat getChat();

    /**
     * @return Reserved for future extension system name of session
     */
    @NotNull String getSystemName();

    /**
     * Starts session (invoked externally)
     */
    void begin();

    /**
     * Stops session (invoked internally and externally)
     */
    void end();

}
