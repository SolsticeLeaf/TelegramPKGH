package dev.pkgh.bot.users;

import dev.pkgh.bot.data.UsersSqlHandler;
import dev.pkgh.sdk.logging.Logging;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import xyz.winston.tcommons.map.IndexMap;

import java.time.LocalDateTime;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class BotUser {

    /** Unique identification field for telegram user */
    final long id;

    /** User's group in college */
    @Setter String group;

    /** Time when user registered (NULL if dummy user) */
    @Setter LocalDateTime registrationDate;

    /** <init> stub */
    public BotUser(final long userId) {
        this.id = userId;
    }

    /**
     * Saves data to database
     */
    public void saveAsync() {
        Logging.IMP.debug("Saving user data #" + id);
        UsersSqlHandler.IMP.saveUserData(this);
    }

    // -------------------------------------------------------------------------

    // Cached user instances
    private static final IndexMap<BotUser> USERS = IndexMap.<BotUser>newMap()
            .registerIndex(Long.class, BotUser::getId); // byId

    /**
     * @deprecated Use {@link #getOrCreate(long id)} instead of this
     */
    public static BotUser getBotUser(final long id) {
        synchronized (USERS) {
            return USERS.get(Long.class, id);
        }
    }

    /**
     * Provides bot user data class instance
     *
     * @param id    id of user
     * @return      bot user instance
     */
    public static BotUser getOrCreate(final long id) {
        synchronized (USERS) {
            if (USERS.contains(Long.class, id)) {
                return USERS.get(Long.class, id);
            }
        } // synchronized drop $

        // Getting instance directly from database
        val user = UsersSqlHandler.IMP.getBotUser(id);

        // Saving to a local cache
        USERS.store(user);

        return user;
    }

}
