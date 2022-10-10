package dev.pkgh.bot.core;

import dev.pkgh.bot.adapter.CommandInvokerAdapter;
import dev.pkgh.bot.commands.*;
import dev.pkgh.bot.sessions.SessionsManager;
import dev.pkgh.sdk.commands.CommandManager;
import dev.pkgh.sdk.logging.Logging;
import dev.pkgh.sdk.messages.SmartManager;
import dev.pkgh.sdk.type.BotCredentials;
import dev.pkgh.sdk.utils.Checker;
import dev.pkgh.sdk.utils.ClassStreamUtil;
import dev.pkgh.sdk.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.UpdatesHandler;

/**
 * One-time instantiable primary class of Telegram bot
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class PKGHBot extends TelegramLongPollingBot implements UpdatesHandler {

    // region fields

    /** Credentials of the bot */
    BotCredentials credentials;

    /** Command manager */
    CommandManager commandManager;

    /** Update receiver for commands */
    CommandInvokerAdapter commandHandler;

    /** Manager for SmartMessages :tm: */
    SmartManager smartManager;

    // endregion

    /** Invoked after <init> */
    public void postInit() {
        val properties = ClassStreamUtil.loadFromResources("configuration.properties");

        // Validating properties file
        Checker.nonNull(properties, "properties");

        this.credentials = BotCredentials.getByProperties(properties);

        this.commandManager = new CommandManager(this);
        commandManager.addCommand(TestCommand.class);
        commandManager.addCommand(RankCommand.class);
        commandManager.addCommand(SettingsCommand.class, new SettingsCommand(this));
        commandManager.addCommand(SetGroupCommand.class);
        commandManager.addCommand(SetPropertyCommand.class);
        commandManager.addCommand(StartCommand.class);
        commandManager.addCommand(ScheduleCommand.class, new ScheduleCommand(this));

        this.commandHandler = new CommandInvokerAdapter(this);
        this.smartManager = new SmartManager(this);

        // TODO: Rework instantiation method
        MessageUtil.instantiate(this);

        Logging.IMP.info("Bot is running now!");
    }

    // region impls

    @Override
    public String getBotUsername() {
        return credentials.getUsername();
    }

    @Override
    public String getBotToken() {
        return credentials.getToken();
    }

    @Override
    public void onUpdateReceived(final @NotNull Update update) {
        Logging.IMP.info("Received an update: #{} | CB: {} | MSG: {}",
                update.getUpdateId(),
                update.hasCallbackQuery(),
                update.hasMessage());

        // Handles SmartMessages :tm:
        smartManager.handle(update);

        // Handling stuff for sessions
        SessionsManager.IMP.handle(update);

        // Handling CommandManager update
        commandHandler.handle(update);
    }

    // endregion
}
