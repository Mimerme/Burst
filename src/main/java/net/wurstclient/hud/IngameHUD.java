/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hud;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.BurstClient;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.screens.ClickGuiScreen;
import net.wurstclient.events.GUIRenderListener;

public final class IngameHUD implements GUIRenderListener
{
	private final WurstLogo wurstLogo = new WurstLogo();

	public WurstLogo getWurstLogo() {
		return wurstLogo;
	}

	public TabGui getTabGui() {
		return tabGui;
	}

	private final HackListHUD hackList = new HackListHUD();
	private TabGui tabGui;
	
	@Override
	public void onRenderGUI(MatrixStack matrixStack, float partialTicks)
	{
		if(!BurstClient.INSTANCE.isEnabled())
			return;
		
		if(tabGui == null)
			tabGui = new TabGui();
		
		boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
		ClickGui clickGui = BurstClient.INSTANCE.getGui();
		
		// GL settings
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		clickGui.updateColors();
		
		wurstLogo.render(matrixStack);
		hackList.render(matrixStack, partialTicks);
		tabGui.render(matrixStack, partialTicks);
		
		// pinned windows
		if(!(BurstClient.MC.currentScreen instanceof ClickGuiScreen))
			clickGui.renderPinnedWindows(matrixStack, partialTicks);
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1, 1, 1, 1);
		
		if(blend)
			GL11.glEnable(GL11.GL_BLEND);
		else
			GL11.glDisable(GL11.GL_BLEND);
	}
	
	public HackListHUD getHackList()
	{
		return hackList;
	}
}
