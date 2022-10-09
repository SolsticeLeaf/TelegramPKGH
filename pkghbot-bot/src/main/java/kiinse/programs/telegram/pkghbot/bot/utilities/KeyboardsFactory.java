/*
 * MIT License
 *
 * Copyright (c) 2022 ⭕️⃤ kiinse
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package kiinse.programs.telegram.pkghbot.bot.utilities;

import kiinse.programs.telegram.pkghbot.api.utils.enums.ButtonSettings;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyboardsFactory {

    private KeyboardsFactory() {}

    public static @NotNull ReplyKeyboardMarkup Keyboard(@NotNull String button1, @NotNull String button2) {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        var keyboard = new ArrayList<KeyboardRow>();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        keyboard.add(getButtonRow(button1, button2));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public static @NotNull ReplyKeyboardMarkup Keyboard(@NotNull KeyboardRow row1, @NotNull KeyboardRow row2) {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        var rows = new ArrayList<KeyboardRow>();
        rows.add(row1);
        rows.add(row2);
        replyKeyboardMarkup.setKeyboard(new ArrayList<>(rows));
        return replyKeyboardMarkup;
    }

    public static @NotNull ReplyKeyboardMarkup Keyboard(@NotNull List<KeyboardRow> rows) {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(new ArrayList<>(rows));
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup inlineConstructor(@NotNull String button1, @NotNull String callback1, @NotNull String button2,
                                                         @NotNull String callback2) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(getInlineRow(button1, callback1, button2, callback2)));
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup settings(boolean isGroupMessage, @NotNull ButtonSettings settings) {
        var rows = new ArrayList<List<InlineKeyboardButton>>();
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (settings == ButtonSettings.USERS) {
            rows.add(getInlineRow("Авто-рассылка✉", "settings_mailing"));
            rows.add(getInlineRow("Рассылка пар✉", "settings_lesson_mailing"));
            rows.add(getInlineRow("Изменить группу🆔", "settings_group"));
            if (!isGroupMessage) rows.add(getInlineRow("Добавить бота в беседу👥", "settings_add_chat_group"));
            rows.add(getInlineRow("Закончить", "settings_close"));
        } else if (settings == ButtonSettings.ADMINS) {
            rows.add(getInlineRow("Административные уведомления", "admin_notifications"));
            rows.add(getInlineRow("Закончить", "admin_close"));
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup scheduleList(boolean isGroupMessage) {
        var rows = new ArrayList<List<InlineKeyboardButton>>();
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (isGroupMessage) rows.add(getInlineRow("Сегодня", "сегодня",
                                                  "Завтра", "завтра"));
        rows.add(getInlineRow("Понедельник", "понедельник", "Вторник", "вторник"));
        rows.add(getInlineRow("Среда", "среда",
                              "Четверг", "четверг"));
        rows.add(getInlineRow("Пятница", "пятница",
                              "Суббота", "суббота"));
        rows.add(getInlineRow("Вся неделя", "расписание"));
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public static KeyboardRow getButtonRow(@NotNull String button) {
        var row = new KeyboardRow();
        row.add(button);
        return row;
    }

    public static KeyboardRow getButtonRow(@NotNull String button1, @NotNull String button2) {
        var row = new KeyboardRow();
        row.add(button1);
        row.add(button2);
        return row;
    }

    public static List<InlineKeyboardButton> getInlineRow(@NotNull String button, @NotNull String callback) {
        var row = new ArrayList<InlineKeyboardButton>();
        var inlineButton = new InlineKeyboardButton();
        inlineButton.setText(button);
        inlineButton.setCallbackData(callback);
        row.add(inlineButton);
        return row;
    }

    public static List<InlineKeyboardButton> getInlineRow(@NotNull String button1, @NotNull String callback1, @NotNull String button2,
                                                          @NotNull String callback2) {
        var row = new ArrayList<InlineKeyboardButton>();
        var inlineButton1 = new InlineKeyboardButton();
        inlineButton1.setText(button1);
        inlineButton1.setCallbackData(callback1);
        var inlineButton2 = new InlineKeyboardButton();
        inlineButton2.setText(button2);
        inlineButton2.setCallbackData(callback2);
        row.add(inlineButton1);
        row.add(inlineButton2);
        return row;
    }
}
