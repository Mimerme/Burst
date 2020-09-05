package io.github.burstclient.cmds;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.wurstclient.BurstFeature;
import net.wurstclient.command.BurstCmd;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;

import java.util.Collection;

@BurstFeature(name="echo", description = "Echos a string",
        category = "cmd")
@BurstCmd(syntax = ".echo <string>")
public class EchoCmd extends Command {
    @Override
    public void call(String[] args) throws CmdException {
        if (args.length < 1)
            throw new CmdSyntaxError();

        ChatUtils.message(args[0]);
    }
}