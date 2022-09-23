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

package kiinse.programs.telegram.pkghbot.core.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import kiinse.programs.telegram.pkghbot.api.mongo.Mongo;
import kiinse.programs.telegram.pkghbot.api.mongo.MongoSettings;
import kiinse.programs.telegram.pkghbot.api.utils.LoggerUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mongodb.client.MongoClients.create;

public class MongoDb implements Mongo {

    private static MongoDatabase dataBase;

    private static MongoSettings settings;

    public MongoDb(@NotNull MongoSettings settings) {
        MongoDb.settings = settings;
    }

    public static @NotNull MongoSettings getSettings() {
        return settings;
    }

    public static @NotNull MongoDatabase getDataBase() {
        return dataBase;
    }

    @Override
    public void connect() throws MongoException {
        LoggerUtils.getLogger().info("Getting database...");
        MongoCredential credential = MongoCredential.createCredential(settings.getLogin(),
                                                                      settings.getAuthDb(),
                                                                      settings.getPassword().toCharArray());
        try {
            var mongo = create(MongoClientSettings.builder()
                                                  .applyToClusterSettings(builder -> builder.hosts(List.of(new ServerAddress(
                                                          settings.getHost(),
                                                          Integer.parseInt(settings.getPort())))))
                                                  .credential(credential)
                                                  .build());
            dataBase = mongo.getDatabase(settings.getDbName());
            createCollectionIfNotExists("users");
            createCollectionIfNotExists("posts");
            createCollectionIfNotExists("messages");
            LoggerUtils.getLogger().info("Getting database... OK");
        } catch (Exception e) {
            LoggerUtils.getLogger().info("Getting database... ERR");
            throw new MongoException(e.getMessage());
        }
    }

    private void createCollectionIfNotExists(@NotNull String collection) {
        for (var name : dataBase.listCollectionNames()) {
            if (name.equals(collection)) {
                LoggerUtils.getLogger().info("Collection '{}' exists", collection);
                return;
            }
        }
        dataBase.createCollection(collection);
        LoggerUtils.getLogger().info("Collection '{}' created!", collection);
    }
}
