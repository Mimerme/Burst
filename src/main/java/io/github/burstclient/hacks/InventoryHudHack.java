package io.github.burstclient.hacks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.wurstclient.BurstClient;
import net.wurstclient.BurstFeature;
import net.wurstclient.clickgui.BurstWindow;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.Component;
import net.wurstclient.clickgui.Window;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.hack.Hack;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@BurstFeature(name="InventoryHud", description="Draw your inventory on your HUD", category = "HUD")
public class InventoryHudHack extends Hack implements GUIRenderListener {

    @BurstWindow
    private Window window;
    protected Slot focusedSlot;

    public int x;
    public int y;
    public int zOffset = 5;

    public final class InventoryHudComponent extends Component
    {
        public InventoryHudComponent(){
            setWidth(100);
            setHeight(100);
        }


        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY,
                           float partialTicks)
        {
            ClickGui gui = BurstClient.INSTANCE.getGui();
            float[] bgColor = gui.getBgColor();
            float[] acColor = gui.getAcColor();
            float opacity = gui.getOpacity();

            int x1 = getX();
            int x2 = x1 + getWidth();
            int y1 = getY();
            int y2 = y1 + getHeight();

        }

        @Override
        public int getDefaultWidth()
        {
            return 150;
        }

        @Override
        public int getDefaultHeight()
        {
            return 96;
        }
    }

    public InventoryHudHack(){
        window = new Window("Inventory");
        window.setPinned(true);
        window.setInvisible(true);
        window.add(new InventoryHudComponent());
        window.setInvisible(true);
    }

    public void onEnable(){
        window.setInvisible(false);
        BurstClient.INSTANCE.getEventManager().add(GUIRenderListener.class, this);
    }

    public void onDisable(){
        window.setInvisible(true);
        BurstClient.INSTANCE.getEventManager().remove(GUIRenderListener.class, this);

    }



    @Override
    public void onRenderGUI(MatrixStack matrixStack, float partialTicks) {
        this.x = window.getX() + 10;
        this.y = window.getY() + 10;

        int offsetX = 0;
        int offsetY = 0;
        int offsetSize= 20;

        for(ItemStack item : MC.player.inventory.main){
            MC.getItemRenderer().renderGuiItemIcon(item, x + (offsetSize * offsetX),y + offsetSize * offsetY);
            MC.getItemRenderer().renderGuiItemOverlay(MC.textRenderer, item, x + (offsetSize * offsetX), y + (offsetSize * offsetY), String.valueOf(item.getCount()));

            if (offsetX > 7){
                offsetX = -1;
                offsetY += 1;
            }

            offsetX +=1;

        }


    }
}
