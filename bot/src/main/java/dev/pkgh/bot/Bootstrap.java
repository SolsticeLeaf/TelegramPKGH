package dev.pkgh.bot;

import dev.pkgh.bot.core.PKGHBot;
import dev.pkgh.sdk.logging.Logging;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Bootstrap-class for telegram bot
 *
 * @author winston
 */
public final class Bootstrap {

    /** Entry-Point */
    public static void main(final String @NotNull [] args) throws TelegramApiException {
        val botInst = new PKGHBot();
        val api = new TelegramBotsApi(DefaultBotSession.class);

        try {
            botInst.postInit();
        } catch (final Exception e) {
            Logging.IMP.stacktrace(e, "Exception caught while initializing bot");
            e.printStackTrace();
        }

        api.registerBot(botInst);
        Logging.IMP.info("Bootstrapper finished it's work");
    }

}
