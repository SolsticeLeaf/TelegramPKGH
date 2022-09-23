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

package kiinse.programs.telegram.pkghbot.bot.data;

import kiinse.programs.telegram.pkghbot.bot.callbackdata.CallBackDataManager;
import kiinse.programs.telegram.pkghbot.bot.commands.CommandsManager;
import kiinse.programs.telegram.pkghbot.bot.commands.usercommands.OtherCommands;

/**
 * Класс, содержащий в себе информацию для работы бота
 *
 * @author kiinse
 * @version 3.1.1
 * @since 3.1.1
 */
public class BotSettings {

    private OtherCommands otherCommands = null;

    private CommandsManager commandsManager = null;

    private CallBackDataManager callBackDataManager = null;

    private String botName = null;

    private String botToken = null;

    public OtherCommands getOtherCommands() {
        return otherCommands;
    }

    public void setOtherCommands(OtherCommands otherCmd) throws NullPointerException {
        if (otherCmd == null) {
            throw new NullPointerException("OtherCommands can't be null");
        }
        this.otherCommands = otherCmd;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    /**
     * Setter для CommandsManager
     *
     * @param cmdMng CommandsManager {@link CommandsManager}
     * @throws NullPointerException В случае, если поступающий CommandsManager == null
     */
    public void setCommandsManager(CommandsManager cmdMng) throws NullPointerException {
        if (cmdMng == null) {
            throw new NullPointerException("CommandsManager can't be null");
        }
        this.commandsManager = cmdMng;
    }

    public CallBackDataManager getCallBackDataManager() {
        return callBackDataManager;
    }

    /**
     * Setter для CallBackDataManager
     *
     * @param cbdMng CallBackDataManager {@link CallBackDataManager}
     * @throws NullPointerException В случае, если поступающий CallBackDataManager == null
     */
    public void setCallBackDataManager(CallBackDataManager cbdMng) throws NullPointerException {
        if (cbdMng == null) {
            throw new NullPointerException("CallBackDataManager can't be null");
        }
        this.callBackDataManager = cbdMng;
    }

    public String getBotName() {
        return botName;
    }

    /**
     * Setter для BotName
     *
     * @param name Имя бота
     * @throws NullPointerException В случае, если поступающая строка пустая
     */
    public void setBotName(String name) throws NullPointerException {
        if (name.isBlank()) {
            throw new NullPointerException("Bot name can't be empty");
        }
        this.botName = name;
    }

    public String getBotToken() {
        return botToken;
    }

    /**
     * Setter для BotToken
     *
     * @param token Токен бота
     * @throws NullPointerException В случае, если поступающая строка пустая
     */
    public void setBotToken(String token) throws NullPointerException {
        if (token.isBlank()) {
            throw new NullPointerException("Bot token can't be empty");
        }
        this.botToken = token;
    }

}
