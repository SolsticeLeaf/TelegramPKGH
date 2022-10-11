package dev.pkgh.bot.commands;

import dev.pkgh.bot.core.PKGHBot;
import dev.pkgh.bot.type.Setting;
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
import org.jetbrains.annotations.NotNull;
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

    /** Pattern for "settings" message */
    String informationPattern =
            "⚙️Настройки: \n" +
            "Ваш ID: %d\n" +            // id of user
            "Ваш статус: %s\n" +        // permissions etc
            "Ваша группа: %s\n" +       // user's group in pkgh
            "Авто-рассылка: %s\n\n" +   // auto mailing;
            "Для изменений настроек, используйте кнопки под этим сообщением";

    @ExecutorMethod
    public void execute(final ExecutionSession session) {
        val botUser = BotUser.getOrCreate(session.getSender().getId());

        bot.getSmartManager().publishMessages(SmartMessage.bakeBuilder()
                .withText(getMessageForUser(botUser))
                .enableMarkdown(true)
                .withChatId(session.getTextChannel().getId())
                .addButton("Рассылка " + (botUser.isSettingEnabled(Setting.NOTIFICATOR) ? "\uD83D\uDFE2" : "\uD83D\uDD34"), "settings_test1", new SmartMessage.ActionExecutorImpl() {
                    @Override
                    public void handleActionExecution(@NonNull Update update, @NonNull SmartManager invokerManager, @NonNull SmartMessage selfInstance) {
                    val shit = botUser.toggleSetting(Setting.NOTIFICATOR);

                    editMessageText(selfInstance, invokerManager, getMessageForUser(botUser), true);
                    editMarkup(selfInstance, invokerManager, 0,
                            "Рассылка " + (shit ? "\uD83D\uDFE2" : "\uD83D\uDD34"));
                    }
                })
                .addButton("Готово ✅", "settings_close", new SmartMessage.ActionExecutor() {
                    @Override
                    public void handleActionExecution(@NonNull Update update, @NonNull SmartManager invokerManager, @NonNull SmartMessage selfInstance) {
                    destroy(invokerManager, selfInstance);
                    }
                })
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

    /**
     * Returns formatted pattern of user information
     * @param user  the user we are getting pattern for
     * @return      formatted pattern
     */
    private @NotNull String getMessageForUser(final @NonNull BotUser user) {
        return String.format(informationPattern,
                user.getId(),
                user.getProperty("admin") == 1 ? "Администратор" : "Пользователь",
                user.getGroup() == null ? "Не установлена" : user.getGroup(),
                user.getSettings().contains(Setting.NOTIFICATOR) ? "Включена" : "Выключена");
    }

}
