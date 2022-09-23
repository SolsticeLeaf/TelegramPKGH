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
import kiinse.programs.telegram.pkghbot.api.messages.Message;
import kiinse.programs.telegram.pkghbot.api.messages.enums.MessageStatus;
import kiinse.programs.telegram.pkghbot.api.messages.enums.MessageType;
import kiinse.programs.telegram.pkghbot.core.mongo.data.BotMessage;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class MessageQuery {

    private static final MongoCollection<Document> collection = MongoDb.getDataBase().getCollection("messages");

    private MessageQuery() {}

    public static @NotNull Document messageToDocument(@NotNull Message message) {
        var query = new Document();
        query.put("_id", message.getId().toString());
        query.put("user", message.getUser() != null ? message.getUser().getChatId() : "To all");
        query.put("text", message.getText());
        query.put("status", message.getStatus().toString());
        query.put("type", message.getType().toString());
        query.put("created", message.getCreationDate());
        return query;
    }

    private static @Nullable Message parseMessageResult(@Nullable Document result) {
        if (result == null || result.keySet().size() < 3) {
            return null;
        }
        return new BotMessage(UUID.fromString(String.valueOf(result.get("_id"))))
                .setUser(!String.valueOf(result.get("user")).equalsIgnoreCase("To all") ? Objects.requireNonNull(UserQuery.getUser(Long.parseLong(String.valueOf(result.get("user"))))) : null)
                .setText(String.valueOf(result.get("text")))
                .setStatus(MessageStatus.valueOf(String.valueOf(result.get("status"))))
                .setType(MessageType.valueOf(String.valueOf(result.get("type"))))
                .setCreationDate(String.valueOf(result.get("created")));
    }

    public static boolean hasMessage(@NotNull UUID id) {
        var query = new Document();
        query.put("_id", id.toString());
        var result = collection.find(query).first();
        return result != null && !result.isEmpty();
    }

    public static @NotNull Message createMessage(@NotNull Message post) {
        collection.insertOne(messageToDocument(post));
        return post;
    }

    public static @Nullable Message getMessage(@NotNull UUID id) {
        var query = new Document();
        query.put("_id", id.toString());
        return parseMessageResult(collection.find(query).first());
    }

    public static @NotNull Message updateMessage(@NotNull Message post) {
        var query = new Document();
        query.put("_id", post.getId().toString());
        collection.findOneAndReplace(query, messageToDocument(post));
        return post;
    }

    public static void removeMessage(@NotNull UUID id) {
        var query = new Document();
        query.put("_id", id.toString());
        collection.findOneAndDelete(query);
    }

    public static void removeMessage(@NotNull Message post) {
        var query = new Document();
        query.put("_id", post.getId().toString());
        collection.findOneAndDelete(query);
    }
}
