package dev.pkgh.bot.users;

import com.google.common.collect.Lists;
import dev.pkgh.bot.data.UsersSqlHandler;
import dev.pkgh.bot.type.Setting;
import dev.pkgh.sdk.logging.Logging;
import lombok.*;
import lombok.experimental.FieldDefaults;
import xyz.winston.tcommons.map.IndexMap;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** Container for user properties */
    @Getter final Map<String, Integer> properties = new HashMap<>();

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

    // region properties

    public void setProperty(final @Nonnull String key, final int value) {
        if (properties.containsKey(key)) {
            properties.replace(key, value);
        } else {
            properties.put(key, value);
        }
    }

    public int getProperty(final @NonNull String property) {
        return properties.getOrDefault(property, 0);
    }

    // endregion

    /**
     * Gets list of user settings
     * @return  list of user's enabled settings
     */
    public List<Setting> getSettings() {
        return Lists.newArrayList(Setting.getSettings(properties.getOrDefault("settings", 0)));
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
