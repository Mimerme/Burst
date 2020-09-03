package net.wurstclient.commands;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.wurstclient.BurstFeature;
import net.wurstclient.command.BurstCmd;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;

import java.util.Collection;

@BurstFeature(name="modlist", description = "Lists all currently loaded Fabric mods",
        category = "cmd")
@BurstCmd(syntax = ".modlist")
public class ModlistCmd extends Command{
    @Override
    public void call(String[] args) throws CmdException {
        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        ChatUtils.message(">>> MOD LIST <<<");
        for (ModContainer mod : mods) {
            ModMetadata metadata = mod.getMetadata();
            ChatUtils.message(metadata.getName() + " " + metadata.getVersion() + " " + metadata.getType());
        }
    }
}
