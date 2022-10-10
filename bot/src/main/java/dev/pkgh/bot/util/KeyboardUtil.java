package dev.pkgh.bot.util;

import dev.pkgh.bot.type.Setting;
import dev.pkgh.bot.users.BotUser;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        keyboard.add(getButtonRow("Настройки"));
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

    public @NotNull InlineKeyboardMarkup bakeSettings(final @NonNull BotUser botUser) {
        val inlineKeyboardMarkup = new InlineKeyboardMarkup();

        val lines = new ArrayList<List<InlineKeyboardButton>>();

        lines.add(createRow(createInlineButton("Обновить группу", "settings_update_group")));
        lines.add(createRow(createInlineButton("Авто-Рассылка (" +
                (botUser.getSettings().contains(Setting.NOTIFICATOR) ? "+" : "-") + ")", "settings_toggle_mailing")));
        lines.add(createRow(createInlineButton("Назад", "start")));

        inlineKeyboardMarkup.setKeyboard(lines);

        return inlineKeyboardMarkup;
    }

    public List<InlineKeyboardButton> createRow(InlineKeyboardButton... buttons) {
        val row = new ArrayList<InlineKeyboardButton>();
        Collections.addAll(row, buttons);
        return row;
    }

    public @NotNull KeyboardRow getButtonRow(@NotNull String... buttons) {
        var row = new KeyboardRow();
        for (val btn : buttons) {
            row.add(new KeyboardButton(btn));
        }

        return row;
    }


    public InlineKeyboardButton createInlineButton(final @NonNull String text,
                                                   final @NonNull String callback) {
        val keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(text);
        keyboardButton.setCallbackData(callback);

        return keyboardButton;
    }

}
