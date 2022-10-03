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
import kiinse.programs.telegram.pkghbot.api.messages.Message;
import kiinse.programs.telegram.pkghbot.api.messages.enums.MessageStatus;
import kiinse.programs.telegram.pkghbot.api.messages.enums.MessageType;
import kiinse.programs.telegram.pkghbot.core.mongo.MessageQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BotMessage implements Message {

    private final UUID id;
    private User user;
    private String text;
    private MessageType type;
    private MessageStatus status;
    private String creationDate;

    public BotMessage() {
        this.id = UUID.randomUUID();
        this.user = null;
        this.text = "";
        this.type = MessageType.ALL;
        this.status = MessageStatus.NEW;
        this.creationDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    public BotMessage(@NotNull UUID id) {
        this.id = id;
        this.user = null;
        this.text = "";
        this.type = MessageType.ALL;
        this.status = MessageStatus.NEW;
        this.creationDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    @Override
    public @NotNull UUID getId() {
        return id;
    }

    @Override
    public @Nullable User getUser() {
        return user;
    }

    @Override
    public @NotNull String getText() {
        return text;
    }

    @Override
    public @NotNull MessageType getType() {
        return type;
    }

    @Override
    public @NotNull MessageStatus getStatus() {
        return status;
    }

    @Override
    public @NotNull String getCreationDate() {
        return creationDate;
    }

    @Override
    public @NotNull Message setUser(@Nullable User user) {
        this.user = user;
        return this;
    }

    @Override
    public @NotNull Message setText(@NotNull String text) {
        this.text = text;
        return this;
    }

    @Override
    public @NotNull Message setType(@NotNull MessageType type) {
        this.type = type;
        return this;
    }

    @Override
    public @NotNull Message setStatus(@NotNull MessageStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public @NotNull Message setCreationDate(@NotNull String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    @Override
    public @NotNull Message upload() {
        return MessageQuery.updateMessage(this);
    }

    @Override
    public @NotNull String toString() {
        return (type == MessageType.ALL ?
                new StringBuilder("▬▬▬ Оповещение ▬▬▬") : new StringBuilder("▬▬▬ Сообщение ▬▬▬"))
                .append("\n")
                .append(getText()).toString();
    }
}
