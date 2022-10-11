package dev.pkgh.sdk.messages;

import dev.pkgh.sdk.adapter.UpdateAdapter;
import dev.pkgh.sdk.logging.Logging;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.graalvm.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.*;
import java.util.stream.Collectors;

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
     * FIXME: Critical vulnerability - overflow of map, if adding many messages. Add cleanup
     * --> FIXED 11.10.2022 @ winston
     */
    Map<UUID, Pair<SmartMessage, Message>> activeMessages = new HashMap<>();

    // Not more than 5 active smart messages per chat
    int CONSTANT_MESSAGES_PER_CHAT = 2;

    /**
     * @param message the message that we are publishing
     */
    @SneakyThrows
    public UUID publishMessages(final @NonNull SmartMessage message) {
        if (activeMessages.containsKey(message.getUniqueId())) {
            throw new IllegalStateException("This message is already posted!");
        }

        activeMessages.put(message.getUniqueId(), Pair.create(message, bot.execute(message.bakeMessage())));

        // shit fix for message overflow vulnerability
        val messagesStream = activeMessages.values().stream().filter(s -> s.getRight().getChatId().equals(message.getChatId())).collect(Collectors.toList());
        val amount = messagesStream.size();// amount of messages per chat
        if (amount > CONSTANT_MESSAGES_PER_CHAT) {
            val a = messagesStream.stream().sorted((m1, m2) -> Integer.compareUnsigned(m1.getRight().getDate(), m2.getRight().getDate())).collect(Collectors.toList()).remove(0);
            activeMessages.values().remove(a);
            Logging.IMP.info("fixed memory by " + a.getRight().getMessageId());
        }

        return message.getUniqueId();
    }

    /**
     * TODO: Rewrite
     */
    @SneakyThrows
    public void editMessageContent(final @NonNull UUID uniqueId,
                                   final @NonNull String newText,
                                   final boolean keepMarkup) {
        if (!activeMessages.containsKey(uniqueId)) {
            throw new IllegalStateException("This message does not exist");
        }

        val oldInstance = activeMessages.get(uniqueId).getRight();
        val editShit = new EditMessageText();
        editShit.setChatId(oldInstance.getChatId());
        editShit.setMessageId(oldInstance.getMessageId());
        editShit.setText(newText);
        if (keepMarkup) {
            editShit.setReplyMarkup(oldInstance.getReplyMarkup());
        }
        bot.execute(editShit);
    }

    // TODO: Update internal markup (in smartmessage instance)
    @SneakyThrows
    public void editMessageMarkup(final @NonNull UUID uniqueId,
                                  final @NonNull InlineKeyboardMarkup newInstance) {
        if (!activeMessages.containsKey(uniqueId)) {
            throw new IllegalStateException("This message does not exist");
        }

        val oldInstance = activeMessages.get(uniqueId).getRight();

        val editMsg = new EditMessageReplyMarkup();
        editMsg.setChatId(oldInstance.getChatId());
        editMsg.setMessageId(oldInstance.getMessageId());
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
                    message.getLeft().getActionExecutorMap().get(data).getActionExecutor().handleActionExecution(update, this, message.getLeft());
                }
            }
        }
    }
}
