package dev.pkgh.bot.adapter;

import dev.pkgh.bot.core.PKGHBot;
import dev.pkgh.bot.sessions.SessionsManager;
import dev.pkgh.bot.users.BotUser;
import dev.pkgh.sdk.adapter.UpdateAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class CommandInvokerAdapter implements UpdateAdapter {

    /** Bot core instance */
    PKGHBot instance;

    @Override
    public void handle(@NotNull Update update) {
        // We handle only messages with text
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            return;
        }

        // Dropping commands from user, that belongs to any session
        if (SessionsManager.IMP.hasActiveSession(update.getMessage().getFrom().getId())) {
            return;
        }

        // Executing shit
        instance.getCommandManager().executeCommand(
                BotUser.getOrCreate(update.getMessage().getFrom().getId()),
                update.getMessage().getFrom(),
                update.getMessage().getChat(),
                update.getMessage().getText(),
                update);
    }
}
