package dev.pkgh.bot.sessions.impl;

import dev.pkgh.bot.sessions.AbstractSession;
import dev.pkgh.bot.sessions.SessionsManager;
import dev.pkgh.bot.users.BotUser;
import dev.pkgh.bot.util.ScheduleUtil;
import dev.pkgh.sdk.utils.MessageUtil;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * When user is sending his group
 *
 * @author winston
 */
public final class GroupAwaitSession extends AbstractSession {

    public GroupAwaitSession(final @NonNull User user,
                             final @NonNull Chat chat) {
        super("group-await", user, chat);
    }

    @Override
    public void handle(@NotNull Update update) {
        val rawGroup = update.getMessage().getText();

        if (!ScheduleUtil.isValidGroup(rawGroup)) {
            MessageUtil.sendMessage(chat,
                    "Такой группы не существует! Попробуйте снова.\n" +
                    "Если вы больше не хотите выбирать группу, пропишите /start");
            return;
        }

        val botUser = BotUser.getOrCreate(user.getId());
        botUser.setGroup(rawGroup);
        botUser.saveAsync();

        MessageUtil.sendMessage(chat, "Ваша группа изменена на: " + rawGroup);

        SessionsManager.IMP.stopSession(this);
    }
}
