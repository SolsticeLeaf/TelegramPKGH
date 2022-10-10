package dev.pkgh.sdk.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import dev.pkgh.sdk.commands.execution.CommandExecutor;
import dev.pkgh.sdk.commands.execution.ExecutionSession;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CommandParams {

    int methodHash;
    CommandWrapper wrapper;
    ExecutorService threadExecutor;

    @Getter CommandExecutor<ExecutionSession> executor;

    public CommandParams(final @NonNull CommandWrapper wrapper,
                         final int hash,
                         final @NonNull CommandExecutor<ExecutionSession> executor) {
        this(wrapper, null, hash, executor);
    }

    public CommandParams(final CommandWrapper wrapper,
                         final ExecutorService threadExecutor,
                         final int methodHash,
                         final CommandExecutor<ExecutionSession> executor) {
        this.wrapper = wrapper;
        this.methodHash = methodHash;
        this.threadExecutor = threadExecutor;
        this.executor = executor;
    }

    public CommandParams(final @NonNull CommandWrapper wrapper,
                         final @NonNull Method method,
                         final @NonNull CommandExecutor<ExecutionSession> executor) {
        this.wrapper = wrapper;
        this.methodHash = method.getName().hashCode();
        this.threadExecutor = getThreadExecutor(method);
        this.executor = executor;
    }

    public int getMethodHash() {
        return methodHash;
    }

    public CommandWrapper getWrapper() {
        return wrapper;
    }

    public ExecutorService getThreadExecutor() {
        return threadExecutor;
    }

    private ExecutorService getThreadExecutor(Method method) {
        return method.isAnnotationPresent(AsyncExecution.class)
                ? ForkJoinPool.commonPool()
                : null;
    }


}
