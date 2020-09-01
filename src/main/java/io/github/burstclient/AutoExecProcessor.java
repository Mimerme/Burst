package io.github.burstclient;

import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.BurstClient;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.util.ChatUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AutoExecProcessor implements GUIRenderListener {
    boolean exec = false;

    @Override
    public void onRenderGUI(MatrixStack matrixStack, float partialTicks) {
        exec("autoexec.mr");
        exec = true;
    }

    public static void exec(String filename){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    filename));
            String line = reader.readLine();
            while (line != null) {
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
