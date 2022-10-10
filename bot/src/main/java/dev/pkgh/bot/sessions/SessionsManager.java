package dev.pkgh.bot.sessions;

import dev.pkgh.sdk.adapter.UpdateAdapter;
import dev.pkgh.sdk.utils.MessageUtil;
import lombok.NonNull;
import lombok.val;
import org.checkerframework.checker.builder.qual.NotCalledMethods;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * @author winston
 */
public final class SessionsManager implements UpdateAdapter {

    // $ static instance
    public static final SessionsManager IMP = new SessionsManager();

    /** List of active sessions */
    Collection<Session> sessions = new HashSet<>();

    /**
     * Invoked on received update in bot core.
     *
     * @param update    received update
     */
    @Override public void handle(@NotNull Update update) {

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("cancel_session_")) {
            val sessionToCancel = update.getCallbackQuery().getData().replaceFirst("cancel_session_", "");
            val session = getSession(update.getCallbackQuery().getFrom().getId()).orElse(null);
            if (session == null) {
                return; // silently dropping
            }

            if (session.getSystemName().equals(sessionToCancel)) {
                stopSession(session);
                MessageUtil.sendMessage(update.getCallbackQuery().getMessage().getChat(), "А все ...");
            }

            return;
        }
        // Here we will check, if update has message.
        // If so, we process it.

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        for (val session : sessions) {
            if (session.getUser().getId().equals(update.getMessage().getFrom().getId())
                    && session.getChat().getId().equals(update.getMessage().getChatId())) {
                session.handle(update);
            }
        }
    }

    /**
     * Get optional of first user's session
     * TODO: Multi-session handling
     *
     * @param userId    id of user
     * @return          optional of first user's session
     */
    public @NotNull Optional<Session> getSession(final long userId) {
        return sessions.stream()
                .filter(session -> session.getUser().getId() == userId)
                .findFirst();
    }

    /**
     * Checks if user has any session running
     *
     * @param userId    id of user
     * @return          yeah or nah??!?!
     */
    public boolean hasActiveSession(final long userId) {
        return getSession(userId).isPresent();
    }

    /**
     * Stops and removes session from running sessions.
     * @param session   session that we are stopping
     */
    public void stopSession(final @NonNull Session session) {
        if (!sessions.contains(session)) {
            throw new IllegalStateException("Session is not running");
        }

        sessions.remove(session);

        session.end();
    }

    /**
     * Adds session to running sessions collection and begins it.
     * @param session   session that we are starting
     */
    public void startSession(final @NonNull Session session) {
        if (sessions.contains(session)) {
            throw new IllegalStateException("Session is already running");
        }

        sessions.add(session);

        session.begin();
    }
}
