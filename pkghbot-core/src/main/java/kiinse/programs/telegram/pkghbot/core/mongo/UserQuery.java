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

import com.mongodb.client.MongoCollection;
import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.data.enums.UserStatus;
import kiinse.programs.telegram.pkghbot.core.mongo.data.BotUser;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class UserQuery {

    private static final MongoCollection<Document> collection = MongoDb.getDataBase().getCollection("users");

    private UserQuery() {}

    public static @NotNull Document userToDocument(@NotNull User user) {
        var query = new Document();
        query.put("_id", user.getChatId());
        query.put("name", user.getName());
        query.put("group", user.getGroup());
        query.put("status", user.getStatus().toString());
        query.put("isMailing", user.isMailing());
        query.put("isLessonMailing", user.isLessonMailing());
        query.put("isAdminMailing", user.isAdminMailing());
        query.put("isAdmin", user.isAdmin());
        query.put("isGroup", user.isGroup());
        query.put("registration", user.getRegistrationDate());
        return query;
    }

    public static void createUser(@NotNull User user) {
        collection.insertOne(userToDocument(user));
    }

    public static boolean hasUser(@NotNull User user) {
        var query = new Document();
        query.put("_id", user.getChatId());
        var result = collection.find(query).first();
        return result != null && !result.isEmpty();
    }

    public static boolean hasUser(long chat) {
        var query = new Document();
        query.put("_id", chat);
        var result = collection.find(query).first();
        return result != null && !result.isEmpty();
    }

    public static User updateUser(@NotNull User user) {
        var query = new Document();
        query.put("_id", user.getChatId());
        collection.findOneAndReplace(query, userToDocument(user));
        return user;
    }

    public static @Nullable User getUser(@NotNull String name) {
        var query = new Document();
        query.put("name", name);
        return parseUserResult(collection.find(query).first());

    }

    public static @Nullable User getUser(long chatId) {
        var query = new Document();
        query.put("_id", chatId);
        return parseUserResult(collection.find(query).first());
    }

    private static @Nullable User parseUserResult(@Nullable Document result) {
        if (result == null || result.keySet().size() < 3) {
            return null;
        }
        return new BotUser()
                .setName(String.valueOf(result.get("name")))
                .setGroup(String.valueOf(result.get("group")))
                .setChatId(Long.parseLong(String.valueOf(result.get("_id"))))
                .setStatus(UserStatus.valueOf(String.valueOf(result.get("status"))))
                .setMailing(Boolean.parseBoolean(String.valueOf(result.get("isMailing"))))
                .setLessonMailing(Boolean.parseBoolean(String.valueOf(result.get("isLessonMailing"))))
                .setAdminMailing(Boolean.parseBoolean(String.valueOf(result.get("isAdminMailing"))))
                .setAdmin(Boolean.parseBoolean(String.valueOf(result.get("isAdmin"))))
                .setGroupChat(Boolean.parseBoolean(String.valueOf(result.get("isGroup"))))
                .setRegistrationDate(String.valueOf(result.get("registration")));
    }

    public static void removeUser(@NotNull User user) {
        var query = new Document();
        query.put("_id", user.getChatId());
        collection.findOneAndDelete(query);
    }

    public static long countChats() {
        return collection.countDocuments();
    }

    public static long countActiveChats() {
        var query = new Document();
        query.put("status", UserStatus.ACTIVE);
        return collection.countDocuments(query);
    }

    public static long countBlockedChats() {
        var query = new Document();
        query.put("status", UserStatus.BOT_BLOCKED);
        return collection.countDocuments(query);
    }

    public static long countGroupChats() {
        var query = new Document();
        query.put("isGroup", true);
        return collection.countDocuments(query);
    }

    public static long countAdmins() {
        var query = new Document();
        query.put("isAdmin", true);
        return collection.countDocuments(query);
    }

    public static long countAutoMailing() {
        var query = new Document();
        query.put("isMailing", true);
        return collection.countDocuments(query);
    }

    public static long countLessonMailing() {
        var query = new Document();
        query.put("isLessonMailing", true);
        return collection.countDocuments(query);
    }

    public static @NotNull StringBuilder adminList() {
        var msg = new StringBuilder("▬▬▬ Админы ▬▬▬");
        for (var user : getAdmins()) {
            msg.append("\n")
               .append(user.getChatId())
               .append(" | ")
               .append(user.getGroup())
               .append(" | ")
               .append(user.getName());
        }
        return msg;
    }

    public static @NotNull Set<User> getAdmins() {
        var query = new Document();
        query.put("isAdmin", true);
        return parseUsersSet(query);
    }

    public static @NotNull Set<User> getAllUsers() {
        var result = new HashSet<User>();
        for (var object : collection.find()) {
            result.add(parseUserResult(object));
        }
        return result;
    }

    public static @NotNull Set<User> getActiveUsers() {
        var query = new Document();
        query.put("status", UserStatus.ACTIVE);
        return parseUsersSet(query);
    }

    public static @NotNull Set<User> getBlockedUsers() {
        var query = new Document();
        query.put("status", UserStatus.BANNED);
        return parseUsersSet(query);
    }

    public static @NotNull Set<User> getBannedUsers() {
        var query = new Document();
        query.put("status", UserStatus.BOT_BLOCKED);
        return parseUsersSet(query);
    }

    private static @NotNull Set<User> parseUsersSet(@NotNull Document query) {
        var result = new HashSet<User>();
        for (var object : collection.find(query)) {
            result.add(parseUserResult(object));
        }
        return result;
    }
}
