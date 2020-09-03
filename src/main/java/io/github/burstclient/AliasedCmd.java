package io.github.burstclient;


import net.wurstclient.BurstClient;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.Command;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class AliasedCmd extends Command {
    public ArrayList<String> commands = new ArrayList<>();
    public AliasedCmd(String semicolonCommands, String name){
        super(name, "An aliased command", new String[]{"." + name});

        String[] splits = semicolonCommands.split(";");
        for (String split : splits){
            commands.add(split);
        }

        setCategory("cmd");
    }

    @Override
    public void call(String[] args) throws CmdException {
        for (String cmd : commands){
            BurstClient.INSTANCE.getCmdProcessor().process(cmd);
        }
    }
}
