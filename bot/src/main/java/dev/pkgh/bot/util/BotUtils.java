package dev.pkgh.bot.util;

import dev.pkgh.sdk.utils.MessageUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Chat;

/**
 * @author winston
 */
@UtilityClass
public final class BotUtils {

    /**
     * Invoked when user uses /start
     * @param chat  chat, where user returned to main menu
     */
    public void handleStart(final @NonNull Chat chat) {
        MessageUtil.sendMessage(String.valueOf(chat.getId()), "Привет! Этот бот отправляет расписание из самой крутой шараги в мире - ПКГХ.\n" +
                "Внизу появились кнопочки, используй их что бы задать свою группу, а затем запроси расписание.\n" +
                "Я знаю, текст хуйня, потом переделаем :D", KeyboardUtil.getMainMenuKeyboard());
    }

}
