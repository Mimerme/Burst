package net.wurstclient.commands;

import net.minecraft.util.ChatUtil;
import net.wurstclient.BurstFeature;
import net.wurstclient.command.BurstCmd;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;

@BurstFeature(name="cwd", description = "Prints the current working directory",
        category = "cmd")
@BurstCmd(syntax = ".cwd")
public class CwdCmd extends Command{
    @Override
    public void call(String[] args) throws CmdException {
        ChatUtils.message(System.getProperty("user.dir"));
    }
}
