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
import kiinse.programs.telegram.pkghbot.api.posts.Post;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostSettings;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostStatus;
import kiinse.programs.telegram.pkghbot.core.mongo.data.BotPost;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class PostQuery {

    private static final MongoCollection<Document> collection = MongoDb.getDataBase().getCollection("posts");

    private PostQuery() {}

    public static @NotNull Document postToDocument(@NotNull Post post) {
        var query = new Document();
        query.put("_id", post.getId().toString());
        query.put("user", post.getUser().getChatId());
        query.put("text", post.getText());
        query.put("status", post.getStatus().toString());
        query.put("settings", post.getSettings().toString());
        query.put("created", post.getCreationDate());
        return query;
    }

    private static @Nullable Post parsePostResult(@Nullable Document result) {
        if (result == null || result.keySet().size() < 3) {
            return null;
        }
        return new BotPost(UUID.fromString(String.valueOf(result.get("_id"))))
                .setUser(Objects.requireNonNull(UserQuery.getUser(Long.parseLong(String.valueOf(result.get("user"))))))
                .setText(String.valueOf(result.get("text")))
                .setStatus(PostStatus.valueOf(String.valueOf(result.get("status"))))
                .setSettings(PostSettings.valueOf(String.valueOf(result.get("settings"))))
                .setCreationDate(String.valueOf(result.get("created")));
    }

    public static boolean hasPost(@NotNull UUID id) {
        var query = new Document();
        query.put("_id", id.toString());
        var result = collection.find(query).first();
        return result != null && !result.isEmpty();
    }

    public static @NotNull Post createPost(@NotNull Post post) {
        collection.insertOne(postToDocument(post));
        return post;
    }

    public static @Nullable Post getPost(@NotNull UUID id) {
        var query = new Document();
        query.put("_id", id.toString());
        return parsePostResult(collection.find(query).first());
    }

    public static @NotNull Post updatePost(@NotNull Post post) {
        var query = new Document();
        query.put("_id", post.getId().toString());
        collection.findOneAndReplace(query, postToDocument(post));
        return post;
    }

    public static void removePost(@NotNull UUID id) {
        var query = new Document();
        query.put("_id", id.toString());
        collection.findOneAndDelete(query);
    }

    public static void removePost(@NotNull Post post) {
        var query = new Document();
        query.put("_id", post.getId().toString());
        collection.findOneAndDelete(query);
    }
}
