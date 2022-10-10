package dev.pkgh.bot.commands;

import dev.pkgh.bot.core.PKGHBot;
import dev.pkgh.bot.users.BotUser;
import dev.pkgh.bot.util.ScheduleUtil;
import dev.pkgh.sdk.commands.NamedCommand;
import dev.pkgh.sdk.commands.WithAlias;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.ExecutorMethod;
import dev.pkgh.sdk.utils.TimeUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@WithAlias("расписание")
@NamedCommand("schedule")
public final class ScheduleCommand {

    /** Bot core instance */
    PKGHBot bot;

    @ExecutorMethod @SneakyThrows
    public void execute(final ExecutionSession session) {
        val user = BotUser.getOrCreate(session.getSender().getId());
        if (user.getGroup() == null) {
            session.sendMessage("Вы не установили группу");
            return;
        }

        if (!ScheduleUtil.isValidGroup(user.getGroup())) {
            session.sendMessage("Ваша группа невалидна. Установите новую, используя /group");
            return;
        }

        val sendImage = new SendPhoto();

        var file = new File(user.getId() + "-" + user.getGroup() + "-" + TimeUtil.getFormattedDate() + ".png");
        val schedule = ScheduleUtil.loadSchedule(user.getGroup());
        ImageIO.write(schedule.getScheduleImage().getImage(), "png", file);

        sendImage.setPhoto(new InputFile(user.getGroup()).setMedia(file));
        sendImage.setChatId(session.getTextChannel().getId());
        sendImage.setCaption(
                "Расписание от: " + schedule.getScheduleImage().getGatheredAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + (schedule.isCached()
                            ? " [C]"
                            : ""));

        bot.execute(sendImage);
    }

}
