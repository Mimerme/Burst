package io.github.burstclient;

import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.BurstClient;
import net.wurstclient.event.Event;
import net.wurstclient.events.BurstListener;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.util.ChatUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AutoExecProcessor implements GUIRenderListener {
    boolean exec = false;

    //Create a seperate thread for firing events asap
    public void startFastEvent(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(BurstClient.MC.player != null) {
                    synchronized (BurstClient.INSTANCE.getEventManager()) {
                        BurstClient.INSTANCE.getEventManager().fire(BurstListener.BurstEvent.INSTANCE);
                    }
                }
            }
        }).start();
    }

    @Override
    public void onRenderGUI(MatrixStack matrixStack, float partialTicks) {
        if (exec)
            return;

        File f = new File("autoexec.mr");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        exec("autoexec.mr");
        exec = true;

        startFastEvent();
    }

    public static void exec(String filename){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    filename));
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();

                if (line.equals("")) {
                    line = reader.readLine();
                    continue;
                }

                System.out.println(line);
                BurstClient.INSTANCE.getCmdProcessor().process(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            ChatUtils.message("File doesn't exist");
        } catch (Exception e){
            ChatUtils.message("ERROR: " + e.getMessage());
        }
    }
}
