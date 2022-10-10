package dev.pkgh.sdk.messages;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.graalvm.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Extendable message, that can be
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@SuppressWarnings("ALL")
@Getter @Setter
public final class SmartMessage {

    /** Unique Identificator of message */
    UUID uniqueId;

    /** Map, that contains "callback" -> "Action executor" */
    Map<String, Pair<String, ActionExecutor>> actionExecutorMap;

    /** Text of message */
    String messageText;

    /** Chat ID */
    long chatId;

    /** Is markdown enabled for this message */
    boolean markdown;

    // NOTE: This shit is initialized once message is built.
    InlineKeyboardMarkup keyboardMarkup;

    /**
     * Creates SendMessage query from this object
     * @return  SendMessage query
     */
    public @NotNull SendMessage bakeMessage() {
        val sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        sendMessage.setText(messageText);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }

    /**
     * Creates builder class
     */
    public static SmartMessageBuilder bakeBuilder() {
        return new SmartMessageBuilder();
    }

    /**
     * @author winston
     */
    public abstract static class ActionExecutorImpl extends ActionExecutor {

    }

    /**
     * @author winston
     */
    @FieldDefaults(level = AccessLevel.PROTECTED)
    @Setter
    public abstract static class ActionExecutor {

        public abstract void handleActionExecution(final @NonNull Update update,
                                                   final @NonNull SmartManager invokerManager,
                                                   final @NonNull SmartMessage selfInstance);

        /**
         * Change text on some button
         */
        protected final void editButtonText(final @NonNull SmartMessage message,
                                            final @NonNull SmartManager manager,
                                            final int buttonNumber,
                                            final @NonNull String newText) {
            val markup = message.getKeyboardMarkup();

            val filledShit = new ConcurrentHashMap<Integer, Map<Integer, KeyboardButton>>();
            int i = 0;
            int j = 0;
            for (val line : markup.getKeyboard()) {
                for (val button : line.toArray(KeyboardButton[]::new)) {
                    filledShit.put(j, Map.of(i, button));
                    j++;
                }
                i++;
            }

            filledShit.get(buttonNumber).get(0);

            manager.updateMessageMarkup(message.getUniqueId(), markup);
        }

        /**
         * When invoked, removes the message.
         */
        protected final void destroy(final @NonNull SmartManager invokerManager,
                                     final @NonNull SmartMessage selfInstance) {
            invokerManager.destroyMessages(selfInstance.getUniqueId());
        }

    }

    /**
     * Builder class for this shit
     */
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    public static class SmartMessageBuilder {

        String messageText;

        long chatId;

        boolean markdown;

        final Map<String, Pair<String, ActionExecutor>> actionExecutorMap = new HashMap<>();

        public SmartMessageBuilder withChatId(final long chatId) {
            this.chatId = chatId;
            return this;
        }

        public SmartMessageBuilder enableMarkdown(final boolean markup) {
            this.markdown = markup;
            return this;
        }

        public SmartMessageBuilder withText(final @NonNull String text) {
            this.messageText = text;
            return this;
        }

        public SmartMessageBuilder addButton(final @NonNull String buttonName,
                                             final @NonNull String callback,
                                             final @NonNull ActionExecutor executor) {
            actionExecutorMap.put(callback, Pair.create(buttonName, executor));
            return this;
        }

        public SmartMessage build() {

            val replyMarkup = new InlineKeyboardMarkup();
            val rows = new ArrayList<List<InlineKeyboardButton>>();

            for (val actions : actionExecutorMap.entrySet()) {
                val callbackData = actions.getKey();
                val buttonText = actions.getValue().getLeft();

                val button = new InlineKeyboardButton(buttonText);
                button.setCallbackData(callbackData);

                rows.add(Collections.singletonList(button));
            }

            // Idk why this shit happens, but it's required
            Collections.reverse(rows);

            replyMarkup.setKeyboard(rows);

            return new SmartMessage(UUID.randomUUID(), actionExecutorMap, messageText, chatId, markdown, replyMarkup);
        }

    }

}
