/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.clickgui.components;

import java.util.Objects;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.Feature;
import net.wurstclient.BurstClient;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.Component;
import net.wurstclient.clickgui.SettingsWindow;
import net.wurstclient.clickgui.Window;
import net.wurstclient.hacks.TooManyHaxHack;
import net.wurstclient.util.ChatUtils;

public final class FeatureButton extends Component
{
	public final MinecraftClient MC = BurstClient.MC;
	public final ClickGui GUI = BurstClient.INSTANCE.getGui();

	public final Feature feature;
	public final boolean hasSettings;

	public Window settingsWindow;

	public FeatureButton(Feature feature)
	{
		this.feature = Objects.requireNonNull(feature);
		setWidth(getDefaultWidth());
		setHeight(getDefaultHeight());
		hasSettings = !feature.getSettings().isEmpty();
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton)
	{
		if(hasSettings && ((mouseX > getX() + getWidth() - 12
				|| feature.getPrimaryAction().isEmpty()) || mouseButton == 1))
		{
			if(isSettingsWindowOpen())
				closeSettingsWindow();
			else
				openSettingsWindow();

			return;
		}

		TooManyHaxHack tooManyHax =
				BurstClient.INSTANCE.getHax().getTooManyHaxHack();
		if(tooManyHax.isEnabled() && tooManyHax.isBlocked(feature))
		{
			ChatUtils.error(feature.getName() + " is blocked by TooManyHax.");
			return;
		}

		feature.doPrimaryAction();
	}

	public boolean isSettingsWindowOpen()
	{
		return settingsWindow != null && !settingsWindow.isClosing();
	}

	public void openSettingsWindow()
	{
		settingsWindow = new SettingsWindow(feature, getParent(), getY());
		GUI.addWindow(settingsWindow);
	}

	public void closeSettingsWindow()
	{
		settingsWindow.close();
		settingsWindow = null;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
					   float partialTicks)
	{
		int x1 = getX();
		int x2 = x1 + getWidth();
		int x3 = hasSettings ? x2 - 11 : x2;
		int y1 = getY();
		int y2 = y1 + getHeight();

		boolean hovering = isHovering(mouseX, mouseY, x1, x2, y1, y2);
		boolean hHack = hovering && mouseX < x3;
		boolean hSettings = hovering && mouseX >= x3;

		if(hHack)
			setTooltip();

		drawButtonBackground(x1, x3, y1, y2, hHack);

		if(hasSettings)
			drawSettingsBackground(x2, x3, y1, y2, hSettings);

		drawOutline(x1, x2, y1, y2);

		if(hasSettings)
		{
			drawSeparator(x3, y1, y2);
			drawSettingsArrow(x2, x3, y1, y2, hSettings);
		}

		drawName(matrixStack, x1, x3, y1);
	}

	public boolean isHovering(int mouseX, int mouseY, int x1, int x2, int y1,
							  int y2)
	{
		Window parent = getParent();
		boolean scrollEnabled = parent.isScrollingEnabled();
		int scroll = scrollEnabled ? parent.getScrollOffset() : 0;

		return mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2
				&& mouseY >= -scroll && mouseY < parent.getHeight() - 13 - scroll;
	}

	public void setTooltip()
	{
		String tooltip = feature.getDescription();

		// if(feature.isBlocked())
		// {
		// if(tooltip == null)
		// tooltip = "";
		// else
		// tooltip += "\n\n";
		// tooltip +=
		// "Your current YesCheat+ profile is blocking this feature.";
		// }

		GUI.setTooltip(tooltip);
	}

	public void drawButtonBackground(int x1, int x3, int y1, int y2,
									 boolean hHack)
	{
		float[] bgColor = GUI.getBgColor();
		float[] acColor = GUI.getAcColor();

		float opacity = GUI.getOpacity();

		GL11.glBegin(GL11.GL_QUADS);

		if(feature.isEnabled())
			// if(feature.isBlocked())
			// glColor4f(1, 0, 0, hHack ? opacity * 1.5F : opacity);
			// else
			//GL11.glColor4f(0, 1, 0, hHack ? opacity * 1.5F : opacity);
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 1);
		else
			GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2],
					hHack ? opacity * 1.5F : opacity);

		GL11.glVertex2i(x1, y1);
		GL11.glVertex2i(x1, y2);
		GL11.glVertex2i(x3, y2);
		GL11.glVertex2i(x3, y1);

		GL11.glEnd();
	}

	public void drawSettingsBackground(int x2, int x3, int y1, int y2,
									   boolean hSettings)
	{
		float[] bgColor = GUI.getBgColor();
		float opacity = GUI.getOpacity();

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2],
				hSettings ? opacity * 1.5F : opacity);
		GL11.glVertex2i(x3, y1);
		GL11.glVertex2i(x3, y2);
		GL11.glVertex2i(x2, y2);
		GL11.glVertex2i(x2, y1);
		GL11.glEnd();
	}

	public void drawOutline(int x1, int x2, int y1, int y2)
	{
		float[] acColor = GUI.getAcColor();

		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5F);
		GL11.glVertex2i(x1, y1);
		GL11.glVertex2i(x1, y2);
		GL11.glVertex2i(x2, y2);
		GL11.glVertex2i(x2, y1);
		GL11.glEnd();
	}

	public void drawSeparator(int x3, int y1, int y2)
	{
		// separator
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(x3, y1);
		GL11.glVertex2i(x3, y2);
		GL11.glEnd();
	}

	public void drawSettingsArrow(int x2, int x3, int y1, int y2,
								  boolean hSettings)
	{
		double xa1 = x3;
		double xa2 = x2;
		double ya1;
		double ya2;

		if(isSettingsWindowOpen())
		{
			ya1 = y2;
			ya2 = y1;
			float[] accColor = BurstClient.INSTANCE.getGui().getAcColor();
			GL11.glColor4f(accColor[0], accColor[1], accColor[2], 1);

		}else
		{
			ya1 = y1;
			ya2 = y2;
			float[] bgColor = BurstClient.INSTANCE.getGui().getBgColor();
			GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], 0);
		}

		// colored rectangle
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glVertex2d(xa1, ya1);
		GL11.glVertex2d(xa1, ya2);
		GL11.glVertex2d(xa2, ya2);
		GL11.glVertex2d(xa2, ya1);
		GL11.glEnd();
	}

	public void drawName(MatrixStack matrixStack, int x1, int x3, int y1)
	{
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		TextRenderer tr = MC.textRenderer;
		String name = feature.getName();
		int nameWidth = tr.getWidth(name);
		int tx = x1 + (x3 - x1 - nameWidth) / 2;
		int ty = y1 + getHeight() / 4;

		//https://stackoverflow.com/questions/4801366/convert-rgb-values-to-integer
		int[] txtColor = BurstClient.INSTANCE.getHax().getClickGuiHack().getTxtColor();
		int rgb = txtColor[0];
		rgb = (rgb << 8) + txtColor[1];
		rgb = (rgb << 8) + txtColor[2];

		tr.draw(matrixStack, name, tx, ty, rgb);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
	}

	@Override
	public int getDefaultWidth()
	{
		String name = feature.getName();
		TextRenderer tr = MC.textRenderer;
		int width = tr.getWidth(name) + 4;
		if(hasSettings)
			width += 11;

		return width;
	}

	@Override
	public int getDefaultHeight()
	{
		return 20;
	}
}
