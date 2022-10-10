package dev.pkgh.sdk.commands;

import com.google.common.collect.Lists;
import dev.pkgh.sdk.commands.execution.*;
import dev.pkgh.sdk.type.UserPermission;
import dev.pkgh.sdk.utils.MetafactoryBuilder;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.glassfish.grizzly.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class CommandManager {

    /**
     * Registered commands map
     * <ALIAS> - <WRAPPER>
     */
    Map<String, ICommand> commands = new ConcurrentHashMap<>();

    /** Bot instance */
    TelegramLongPollingBot bot;

    public void addCommand(final @NonNull Class<?> clazz) {
        addCommand(clazz, null);
    }
    /**
     * Adds command
     *
     * @param clazz     command class
     * @param instance  command instance (null, if command is static-marked)
     */
    public void addCommand(final @NonNull Class<?> clazz,
                           final @Nullable Object instance) {

        if (!clazz.isAnnotationPresent(NamedCommand.class)) {
            throw new IllegalStateException("Command must be annotated with @NamedCommand");
        }

        val command = clazz.getDeclaredAnnotation(NamedCommand.class);
        val description = clazz.isAnnotationPresent(Description.class)
                ? clazz.getAnnotation(Description.class).value()
                : "Unspecified";

        val permission = clazz.isAnnotationPresent(WithPermission.class)
                ? clazz.getAnnotation(WithPermission.class).value()
                : UserPermission.USER;

        val commandName = command.value();
        val commandAliases = getAliases(clazz);

        val wrapper = new CommandWrapper(this, commandName, commandAliases, description, permission);

        for (val method : clazz.getMethods()) {
            try {
                for (val type : CommandType.values()) {
                    if (method.isAnnotationPresent(type.cls)) {
                        type.apply(wrapper, instance, method);
                    }
                }
            } catch (final Exception e) {
                System.out.println("Error occurred until adding a command: [Method: " + method.getName() + "; Class: " + clazz + "]");

                e.printStackTrace();
            }
        }

        registerCommand(commandName, wrapper);

        for (val alias : wrapper.getAliases()) {
            registerCommand(alias, wrapper);
        }

    }

    private static WrappedCommandExecutor createCommandExecutor(CommandWrapper wrapper,
                                                                Object instance,
                                                                Method method) {
        return new WrappedCommandExecutor(
                new CommandParams(wrapper, method, createExecutor(instance, method))
        );
    }

    public ExecutionSession newCommandSession(
            final @NonNull WrappedCommandExecutor executor,
            final @NonNull Chat channel,
            final @NonNull User sender,
            final @NonNull List<String> args,
            final @NonNull Update update
    ) {
        return new ExecutionSession(executor, sender, channel, update, args);
    }

    private static WrappedSubcommandExecutor createSubcommandExecutor(
            final @NonNull CommandWrapper wrapper,
            final @Nullable Object instance,
            final @NonNull Method method) {
        val subcommandExecutor = method.getDeclaredAnnotation(SubcommandExecutor.class);

        val description = method.isAnnotationPresent(Description.class) ?
                                method.getDeclaredAnnotation(Description.class).value() :
                                "Unspecified.";

        val aliases = getAliases(method);
        val names = new String[1 + aliases.length];
        names[0] = subcommandExecutor.value();

        System.arraycopy(aliases, 0, names, 1, aliases.length);

        return new WrappedSubcommandExecutor(
                names, description, new CommandParams(wrapper, method, createExecutor(instance, method))
        );
    }

    // TODO: Optimization
    public void executeCommand(final @NonNull User sender,
                               final @NonNull Chat channel,
                               final @NonNull String message,
                               final @NonNull Update update) {
        val rawCmd = message.split(" ")[0].replaceFirst("/", "");
        val command = getCommand(rawCmd);
        val args = Lists.newArrayList(ArrayUtils.remove(message.split(" "), 0));

        if (command != null) {
            command.execute(channel, sender, args, update);
        }

    }

    /**
     * Get command wrapper by its name / alias
     *
     * @param name      command name / alias
     *
     * @return          command wrapper instance or {@code null} if not registered
     */
    public @Nullable ICommand getCommand(final @NonNull String name) {
        return commands.get(name.toLowerCase());
    }

    /**
     * Registering a command by name and wrapper
     *
     * @param name      command name
     * @param command   command wrapper
     */
    public void registerCommand(final @NonNull String name,
                                final @NonNull ICommand command) {
        commands.put(name.toLowerCase(), command);
    }

    /**
     * Get aliases for command
     *
     * @param element   le command class
     *
     * @return          array of aliases
     */
    private static String[] getAliases(final @NonNull AnnotatedElement element) {
        var totalAliases = new ArrayList<String>();

        var withAlias = element.getDeclaredAnnotation(WithAlias.class);

        if (withAlias != null) {
            totalAliases.add(withAlias.value());
        }

        return totalAliases.toArray(String[]::new);
    }

    @SuppressWarnings("unchecked")
    private static CommandExecutor<ExecutionSession> createExecutor(final @Nullable Object object,
                                                                    final @NotNull Method method) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());

        if (!isStatic && object == null) {
            throw new IllegalStateException("Can't get invoker for non-static method without instance");
        }

        return isStatic
                ? new MetafactoryBuilder<>(CommandExecutor.class).target(method).buildLambda()
                : new MetafactoryBuilder<>(CommandExecutor.class).target(method).buildLambda(object);
    }

    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    @RequiredArgsConstructor
    public enum CommandType {

        MAIN(ExecutorMethod.class) {
            @Override
            public void apply(CommandWrapper wrapper, Object instance, Method method) {
                wrapper.setExecutor(createCommandExecutor(wrapper, instance, method));
            }
        },
        SUB(SubcommandExecutor.class) {
            @Override
            public void apply(CommandWrapper wrapper, Object instance, Method method) {
                wrapper.addSubcommand(createSubcommandExecutor(wrapper, instance, method));
            }
        };

        Class<? extends Annotation> cls;

        public abstract void apply(CommandWrapper wrapper, Object instance, Method method);

    }

}
