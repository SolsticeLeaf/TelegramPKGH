package dev.pkgh.sdk.commands.execution;

public interface CommandExecutor<Session extends ExecutionSession> {

    void execute(Session session) throws Exception;

    @SuppressWarnings("unchecked")
    default CommandExecutor<ExecutionSession> unchecked() {
        return session -> execute((Session) session);
    }

}
