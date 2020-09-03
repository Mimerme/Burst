package net.wurstclient.commands;

import io.github.burstclient.AliasedCmd;
import net.wurstclient.BurstClient;
import net.wurstclient.BurstFeature;
import net.wurstclient.Feature;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.Window;
import net.wurstclient.clickgui.components.FeatureButton;
import net.wurstclient.command.BurstCmd;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

@BurstFeature(name="click", description = "modify ClickGui Settings",
        category = "cmd")
@BurstCmd(syntax = ".click <block/unblock> <window title string>\n" +
                    ".click block list\n" +
                    ".click window <add/rm/new/del>\n")
public class ClickGuiCmd extends Command{
    public HashSet<String> blockedWindowNames = new HashSet<>();
    public TreeMap<String, Window> aliasedWindows = new TreeMap<String, Window>();
    public TreeMap<String, AliasedCmd> aliasedCommands = new TreeMap<String, AliasedCmd>();

    public ClickGuiCmd(){
        BurstClickData e = null;
        try {
            FileInputStream fileIn = new FileInputStream("burstclickgui.mrmrmr");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = (BurstClickData) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("BurstClickData class not found");
            c.printStackTrace();
            return;
        }

        this.blockedWindowNames = e.blockedWindowNames;
        this.aliasedWindows = e.aliasedWindows;
        this.aliasedCommands = e.aliasedCommands;
    }

    private class BurstClickData implements Serializable {
        public HashSet<String> blockedWindowNames;
        public TreeMap<String, Window> aliasedWindows;
        public TreeMap<String, AliasedCmd> aliasedCommands;

        public BurstClickData(HashSet<String> blockedWindowNames, TreeMap<String, Window> aliasedWindows, TreeMap<String, AliasedCmd> aliasedCommands){
            this.blockedWindowNames = blockedWindowNames;
            this.aliasedWindows = aliasedWindows;
            this.aliasedCommands = aliasedCommands;
        }
    }




    @Override
    public void call(String[] args) throws CmdException {
        ClickGui gui = BurstClient.INSTANCE.getGui();

        if (args.length < 1)
            throw new CmdSyntaxError();


        switch(args[0]){
            case "block":
                if (args.length > 1 && args[1].equals("list")){
                    for (String window : blockedWindowNames){
                        ChatUtils.message("Blocked Window: " + window);
                    }
                    return;
                }
                blockedWindowNames.add(args[1].toLowerCase());
                break;
            case "window":
                if (args.length < 3)
                    throw new CmdSyntaxError();

                Window aliasedWindow = aliasedWindows.get(args[2]);


                switch (args[1]) {
                    case "new":
                        Window newWindow = new Window(args[2]);
                        newWindow.setInvisible(false);
                        newWindow.pack();
                        aliasedWindows.put(args[2], newWindow);
                        gui.addWindow(newWindow);
                        return;
                    case "del":
                        aliasedWindows.remove(args[2]);
                        gui.getWindows().remove(aliasedWindow);
                        return;
                }

                if (args.length < 4)
                    throw new CmdSyntaxError();

                Feature feature = BurstClient.INSTANCE.getFeatureByName(args[3]);

                if (feature == null) {
                    ChatUtils.message("Feature does not exist");
                    return;
                }

                switch (args[1]) {
                    case "add":
                        aliasedWindow.add(new FeatureButton(feature));
                        return;
                    case "rm":
                        ChatUtils.message("Enter an index, not feature name. \nLol i'm too lazy to make this easier");
                        aliasedWindow.remove(Integer.parseInt(args[3]));
                        return;
                }

                blockedWindowNames.remove(args[1]);
                break;
            default:
                throw new CmdSyntaxError();
        }

    }
}
