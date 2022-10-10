package dev.pkgh.sdk.type;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.Properties;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
public final class BotCredentials {

    /**
     * Token of bot
     */
    String token;

    /**
     * Bot's username
     */
    String username;

    /**
     * Returns {@link BotCredentials} instance by properties instance
     * @param properties        properties
     * @return                  bot credentials dataclass instance
     */
    public static BotCredentials getByProperties(final @NonNull Properties properties) {
        return new BotCredentials(
                properties.getProperty("bot.token"),
                properties.getProperty("bot.username")
        );
    }

}
