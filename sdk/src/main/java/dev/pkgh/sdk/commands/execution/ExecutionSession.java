package dev.pkgh.sdk.commands.execution;

import dev.pkgh.sdk.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Nullable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class ExecutionSession {

    @Getter
    WrappedCommandExecutor executor;

    @Getter User sender;
    @Getter Chat textChannel;
    @Getter Update update;

    @Getter List<String> arguments;

    public void sendMessage(final @NonNull String message) {
        MessageUtil.sendMessage(textChannel, message);
    }

}
