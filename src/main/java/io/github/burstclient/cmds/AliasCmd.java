package io.github.burstclient.cmds;

import io.github.burstclient.AliasedCmd;
import net.wurstclient.BurstClient;
import net.wurstclient.BurstFeature;
import net.wurstclient.command.*;
import net.wurstclient.util.ChatUtils;

import java.util.HashMap;

@BurstFeature(name="alias", description = "Assign multiple commands under a single alias",
        category = "cmd")
@BurstCmd(syntax = ".alias <alias name> <cmd list delimited by ;>")
public class AliasCmd extends Command {
    public HashMap<String, AliasedCmd> aliasedCommands = new HashMap<>();


    @Override
    public void call(String[] args) throws CmdException {
        if (args.length < 1)
            throw new CmdSyntaxError();

        String name = args[0];

        if (name.equals("list")){
            for (String a : aliasedCommands.keySet())
                ChatUtils.message(a + " -> " + aliasedCommands.get(a).commands);
            return;
        }

        if (args.length < 2)
            throw new CmdSyntaxError();



        if (aliasedCommands.containsKey(name))
            throw new CmdError("Aliased command already exists");

        AliasedCmd newCmd = new AliasedCmd(args[1], name);
        BurstClient.INSTANCE.getCmds().addCommand("." + name, newCmd);
        aliasedCommands.put(name, newCmd);
    }
}
