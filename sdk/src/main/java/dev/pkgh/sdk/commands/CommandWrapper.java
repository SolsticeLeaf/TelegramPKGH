package dev.pkgh.sdk.commands;

import com.google.common.collect.Lists;
import dev.pkgh.sdk.logging.Logging;
import dev.pkgh.sdk.type.IBotUser;
import dev.pkgh.sdk.type.UserPermission;
import dev.pkgh.sdk.utils.MessageUtil;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.glassfish.grizzly.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.pkgh.sdk.commands.exception.CommandMessageException;
import dev.pkgh.sdk.commands.execution.CommandExecutor;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.WrappedCommandExecutor;
import dev.pkgh.sdk.commands.execution.WrappedSubcommandExecutor;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString(exclude = "commandManager")
@RequiredArgsConstructor
@Getter
public final class CommandWrapper implements ICommand {

    CommandManager commandManager;

    String name;

    String[] aliases;

    String description;

    UserPermission permission;

    @NonFinal @Setter CommandExecutor<ExecutionSession> prepareSession;

    @NonFinal WrappedCommandExecutor executor;

    final Map<String, WrappedSubcommandExecutor> subcommands = new LinkedHashMap<>();

    public void setExecutor(final @NonNull WrappedCommandExecutor executor) {
        this.executor = executor;
    }

    public void addSubcommand(final @NonNull WrappedSubcommandExecutor executor) {
        for (val name : executor.getNames()) {
            subcommands.put(name.toLowerCase(), executor);
        }
    }

    private boolean doChecked(final @NonNull CommandExecutor<ExecutionSession> sessionConsumer,
                              final @NonNull ExecutionSession session) throws Exception {
        try {
            sessionConsumer.execute(session);

            return false;
        } catch (CommandMessageException e) {
            e.printMessages(session);
            return true;
        }
    }

    public boolean hasExecutor() {
        return executor != null;
    }

    public WrappedCommandExecutor getExecutor(final @NonNull String subcommandName) {
        if (subcommands.size() == 0) {
            return executor;
        }

        var split = subcommandName.split(" ");
        if (split.length == 1) {
            return executor;
        }

        WrappedCommandExecutor executor = subcommands.get(split[1].toLowerCase());

        if (executor == null) {
            executor = this.executor;
        }

        return executor;
    }

    public void run(final @NonNull IBotUser user,
                    final @NonNull User sender,
                    final @NonNull Chat channel,
                    final @NonNull Update interaction,
                    final @NonNull List<String> args,
                    final @Nullable WrappedCommandExecutor executor) {
        try {
            if (user.getPermission() == UserPermission.USER && permission == UserPermission.ADMINISTRATOR) {
                MessageUtil.sendMessage(channel, "У вас нет прав на выполнение этой команды :(");
                return;
            }

            if (executor == null) {
                Logging.IMP.error("No executor found for " + name);
                MessageUtil.sendMessage(channel, "кинсе памаги кинсе памаги кинсе памрги");
                return;
            }

            var session = commandManager.newCommandSession(executor, channel, sender, args, interaction);

            if (prepareSession != null) {
                doChecked(prepareSession, session);
            }

            doChecked(executor.getExecutor(), session);
        } catch (Throwable t) {
            t.printStackTrace();
            MessageUtil.sendMessage(channel, "Произошла ошибка во время выполнения команды. Обратитесь к администратору");
        }
    }

    @Override
    public void execute(final @NonNull IBotUser user, @NotNull Chat channel, @NotNull User sender, @NotNull List<String> args, @NotNull Update update) {
        var executor = getExecutor(update.getMessage().getText().replaceAll("/", ""));

        args.remove(0); // removing command base
        if (executor instanceof WrappedSubcommandExecutor) {
            args.remove(0); // removing subcommand base
        }

        var threadExecutor = executor == null ? null : executor.getThreadExecutor();

        @NotNull List<String> finalArgs = args; // thanks java 17 (fuck it)
        val runnable = (Runnable) () -> run(user, sender, channel, update, finalArgs, executor);

        if (threadExecutor == null) {
            runnable.run();
        } else {
            threadExecutor.submit(runnable);
        }
    }
}