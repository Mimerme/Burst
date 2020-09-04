/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.clickgui.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.wurstclient.BurstClient;
import net.wurstclient.clickgui.ClickGui;

public final class ClickGuiScreen extends Screen
{
	private final ClickGui gui;
	
	public ClickGuiScreen(ClickGui gui)
	{
		super(new LiteralText(""));
		this.gui = gui;
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		try {
			gui.handleMouseClick((int) mouseX, (int) mouseY, mouseButton);
			return super.mouseClicked(mouseX, mouseY, mouseButton);
		}
		catch (Exception e){
			//TODO: actually integrated script error handling
			System.out.println(e);
			BurstClient.INSTANCE.fallback();
		}

		return false;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
	{
		try {
			gui.handleMouseRelease(mouseX, mouseY, mouseButton);
			return super.mouseReleased(mouseX, mouseY, mouseButton);
		}
		catch (Exception e){
			//TODO: actually integrated script error handling
			System.out.println(e);
			BurstClient.INSTANCE.fallback();
		}

		return false;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta)
	{
		try {
			gui.handleMouseScroll(mouseX, mouseY, delta);
			return super.mouseScrolled(mouseX, mouseY, delta);
		}
		catch (Exception e){
			//TODO: actually integrated script error handling
			System.out.println(e);
			BurstClient.INSTANCE.fallback();
		}
		return false;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
		float partialTicks)
	{
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		try {
			gui.render(matrixStack, mouseX, mouseY, partialTicks);
		}
		catch (Exception e){
			//TODO: actually integrated script error handling
			System.out.println(e);
			BurstClient.INSTANCE.fallback();
		}
	}
}
