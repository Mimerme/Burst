package net.wurstclient.commands;

import io.github.burstclient.AutoExecProcessor;
import net.wurstclient.BurstFeature;
import net.wurstclient.command.BurstCmd;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;

@BurstFeature(name="exec", description = "execute .mr batch files",
        category = "cmd")
@BurstCmd(syntax = ".exec <*.mr>")
public class ExecCmd extends Command {
    @Override
    public void call(String[] args) throws CmdException {
        if (args.length < 1)
            throw new CmdSyntaxError();

        AutoExecProcessor.exec(args[0]);
    }
}
