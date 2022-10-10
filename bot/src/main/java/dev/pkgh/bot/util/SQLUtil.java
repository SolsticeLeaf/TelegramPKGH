package dev.pkgh.bot.util;

import dev.pkgh.sdk.utils.ClassStreamUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import xyz.winston.tcommons.databases.sql.MySQLDatabaseCredentials;
import xyz.winston.tcommons.databases.sql.SQLDatabase;
import xyz.winston.tcommons.databases.sql.SQLiteDatabaseCredentials;
import xyz.winston.tcommons.databases.sql.impl.MySQL;
import xyz.winston.tcommons.databases.sql.impl.SQLite;

/**
 * @author winston
 */
@UtilityClass
public final class SQLUtil {

    public SQLDatabase getDatabase(final @NonNull String database) {
        val props = ClassStreamUtil.loadFromResources("configuration.properties");
        val selected = props.getProperty("db.selected");
        if (selected.equalsIgnoreCase("MySQL")) {
            return new MySQL(MySQLDatabaseCredentials.makePreset(
                    props.getProperty("db.mysql.host"),
                    Integer.parseInt(props.getProperty("db.mysql.port")),
                    props.getProperty("db.mysql.user"),
                    props.getProperty("db.mysql.password")
            ).withDatabase(database));
        } else if (selected.equalsIgnoreCase("SQLite")) {
            return new SQLite(SQLiteDatabaseCredentials.makePreset(props.getProperty("db.sqlite.path") + "/" + database + ".db"));
        } else {
            throw new IllegalStateException("Invalid database driver");
        }
    }

}
