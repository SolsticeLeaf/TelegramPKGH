package dev.pkgh.bot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author winston
 */
@UtilityClass
public final class KeyboardUtil {

    /**
     * @return keyboard, that is used as main menu
     */
    public @NotNull ReplyKeyboardMarkup getMainMenuKeyboard() {
        val replyKeyboardMarkup = new ReplyKeyboardMarkup();
        val keyboard = new ArrayList<KeyboardRow>();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        keyboard.add(getButtonRow("Группа", "Расписание"));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    /**
     * Creates inline keyboard markup for cancelling some action.
     *
     * @param cancelCallback    which action we are closing
     * @return                  inline keyboard markup
     */
    public InlineKeyboardMarkup bakeCancelButton(final @NonNull String cancelCallback) {
        val inlineKeyboardMarkup = new InlineKeyboardMarkup();
        val row = new ArrayList<InlineKeyboardButton>();
        val inlineButton = new InlineKeyboardButton();

        inlineButton.setText("Отменить");
        inlineButton.setCallbackData(cancelCallback);

        row.add(inlineButton);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(row));

        return inlineKeyboardMarkup;
    }

    public @NotNull KeyboardRow getButtonRow(@NotNull String... buttons) {
        var row = new KeyboardRow();
        Collections.addAll(row, buttons);
        return row;
    }

}
