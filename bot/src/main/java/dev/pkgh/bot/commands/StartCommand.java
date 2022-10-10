package dev.pkgh.bot.commands;

import dev.pkgh.bot.util.BotUtils;
import dev.pkgh.bot.util.KeyboardUtil;
import dev.pkgh.sdk.commands.NamedCommand;
import dev.pkgh.sdk.commands.WithAlias;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.ExecutorMethod;
import dev.pkgh.sdk.utils.MessageUtil;

/**
 * Primary bot command. Executed when user starts the bot.
 * (in most cases)
 *
 * @author winston
 */
@WithAlias("начать")
@NamedCommand("start")
public final class StartCommand {

    @ExecutorMethod
    public static void execute(ExecutionSession session) {
        BotUtils.handleStart(session.getTextChannel());
    }

}
