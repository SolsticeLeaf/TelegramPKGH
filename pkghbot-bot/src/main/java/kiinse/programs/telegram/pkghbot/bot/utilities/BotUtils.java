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

import io.sentry.Sentry;
import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.data.enums.UserStatus;
import kiinse.programs.telegram.pkghbot.api.posts.Post;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostSettings;
import kiinse.programs.telegram.pkghbot.api.utils.LoggerUtils;
import kiinse.programs.telegram.pkghbot.bot.Bot;
import kiinse.programs.telegram.pkghbot.core.data.Config;
import kiinse.programs.telegram.pkghbot.core.data.Text;
import kiinse.programs.telegram.pkghbot.core.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

public class BotUtils {

    private static final Logger logger = LoggerUtils.getLogger();

    private BotUtils() {}

    public static void sendMessage(@NotNull User user, @NotNull String text) {
        sendMessage(user, text, null, null);
    }

    public static void sendMessage(@NotNull User user, @NotNull String text, @Nullable InlineKeyboardMarkup inlineKeyboard) {
        sendMessage(user, text, null, inlineKeyboard);
    }

    public static void sendMessage(@NotNull User user, @NotNull String text, @Nullable ReplyKeyboardMarkup replyKeyBoard) {
        sendMessage(user, text, replyKeyBoard, null);
    }

    public static void sendMessage(@NotNull User user, @NotNull String text, @Nullable ReplyKeyboardMarkup replyKeyBoard,
                                   @Nullable InlineKeyboardMarkup inlineKeyboard) {
        var message = new SendMessage();
        message.setChatId(user.getChatId());
        message.enableHtml(true);
        if (text.length() >= 4096) {
            message.setText(text.substring(0, 2048));
            execute(user, message);
            message.setText(text.substring(2048));
            if (inlineKeyboard != null) {
                message.setReplyMarkup(inlineKeyboard);
            } else if (replyKeyBoard != null) {
                message.setReplyMarkup(replyKeyBoard);
            }
            execute(user, message);
            return;
        }

        message.setText(text);
        if (inlineKeyboard != null) {
            message.setReplyMarkup(inlineKeyboard);
        } else if (replyKeyBoard != null) {
            message.setReplyMarkup(replyKeyBoard);
        }
        execute(user, message);
    }

    public static void sendMessage(long chat, @NotNull String text) {
        var message = new SendMessage();
        message.setChatId(chat);
        message.enableHtml(true);
        message.setText(text);
        execute(message);
    }

    public static void sendPhoto(@NotNull User user, @NotNull java.io.File file) {
        var message = new SendPhoto();
        message.setChatId(user.getChatId());
        message.setPhoto(new InputFile().setMedia(file));
        execute(user, message);
        file.delete();
    }

    public static void sendPost(@NotNull Post post) throws TelegramApiException {
        sendPostMessage(Config.getProperty("channel"),
                        String.format(Text.getText(false, Text.TextName.POST),
                                      post.getText(),
                                      post.getSettings() == PostSettings.ANONYMOUS ? "Anon" : post.getUser().getName()));
    }

    public static void sendPostPreview(@NotNull User user, @NotNull Post post) throws TelegramApiException {
        sendPostMessage(String.valueOf(user.getChatId()),
                        String.format(Text.getText(false, Text.TextName.POSTPREVIEW),
                                      post.getText(),
                                      post.getSettings() == PostSettings.ANONYMOUS ? "Anon" : post.getUser().getName(),
                                      post.getId()));
    }

    public static void sendPostPreviewToAdmin(@NotNull User user, @NotNull Post post) throws TelegramApiException {
        sendPostMessage(String.valueOf(user.getChatId()),
                        String.format(Text.getText(false, Text.TextName.POSTADDED),
                                      post.getText(),
                                      post.getSettings() == PostSettings.ANONYMOUS ? "Anon" : post.getUser().getName(),
                                      post.getId()));
    }

    private static void sendPostMessage(@NotNull String chat, @NotNull String text) throws TelegramApiException {
        var message = new SendMessage();
        message.setChatId(chat);
        message.enableHtml(true);
        message.setText(text);
        Bot.getInstance().execute(message);
    }

    public static void sendAutoMailing(@NotNull User user, @NotNull String text) {
        var message = new SendMessage();
        message.setChatId(user.getChatId());
        message.enableHtml(true);
        message.setText(text);
        execute(user, message);
    }

    public static void editMessageText(@NotNull User user, int messageId, @NotNull String text, @Nullable InlineKeyboardMarkup inlineKeyboard) {
        var message = new EditMessageText();
        message.setChatId(user.getChatId());
        message.setMessageId(messageId);
        message.enableHtml(true);
        message.setText(text);
        if (inlineKeyboard != null) message.setReplyMarkup(inlineKeyboard);
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            logger.warn("Error on editing message.\nChat ID: {} | Message ID: {} | Reason: {}\nText: {}",
                        user.getChatId(),
                        messageId,
                        e.getMessage(),
                        text);
            Sentry.captureException(e);
        }
    }

    public static void editMessageText(@NotNull User user, int messageId, @NotNull String text) {
        var message = new EditMessageText();
        message.setChatId(user.getChatId());
        message.setMessageId(messageId);
        message.enableHtml(true);
        message.setText(text);
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            logger.warn("Error on editing message.\nChat ID: {} | Message ID: {} | Reason: {}\nText: {}",
                        user.getChatId(),
                        messageId,
                        e.getMessage(),
                        text);
            Sentry.captureException(e);
        }
    }

    private static void execute( @NotNull SendMessage message) {
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            Sentry.captureException(e);
        }
    }

    private static void execute(@NotNull User user, @NotNull SendPhoto message) {
        //TODO: Убрать
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            Sentry.captureException(e);
            if (e.getMessage().contains("was blocked by the user")) {
                user.setStatus(UserStatus.BOT_BLOCKED).upload();
                logger.info("User {} blocked the bot", user.getName());
            } else if (e.getMessage().contains("user is deactivated") || e.getMessage().contains("chat not found") || e.getMessage().contains(
                    "chat was deleted")) {
                user.setStatus(UserStatus.DEACTIVATED).upload();
                logger.info("User {} has been deactivated", user.getName());
            } else {
                logger.warn("Error on sending message.\nChat ID: {} | User group: {} | Reason: {}",
                            user.getChatId(),
                            user.getGroup(),
                            e.getMessage());
            }
        }
    }

    private static void execute(@NotNull User user, @NotNull SendMessage message) {
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            Sentry.captureException(e);
            if (e.getMessage().contains("was blocked by the user")) {
                user.setStatus(UserStatus.BOT_BLOCKED).upload();
                logger.info("User {} blocked the bot", user.getName());
            } else if (e.getMessage().contains("user is deactivated") || e.getMessage().contains("chat not found") || e.getMessage().contains(
                    "chat was deleted")) {
                user.setStatus(UserStatus.DEACTIVATED).upload();
                logger.info("User {} has been deactivated", user.getName());
            } else {
                logger.warn("Error on sending message.\nChat ID: {} | User group: {} | Reason: {}",
                            user.getChatId(),
                            user.getGroup(),
                            e.getMessage());
            }
        }
    }

    public static void editToUserSettings(@NotNull User user, int messageId, boolean isGroupMessage) {
        editToUserSettings(user, messageId, isGroupMessage, null);
    }

    public static void editToUserSettings(@NotNull User user, int messageId, boolean isGroupMessage, @Nullable InlineKeyboardMarkup keyboard) {
        BotUtils.editMessageText(
                user,
                messageId,
                String.format(
                        Text.getText(isGroupMessage, Text.TextName.SETTINGS),
                        user.getGroup(),
                        TextUtils.emojiBoolean(user.isMailing()),
                        TextUtils.emojiBoolean(user.isLessonMailing()),
                        Config.getVersion(Config.Version.BOT)),
                keyboard);
    }

    public static void editToAdminSettings(@NotNull User user, int messageId, boolean isGroupMessage) {
        editToAdminSettings(user, messageId, isGroupMessage, null);
    }


    public static void editToAdminSettings(@NotNull User user, int messageId, boolean isGroupMessage, @Nullable InlineKeyboardMarkup keyboard) {
        BotUtils.editMessageText(
                user,
                messageId,
                String.format(
                        Text.getText(isGroupMessage, Text.TextName.ADMINSETTINGS),
                        user.getName(),
                        user.getChatId(),
                        user.getGroup(),
                        TextUtils.emojiBoolean(user.isAdminMailing()),
                        Config.getVersion(Config.Version.BOT)),
                keyboard);
    }

    public static boolean isGroupMessage(@NotNull Update rawUpdate) {
        return rawUpdate.getMessage().isGroupMessage() || rawUpdate.getMessage().isSuperGroupMessage();
    }
}
