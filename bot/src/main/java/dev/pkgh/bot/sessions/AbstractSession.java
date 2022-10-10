package dev.pkgh.bot.sessions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
@Getter
public abstract class AbstractSession implements Session {

    /** System name (reserved for future extensions) */
    String systemName;

    /** User, that we are awaiting input from */
    User user;

    /** Chat, where user started session */
    Chat chat;

    @Override
    public void begin() {
        // Override me
    }

    @Override
    public void end() {
        // Override me
    }
}
