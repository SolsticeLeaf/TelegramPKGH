package dev.pkgh.sdk.commands.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import dev.pkgh.sdk.commands.CommandParams;
import dev.pkgh.sdk.commands.CommandWrapper;

import java.util.concurrent.ExecutorService;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class WrappedCommandExecutor {

    int methodHash;

    CommandWrapper wrapper;
    ExecutorService threadExecutor;
    CommandExecutor<ExecutionSession> executor;

    public WrappedCommandExecutor(@NonNull CommandParams params) {
        this.methodHash = params.getMethodHash();
        this.wrapper = params.getWrapper();
        this.threadExecutor = params.getThreadExecutor();
        this.executor = params.getExecutor();
    }

    @Override
    public String toString() {
        return "/" + wrapper.getName();
    }

}
