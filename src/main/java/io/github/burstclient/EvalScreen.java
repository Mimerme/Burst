package io.github.burstclient;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.wurstclient.BurstClient;
import net.wurstclient.altmanager.AltRenderer;
import net.wurstclient.altmanager.NameGenerator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class EvalScreen extends Screen {
    private String lastEval = "";
    protected final Screen prevScreen;

    private TextFieldWidget jsFile;

    private ButtonWidget compileButton;

    protected String message = "";


    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> lines = Lists.newArrayList();

    //The difference between start and end will be how many lines we render
    int viewportStart = 0;
    int viewportEnd = 13;

    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    private long field_23935 = 0L;

    private boolean autoEval = false;

    public EvalScreen(Screen prevScreen, boolean autoEval)
    {
        super(new LiteralText("Evaluate .js File"));
        this.prevScreen = prevScreen;
        this.autoEval = autoEval;
    }

    public EvalScreen(Screen prevScreen, Exception e)
    {
        super(new LiteralText("Evaluate .js File"));
        this.prevScreen = prevScreen;

        //https://stackoverflow.com/questions/1149703/how-can-i-convert-a-stack-trace-to-a-string
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string

        for (String line : sStackTrace.split("\n")){
            lines.add(line.trim());
        }
    }
    private void eval(){
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        lastEval = jsFile.getText();
        System.out.println("Evaluating \'" + lastEval + "\'");

        try {
            engine.eval(new FileReader(lastEval));
            lines.add("Success!");
        } catch (Exception e) {
            //https://stackoverflow.com/questions/1149703/how-can-i-convert-a-stack-trace-to-a-string
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string

            for (String line : sStackTrace.split("\n")){
                lines.add(line.trim());
            }
        }

    }

    @Override
    public final void init()
    {
        addButton(new ButtonWidget(width / 2 - 100, height / 4 + 120 + 12, 200,
                20, new LiteralText("Back"), b -> client.openScreen(prevScreen)));

        compileButton = new ButtonWidget(width / 2 - 100, height / 4 + 96 + 12, 200,
                20, new LiteralText("Evaluate"),
                b -> eval());
        addButton(compileButton);


        jsFile = new TextFieldWidget(textRenderer, width / 2 - 100, 60, 200,
                20, new LiteralText(""));
        jsFile.setMaxLength(48);
        jsFile.setSelected(true);
        children.add(jsFile);

        setInitialFocus(jsFile);
    }

    @Override
    public final void tick()
    {
        jsFile.tick();

        String email = jsFile.getText().trim();

        compileButton.active = !email.isEmpty();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int int_3)
    {
        if(keyCode == GLFW.GLFW_KEY_ENTER)
            eval();

        if(keyCode == GLFW.GLFW_KEY_C) {
            lines.clear();
            viewportStart = 0;
            viewportEnd = 13;
        }

        if(keyCode == GLFW.GLFW_KEY_DOWN)
            scroll(1);
        else if(keyCode == GLFW.GLFW_KEY_UP)
            scroll(-1);

        return super.keyPressed(keyCode, scanCode, int_3);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button)
    {
        compileButton.mouseClicked(x, y, button);

        if(jsFile.isFocused())
            message = "";

        return super.mouseClicked(x, y, button);
    }

    public void scroll(int scroll){
        //Save the old viewport Locations
        int oldStart = viewportStart, oldEnd = viewportEnd;

        viewportStart += scroll;
        viewportEnd += scroll;

        //If the viewport is out of bounds revert
        if (viewportStart < 0 || viewportEnd > lines.size() -1)
        {
            viewportStart = oldStart;
            viewportEnd = oldEnd;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY,
                       float partialTicks)
    {
        if(autoEval)
            eval();

        renderBackground(matrixStack);

        // text
        drawStringWithShadow(matrixStack, textRenderer, ".js File",
                width / 2 - 100, 47, 10526880);
        drawCenteredString(matrixStack, textRenderer, message, width / 2, 142,
                16777215);

        // text boxes
        jsFile.render(matrixStack, mouseX, mouseY, partialTicks);

        renderLog(matrixStack, width / 4, height / 5 - 25);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderLog(MatrixStack matrices, float x, float y){
            boolean bl = true;

            double d = BurstClient.INSTANCE.MC.options.chatScale;
            int k = MathHelper.ceil((double)BurstClient.INSTANCE.MC.options.chatWidth / d);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(2.0F, 8.0F, 0.0F);
            RenderSystem.scaled(d, d, 1.0D);
            double e = this.client.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
            double f = this.client.options.textBackgroundOpacity;
            double g = 9.0D * (this.client.options.chatLineSpacing + 1.0D);
            double h = -8.0D * (this.client.options.chatLineSpacing + 1.0D) + 4.0D * this.client.options.chatLineSpacing;
            int l = 0;


            int iter = 0;
            for (int i = viewportStart; i < Math.min(viewportEnd, lines.size() - 1); i++) {
                this.client.textRenderer.drawWithShadow(matrices, new LiteralText(lines.get(i)), x, y + iter * 10, 0xffffffff);
                iter++;
            }

/*
            if (bl) {
                this.client.textRenderer.getClass();
                int v = 9;
                RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
                if (w != x) {
                    aa = y > 0 ? 170 : 96;
                    ab = this.hasUnreadNewMessages ? 13382451 : 3355562;
                    fill(matrices, 0, -y, 2, -y - z, ab + (aa << 24));
                    fill(matrices, 2, -y, 1, -y - z, 13421772 + (aa << 24));
                }
            }
*/

            RenderSystem.popMatrix();
    }

}
