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

package kiinse.programs.telegram.pkghbot.bot;

import io.sentry.Sentry;
import kiinse.programs.telegram.pkghbot.api.mongo.MongoSettings;
import kiinse.programs.telegram.pkghbot.api.sentry.SentrySettings;
import kiinse.programs.telegram.pkghbot.api.utils.LoggerUtils;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.CallBackDataManager;
import kiinse.programs.telegram.pkghbot.bot.commands.CommandsManager;
import kiinse.programs.telegram.pkghbot.bot.commands.usercommands.OtherCommands;
import kiinse.programs.telegram.pkghbot.bot.data.BotSettings;
import kiinse.programs.telegram.pkghbot.bot.schedulers.EveningSchedule;
import kiinse.programs.telegram.pkghbot.bot.schedulers.NextMailingSchedule;
import kiinse.programs.telegram.pkghbot.core.data.Config;
import kiinse.programs.telegram.pkghbot.core.data.ExportFiles;
import kiinse.programs.telegram.pkghbot.core.mongo.MongoDb;
import kiinse.programs.telegram.pkghbot.core.sentry.SentryUtils;
import kiinse.programs.telegram.pkghbot.core.utils.ColorsUtils;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Timer;

public class InitializeClasses {

    private static final Logger logger = LoggerUtils.getLogger();

    public void loadClasses() {
        logger.info("Loading classes...");
        try {
            exportFiles();
            loadSentry();
            loadSQL();
            startTimers();
            loadBot();
            System.out.println(ColorsUtils.color(ColorsUtils.color.GREEN, "Запущено!!!!!!!!!"));
        } catch (Exception e) {
            logger.error("An error occurred while loading the bot.\nThe program is being terminated: {}", e.getMessage());
            Sentry.captureException(e);
            System.out.println(
                    ColorsUtils.color(ColorsUtils.color.RED,
                                      "An error occurred while loading the bot.\nThe program is being terminated."));
            System.exit(1);
        }
    }

    private void exportFiles() throws Exception {
        logger.info("Exporting and loading files...");
        Config.export();
        new ExportFiles().export();
        logger.info("Files exported/loaded");
    }

    private void loadSentry() throws IllegalArgumentException {
        if (Config.getBoolean("sentry.enabled")) {
            logger.info("Loading Sentry...");
            var settings = new SentrySettings();
            settings.setDsn(Config.getProperty("sentry.dsn"));
            settings.setRelease(Config.getVersion(Config.Version.BOT));
            new SentryUtils().init(settings);
            logger.info("Sentry loaded.");
        } else {
            logger.info("Sentry disabled in config. Sentry will not run at this stage.");
        }
    }

    private void loadSQL() throws IllegalArgumentException {
        logger.info("Loading SQL...");
        var settings = new MongoSettings();
        settings.setHost(Config.getProperty("db.host"));
        settings.setPort(Config.getProperty("db.port"));
        settings.setLogin(Config.getProperty("db.login"));
        settings.setPassword(Config.getProperty("db.password"));
        settings.setDbName(Config.getProperty("db.name"));
        settings.setAuthDb(Config.getProperty("db.auth"));
        new MongoDb(settings).connect();
        logger.info("SQL loaded.");
    }

    private void loadBot() throws TelegramApiException, IllegalArgumentException {
        logger.info("Loading Bot...");
        var settings = new BotSettings();
        settings.setBotName(Config.getProperty("bot.name"));
        settings.setBotToken(Config.getProperty("bot.token"));
        settings.setOtherCommands(new OtherCommands());
        settings.setCommandsManager(new CommandsManager());
        settings.setCallBackDataManager(new CallBackDataManager());
        new TelegramBotsApi(DefaultBotSession.class).registerBot(new Bot(settings));
        logger.info("Bot has been started!\nBot version: {}", Config.getVersion(Config.Version.BOT));
    }

    private void startTimers() {
        int min = 60 * 1000;
        var time = new Timer();
        time.schedule(new NextMailingSchedule(), 0, min);
        time.schedule(new EveningSchedule(), 0, min);
    }
}
