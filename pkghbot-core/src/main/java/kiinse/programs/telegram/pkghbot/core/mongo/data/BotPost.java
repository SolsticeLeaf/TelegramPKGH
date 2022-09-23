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

package kiinse.programs.telegram.pkghbot.core.mongo.data;

import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.posts.Post;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostSettings;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostStatus;
import kiinse.programs.telegram.pkghbot.core.mongo.PostQuery;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BotPost implements Post {

    private final UUID id;
    private String text;
    private User user;
    private PostSettings settings;
    private PostStatus postStatus;
    private String creationDate;

    public BotPost(@NotNull UUID id) {
        this.id = id;
        this.postStatus = PostStatus.NEW;
        this.creationDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    public BotPost() {
        this.id = UUID.randomUUID();
        this.postStatus = PostStatus.NEW;
        this.creationDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    @Override
    public @NotNull Post setUser(@NotNull User user) {
        this.user = user;
        return this;
    }

    @Override
    public @NotNull Post setText(@NotNull String text) {
        this.text = text;
        return this;
    }

    @Override
    public @NotNull Post setSettings(@NotNull PostSettings settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public @NotNull Post setStatus(@NotNull PostStatus status) {
        this.postStatus = status;
        return this;
    }

    @Override
    public @NotNull Post setCreationDate(@NotNull String date) {
        this.creationDate = date;
        return this;
    }

    @Override
    public @NotNull UUID getId() {
        return id;
    }

    @Override
    public @NotNull String getText() {
        return text;
    }

    @Override
    public @NotNull User getUser() {
        return user;
    }

    @Override
    public @NotNull PostSettings getSettings() {
        return settings;
    }

    @Override
    public @NotNull PostStatus getStatus() {
        return postStatus;
    }

    @Override
    public @NotNull String getCreationDate() {
        return creationDate;
    }

    @Override
    public @NotNull Post upload() {
        return PostQuery.updatePost(this);
    }
}
