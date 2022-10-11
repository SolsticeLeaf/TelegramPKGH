package dev.pkgh.bot.users;

import com.google.common.collect.Lists;
import dev.pkgh.bot.data.UsersSqlHandler;
import dev.pkgh.bot.type.Setting;
import dev.pkgh.sdk.logging.Logging;
import dev.pkgh.sdk.type.IBotUser;
import dev.pkgh.sdk.type.UserPermission;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.User;
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
public final class BotUser implements IBotUser {

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

    // region settings

    /**
     * @return new value of setting
     */
    public boolean toggleSetting(final @NonNull Setting setting) {
        if (isSettingEnabled(setting)) {
            disableSetting(setting);
            return false;
        } else {
            enableSetting(setting);
            return true;
        }
    }

    public void enableSetting(final @NonNull Setting setting) {
        if (!isSettingEnabled(setting)) {
            val $tmp = getSettings();
            $tmp.add(setting);

            setSettings($tmp);
        }
    }

    public void disableSetting(final @NonNull Setting setting) {
        if (isSettingEnabled(setting)) {
            val $tmp = getSettings();
            $tmp.remove(setting);
            setSettings($tmp);
        }
    }

    public boolean isSettingEnabled(final @NonNull Setting setting) {
        return getSettings().contains(setting);
    }

    // endregion

    // region permissions

    public boolean hasPermission(final @NonNull UserPermission permission) {
        if (permission == UserPermission.USER) return true;
        return getProperty("admin") == 1;
    }

    // endregion

    /**
     * Gets list of user settings
     * @return  list of user's enabled settings
     */
    public List<Setting> getSettings() {
        return Lists.newArrayList(Setting.getSettings(properties.getOrDefault("settings", 0)));
    }

    public void setSettings(final @NonNull List<Setting> settings) {
        setProperty("settings", Setting.createMask(settings.toArray(Setting[]::new)));
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

    @Override
    public @NotNull UserPermission getPermission() {
        return getProperty("admin") == 1
                ? UserPermission.ADMINISTRATOR
                : UserPermission.USER;
    }
}
