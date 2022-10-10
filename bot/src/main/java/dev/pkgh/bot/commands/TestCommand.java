package dev.pkgh.bot.commands;

import dev.pkgh.bot.users.BotUser;
import dev.pkgh.sdk.commands.NamedCommand;
import dev.pkgh.sdk.commands.WithAlias;
import dev.pkgh.sdk.commands.WithPermission;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.ExecutorMethod;
import dev.pkgh.sdk.commands.execution.SubcommandExecutor;
import dev.pkgh.sdk.type.UserPermission;
import dev.pkgh.sdk.utils.TimeUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.io.*;

/**
 * Flow dump generation command and
 * some tests. Do not remove yet.
 * Required for debugging purposes
 *
 * @author winston
 */
@WithPermission(UserPermission.ADMINISTRATOR)
@NamedCommand("testflow")
@WithAlias("члееен")
public final class TestCommand {

    @ExecutorMethod
    public static void execute(final ExecutionSession session) {
        session.sendMessage("Generating flow dump X ...");

        // generating stack trace
        Thread.dumpStack();

        // Writing thread dump into file
        val fileName = TimeUtil.getFormattedDate("yyyy-dd-MM_HH-mm-ss") + "-dump.ebal";
        dumpThreads(fileName);

        session.sendMessage("Done. See terminal for details. Thread dump: `" + fileName + "`");
    }

    @SubcommandExecutor("gc")
    public static void testParam(final ExecutionSession session) {
        System.gc();
        session.sendMessage("GC invoked >.<");
    }

    @SubcommandExecutor("user")
    public static void testUser(final ExecutionSession session) {
        session.sendMessage("Executing test flow user ...");
        val user = BotUser.getOrCreate(session.getSender().getId());
        session.sendMessage(user.getId() + " | " + user.getGroup() + " | " + user.getRegistrationDate().toString());
        session.sendMessage("Done executing test flow user");
    }

    @SubcommandExecutor("group")
    public static void updateGroup(final ExecutionSession session) {
        session.sendMessage("Executing test group (" + session.getArguments().get(0) + ") ...");

        val user = BotUser.getOrCreate(session.getSender().getId());
        user.setGroup(session.getArguments().get(0));
        user.saveAsync();

        session.sendMessage("Done executing test flow user");
    }

    @SneakyThrows
    private static void dumpThreads(final @NonNull String fileName) {
        val pid = ProcessHandle.current().pid();
        val rt = Runtime.getRuntime();
        val commands = new String[]{ "jstack", "-l", String.valueOf(pid) };
        val proc = rt.exec(commands);

        val stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        val writer = new FileWriter(fileName);

        String ln;
        while ((ln = stdInput.readLine()) != null) {
            writer.write(ln);
        }

        writer.flush();
        writer.close();
    }

}
