package dev.pkgh.sdk.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Optional;

/**
 * @author winston
 */
@UtilityClass
public final class MessageUtil {

    private static TelegramLongPollingBot instance;

    /**
     * Called once in bot <init>
     */
    public void instantiate(final @NonNull TelegramLongPollingBot instance) {
        if (MessageUtil.instance != null) {
            throw new IllegalStateException("MessageUtil is already configured for instance: " + MessageUtil.instance.getClass().getSimpleName());
        }

        MessageUtil.instance = instance;
    }

    public void sendMessage(
            final @NotNull Chat chat,
            final @NotNull String message
    ) {
        sendMessage(String.valueOf(chat.getId()), message, (InlineKeyboardMarkup) null);
    }

    /**
     * Sends a plain text message
     *
     * @param chatId        Chat ID
     * @param message       Plain-text message
     */
    public void sendMessage(
            final @NotNull String chatId,
            final @NotNull String message,
            final @Nullable ReplyKeyboardMarkup markup
    ) {
        val apiMethod = new SendMessage();
        apiMethod.setText(message);
        apiMethod.setChatId(chatId);
        apiMethod.enableMarkdown(true);
        apiMethod.setReplyMarkup(markup);

        try {
            instance.execute(apiMethod);
        } catch (final @NotNull Exception $) {
            $.printStackTrace();
        }
    }
    public void sendMessage(
            final @NotNull String chatId,
            final @NotNull String message,
            final @Nullable InlineKeyboardMarkup markup
    ) {
        val apiMethod = new SendMessage();
        apiMethod.setText(message);
        apiMethod.setChatId(chatId);
        apiMethod.enableMarkdown(true);
        apiMethod.setReplyMarkup(markup);

        try {
            instance.execute(apiMethod);
        } catch (final @NotNull Exception $) {
            $.printStackTrace();
        }
    }


    /**
     * Get optional chat ID by {@link Update}
     *
     * @param update    update
     * @return          optional of chat ID
     */
    public @NotNull Optional<@NotNull String> getChatId(final @NonNull Update update) {
        if (update.hasMessage()) {
            return Optional.of(update.getMessage().getChatId().toString());
        }

        if (update.hasCallbackQuery()) {
            return Optional.of(update.getCallbackQuery().getMessage().getChatId().toString());
        }

        return Optional.empty();
    }

}
