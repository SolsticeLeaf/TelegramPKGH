/*
 * MIT License
 *
 * Copyright (c) 2022 ⭕️⃤ kiinse
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package kiinse.programs.telegram.pkghbot.api.mongo;

import org.jetbrains.annotations.NotNull;

public class MongoSettings {

    private String host = "localhost";

    private String port = "5432";

    private String login = "";

    private String password = "";

    private String dbName = "pkghbot";

    private String authDb = "admin";

    public @NotNull String getHost() {
        return host;
    }

    public void setHost(@NotNull String sqlHost) throws IllegalArgumentException {
        if (sqlHost.isBlank()) {
            throw new IllegalArgumentException("Host is empty");
        }
        this.host = sqlHost;
    }

    public @NotNull String getPort() {
        return port;
    }

    public void setPort(@NotNull String sqlPort) throws IllegalArgumentException {
        if (sqlPort.isBlank()) {
            throw new IllegalArgumentException("Port is empty");
        }
        this.port = sqlPort;
    }

    public @NotNull String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String sqlLogin) throws IllegalArgumentException {
        if (sqlLogin.isBlank()) {
            throw new IllegalArgumentException("Login is empty");
        }
        this.login = sqlLogin;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String sqlPassword) throws IllegalArgumentException {
        if (sqlPassword.isBlank()) {
            throw new IllegalArgumentException("Password is empty");
        }
        this.password = sqlPassword;
    }

    public @NotNull String getDbName() {
        return dbName;
    }

    public void setDbName(@NotNull String sqldbName) throws IllegalArgumentException {
        if (sqldbName.isBlank()) {
            throw new IllegalArgumentException("Database name is empty");
        }
        this.dbName = sqldbName;
    }

    public @NotNull String getAuthDb() {
        return authDb;
    }

    public void setAuthDb(@NotNull String authDb) throws IllegalArgumentException {
        if (authDb.isBlank()) {
            throw new IllegalArgumentException("Database name is empty");
        }
        this.authDb = authDb;
    }
}
