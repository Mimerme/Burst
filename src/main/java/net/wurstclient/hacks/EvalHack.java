package net.wurstclient.hacks;

import io.github.burstclient.EvalScreen;
import net.wurstclient.BurstFeature;
import net.wurstclient.SearchTags;
import net.wurstclient.altmanager.screens.DirectLoginScreen;
import net.wurstclient.clickgui.screens.ClickGuiScreen;
import net.wurstclient.hack.Hack;
import net.wurstclient.hack.Setting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

@BurstFeature(name="EvalHack", description="Evalute js scripts for errors.", category = "Developer")
@SearchTags({"eval", "script", "js"})
public class EvalHack extends Hack {
    @Setting
    private CheckboxSetting autoEval = new CheckboxSetting("Auto Evaluate", false);

    @Override
    public void onEnable()
    {
        MC.openScreen(new EvalScreen(null, false));
        setEnabled(false);
    }
}
