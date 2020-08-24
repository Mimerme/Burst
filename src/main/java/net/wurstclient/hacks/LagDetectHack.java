package net.wurstclient.hacks;

import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.LiteralText;
import net.wurstclient.Category;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;

public class LagDetectHack extends Hack implements PacketInputListener, UpdateListener {
    private long lastPacketTime = System.currentTimeMillis();
    private boolean lagging = false;
    private final SliderSetting timeoutThresh = new SliderSetting("Timeout",
            "Threshold before lack of packets is considered lag",
            250, 1, 9000, 100, SliderSetting.ValueDisplay.DECIMAL);

    public TutorialToast lagToast;

    public LagDetectHack() {
        super("LagDetect", "Notifies you when you're lagging");
        setCategory(Category.OTHER);

        addSetting(timeoutThresh);
    }


    @Override
    protected void onEnable() {
        EVENTS.add(PacketInputListener.class, this);
        EVENTS.add(UpdateListener.class, this);
    }

    @Override
    protected void onDisable() {
        EVENTS.remove(PacketInputListener.class, this);
        EVENTS.remove(UpdateListener.class, this);
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        lastPacketTime = System.currentTimeMillis();

        if (lagging) {
            lagging = false;
            this.lagToast.hide();
        }
    }

    @Override
    public void onUpdate() {
        if ((System.currentTimeMillis() - lastPacketTime) > timeoutThresh.getValueF() && !lagging){
            System.out.println("lagging");
            lagToast = new TutorialToast(TutorialToast.Type.WOODEN_PLANKS,
                    new LiteralText("Lag Detected!"), new LiteralText("No incoming packest"), false);
            MC.getToastManager().add(this.lagToast);
            lagging = true;
        }
    }
}
