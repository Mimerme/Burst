package io.github.burstclient.hacks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Quaternion;
import net.wurstclient.BurstClient;
import net.wurstclient.BurstFeature;
import net.wurstclient.SearchTags;
import net.wurstclient.clickgui.BurstWindow;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.Component;
import net.wurstclient.clickgui.Window;
import net.wurstclient.clickgui.components.RadarComponent;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.hack.Hack;

@BurstFeature(name="PlayerDraw", description="Draw the little inventory man on your HUD", category = "HUD")
@SearchTags({"playerdraw"})
public class PlayerHudHack extends Hack implements GUIRenderListener {

    @BurstWindow
    private Window window;

    public final class PlayerHudComponent extends Component
    {
        public PlayerHudComponent(){
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
            return 96;
        }

        @Override
        public int getDefaultHeight()
        {
            return 96;
        }
    }

    public PlayerHudHack(){
        window = new Window("Player");
        window.setPinned(true);
        window.setInvisible(true);
        window.add(new PlayerHudComponent());
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
        drawEntity(window.getX() + 100,
                window.getY() + 50,
                40,
                (float)(window.getX()),
                (float)(window.getY()),
                BurstClient.MC.player);    }


    public static void drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float g = (float)Math.atan((double)(mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x, (float)y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vector3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.yaw;
        float j = entity.pitch;
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.headYaw = entity.yaw;
        entity.prevHeadYaw = entity.yaw;
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880);
        });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.yaw = i;
        entity.pitch = j;
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        RenderSystem.popMatrix();
    }
}
