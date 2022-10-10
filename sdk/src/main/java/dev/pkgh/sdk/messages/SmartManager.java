package dev.pkgh.sdk.messages;

import dev.pkgh.sdk.adapter.UpdateAdapter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.graalvm.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Manager for publishing SmartMessages :tm:
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SmartManager implements UpdateAdapter {

    /** Bot instance */
    TelegramLongPollingBot bot;

    /**
     * Collection of active messages
     * UUID - [SmartMessage - Message]
     */
    Map<UUID, Pair<SmartMessage, Message>> activeMessages = new HashMap<>();

    /**
     * @param message the message that we are publishing
     */
    @SneakyThrows
    public UUID publishMessages(final @NonNull SmartMessage message) {
        if (activeMessages.containsKey(message.getUniqueId())) {
            throw new IllegalStateException("This message is already posted!");
        }

        activeMessages.put(message.getUniqueId(), Pair.create(message, bot.execute(message.bakeMessage())));

        return message.getUniqueId();
    }

    // TODO: Update internal markup (in smartmessage instance)
    @SneakyThrows
    public void updateMessageMarkup(final @NonNull UUID uniqueId,
                                    final @NonNull InlineKeyboardMarkup newInstance) {
        if (!activeMessages.containsKey(uniqueId)) {
            throw new IllegalStateException("This message does not exist");
        }

        val editMsg = new EditMessageReplyMarkup();
        editMsg.setReplyMarkup(newInstance);
        bot.execute(editMsg);
    }

    /**
     * Deletes message from registered map
     * @param uniqueId  unique identificator of message
     */
    public void destroyMessages(final @NonNull UUID uniqueId) {
        if (!activeMessages.containsKey(uniqueId)) {
            throw new IllegalStateException("Message is not registered!");
        }

        val msg = activeMessages.get(uniqueId).getRight();

        // Deleting message
        val dm = new DeleteMessage();
        dm.setChatId(msg.getChatId());
        dm.setMessageId(msg.getMessageId());

        try {
            bot.execute(dm);
        } catch (Exception e) {
            // ignored (message can be already deleted)
        }

        activeMessages.remove(uniqueId);
    }

    /**
     * Called when we receive an update, so we process it here
     * @param update    received update
     */
    @Override public void handle(@NotNull Update update) {
        if (!update.hasCallbackQuery()) {
            return;
        }

        val data = update.getCallbackQuery().getData();

        for (val message : activeMessages.values()) {
            if (message.getLeft().getActionExecutorMap().containsKey(data)) {
                if (Objects.equals(message.getRight().getMessageId(), update.getCallbackQuery().getMessage().getMessageId()))  {
                    message.getLeft().getActionExecutorMap().get(data).getRight().handleActionExecution(update, this, message.getLeft());
                }
            }
        }
    }
}
