package dev.pkgh.bot.commands;

import dev.pkgh.bot.core.PKGHBot;
import dev.pkgh.bot.users.BotUser;
import dev.pkgh.sdk.commands.NamedCommand;
import dev.pkgh.sdk.commands.WithAlias;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.ExecutorMethod;
import dev.pkgh.sdk.messages.SmartManager;
import dev.pkgh.sdk.messages.SmartMessage;
import dev.pkgh.sdk.utils.MessageUtil;
import dev.pkgh.sdk.utils.TimeUtil;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;

/**
 * Primary settings command
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@WithAlias("Настройки")
@NamedCommand("settings")
public final class SettingsCommand {

    /** Core bot instance */
    PKGHBot bot;

    @ExecutorMethod
    public void execute(final ExecutionSession session) {
        val botUser = BotUser.getOrCreate(session.getSender().getId());

        bot.getSmartManager().publishMessages(SmartMessage.bakeBuilder()
                .withText("Информация о пользователе:\n" +
                        "Ваш ID: `" + botUser.getId() + "`\n" +
                        "Дата регистрации в боте: `" + botUser.getRegistrationDate().format(DateTimeFormatter.ofPattern(TimeUtil.STANDARD_PATTERN)) + "`\n" +
                        "")
                .enableMarkdown(true)
                .withChatId(session.getTextChannel().getId())
                .addButton("Сообщение", "settings_test1", new SmartMessage.ActionExecutorImpl() {
                    @Override
                    public void handleActionExecution(@NonNull Update update, @NonNull SmartManager invokerManager, @NonNull SmartMessage selfInstance) {
                        MessageUtil.sendMessage(update.getCallbackQuery().getMessage().getChat(), "hiii");
                    }
                }) // 0
                .addButton("Удалить", "settings_test1231", new SmartMessage.ActionExecutor() {
                    @Override
                    public void handleActionExecution(@NonNull Update update, @NonNull SmartManager invokerManager, @NonNull SmartMessage selfInstance) {
                        destroy(invokerManager, selfInstance);
                    }
                }) // 1
                .addButton("Обновить Markup", "chleeeen", new SmartMessage.ActionExecutor() {
                    @Override
                    public void handleActionExecution(@NonNull Update update, @NonNull SmartManager invokerManager, @NonNull SmartMessage selfInstance) {
                        editMarkup(selfInstance, invokerManager, 2, "sex " + System.currentTimeMillis());
                    }
                }) // 2
                .addButton("Обновить текст", "text_flow", new SmartMessage.ActionExecutor() {
                    @Override
                    public void handleActionExecution(@NonNull Update update, @NonNull SmartManager invokerManager, @NonNull SmartMessage selfInstance) {
                        editMessageText(selfInstance, invokerManager, System.currentTimeMillis() + " $$$", true);
                    }
                }) // 3
                .build());

        /*
        MessageUtil.sendMessage(
                session.getTextChannel().getId().toString(),
                "Информация о пользователе:\n" +
                        "Ваш ID: `" + botUser.getId() + "`\n" +
                        "Дата регистрации в боте: `" + botUser.getRegistrationDate().format(DateTimeFormatter.ofPattern(TimeUtil.STANDARD_PATTERN)) + "`\n" +
                        "Reserved: ---",
                KeyboardUtil.bakeSettings(botUser));
         */
    }

}
