/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hud;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.wurstclient.clickgui.components.FeatureButton;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.Feature;
import net.wurstclient.BurstClient;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.events.KeyPressListener;
import net.wurstclient.hacks.TooManyHaxHack;
import net.wurstclient.other_features.TabGuiOtf;
import net.wurstclient.util.ChatUtils;

public class TabGui implements KeyPressListener
{
	public ArrayList<Tab> getTabs() {
		return tabs;
	}

	public final ArrayList<Tab> tabs = new ArrayList<>();
	public final TabGuiOtf tabGuiOtf =
		BurstClient.INSTANCE.getOtfs().tabGuiOtf;

	public TabGuiOtf getTabGuiOtf() {
		return tabGuiOtf;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getSelected() {
		return selected;
	}

	public boolean isTabOpened() {
		return tabOpened;
	}

	public int width;
	public int height;
	public int selected;
	public boolean tabOpened;
	
	public TabGui()
	{
		BurstClient.INSTANCE.getEventManager().add(KeyPressListener.class,
			this);
		
		LinkedHashMap<String, Tab> tabMap = new LinkedHashMap<>();

		
		ArrayList<Feature> features = new ArrayList<>();
		features.addAll(BurstClient.INSTANCE.getHax().getAllHax());
		features.addAll(BurstClient.INSTANCE.getCmds().getAllCmds());
		features.addAll(BurstClient.INSTANCE.getOtfs().getAllOtfs());

		//Changed from Wurst, similar to ClickGUI
		for(Feature f : features) {
			String category = f.getCategory();
			if (category != null) {
				//Initialize tab gui windows here
				//Since the Category enum was removed initialize the windows based on the initialized hax
				if (!tabMap.containsKey(category))
					tabMap.put(category, new Tab(category));

				tabMap.get(f.getCategory()).add(f);
			}
		}
			
		tabs.addAll(tabMap.values());
		tabs.forEach(tab -> tab.updateSize());
		updateSize();
	}

	public void destroy(){
		BurstClient.INSTANCE.getEventManager().remove(KeyPressListener.class,
				this);
	}
	
	public void updateSize()
	{
		width = 64;
		for(Tab tab : tabs)
		{
			int tabWidth = BurstClient.MC.textRenderer.getWidth(tab.name) + 10;
			if(tabWidth > width)
				width = tabWidth;
		}
		height = tabs.size() * 10;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if(event.getAction() != GLFW.GLFW_PRESS)
			return;

		if(tabGuiOtf.isHidden())
			return;
		
		if(tabOpened)
			switch(event.getKeyCode())
			{
				case GLFW.GLFW_KEY_LEFT:
				tabOpened = false;
				break;
				
				default:
				tabs.get(selected).onKeyPress(event.getKeyCode());
				break;
			}
		else
			switch(event.getKeyCode())
			{
				case GLFW.GLFW_KEY_DOWN:
				if(selected < tabs.size() - 1)
					selected++;
				else
					selected = 0;
				break;
				
				case GLFW.GLFW_KEY_UP:
				if(selected > 0)
					selected--;
				else
					selected = tabs.size() - 1;
				break;
				
				case GLFW.GLFW_KEY_RIGHT:
				tabOpened = true;
				break;
			}
	}
	
	public void render(MatrixStack matrixStack, float partialTicks)
	{
		if(tabGuiOtf.isHidden())
			return;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		GL11.glPushMatrix();
		Window sr = BurstClient.MC.getWindow();
		
		int x = 2;
		int y = 23;
		
		GL11.glTranslatef(x, y, 0);
		drawBox(0, 0, width, height);
		
		double factor = sr.getScaleFactor();
		GL11.glScissor((int)(x * factor),
			(int)((sr.getScaledHeight() - height - y) * factor),
			(int)(width * factor), (int)(height * factor));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		
		int textY = 1;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		for(int i = 0; i < tabs.size(); i++)
		{
			String tabName = tabs.get(i).name;
			if(i == selected)
				tabName = (tabOpened ? "<" : ">") + tabName;
			
			BurstClient.MC.textRenderer.draw(matrixStack, tabName, 2, textY,
				0xffffffff);
			textY += 10;
		}
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		if(tabOpened)
		{
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			Tab tab = tabs.get(selected);
			int tabX = x + width + 2;
			int tabY = y;
			
			GL11.glTranslatef(width + 2, 0, 0);
			drawBox(0, 0, tab.width, tab.height);
			
			GL11.glScissor((int)(tabX * factor),
				(int)((sr.getScaledHeight() - tab.height - tabY) * factor),
				(int)(tab.width * factor), (int)(tab.height * factor));
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			
			int tabTextY = 1;
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			for(int i = 0; i < tab.features.size(); i++)
			{
				Feature feature = tab.features.get(i);
				String fName = feature.getName();
				
				if(feature.isEnabled())
					fName = "\u00a7a" + fName + "\u00a7r";
				
				if(i == tab.selected)
					fName = ">" + fName;
				
				BurstClient.MC.textRenderer.draw(matrixStack, fName, 2,
					tabTextY, 0xffffffff);
				tabTextY += 10;
			}
			GL11.glEnable(GL11.GL_BLEND);
			
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public void drawBox(int x1, int y1, int x2, int y2)
	{
		ClickGui gui = BurstClient.INSTANCE.getGui();
		float[] bgColor = gui.getBgColor();
		float[] acColor = gui.getAcColor();
		float opacity = gui.getOpacity();
		
		// color
		GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
		
		// box
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2i(x1, y1);
			GL11.glVertex2i(x2, y1);
			GL11.glVertex2i(x2, y2);
			GL11.glVertex2i(x1, y2);
		}
		GL11.glEnd();
		
		// outline positions
		double xi1 = x1 - 0.1;
		double xi2 = x2 + 0.1;
		double yi1 = y1 - 0.1;
		double yi2 = y2 + 0.1;
		
		// outline
		GL11.glLineWidth(1);
		GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5F);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(xi1, yi1);
			GL11.glVertex2d(xi2, yi1);
			GL11.glVertex2d(xi2, yi2);
			GL11.glVertex2d(xi1, yi2);
		}
		GL11.glEnd();
		
		// shadow positions
		xi1 -= 0.9;
		xi2 += 0.9;
		yi1 -= 0.9;
		yi2 += 0.9;
		
		// top left
		GL11.glBegin(GL11.GL_POLYGON);
		{
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75F);
			GL11.glVertex2d(x1, y1);
			GL11.glVertex2d(x2, y1);
			GL11.glColor4f(0, 0, 0, 0);
			GL11.glVertex2d(xi2, yi1);
			GL11.glVertex2d(xi1, yi1);
			GL11.glVertex2d(xi1, yi2);
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75F);
			GL11.glVertex2d(x1, y2);
		}
		GL11.glEnd();
		
		// bottom right
		GL11.glBegin(GL11.GL_POLYGON);
		{
			GL11.glVertex2d(x2, y2);
			GL11.glVertex2d(x2, y1);
			GL11.glColor4f(0, 0, 0, 0);
			GL11.glVertex2d(xi2, yi1);
			GL11.glVertex2d(xi2, yi2);
			GL11.glVertex2d(xi1, yi2);
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75F);
			GL11.glVertex2d(x1, y2);
		}
		GL11.glEnd();
	}
	
	public static final class Tab
	{
		public final String name;
		public final ArrayList<Feature> features = new ArrayList<>();
		
		public int width;
		public int height;
		public int selected;
		
		public Tab(String name)
		{
			this.name = name;
		}
		
		public void updateSize()
		{
			if (BurstClient.MC.textRenderer == null)
				return;

			width = 64;
			for(Feature feature : features)
			{
				int fWidth =
					BurstClient.MC.textRenderer.getWidth(feature.getName())
						+ 10;
				if(fWidth > width)
					width = fWidth;
			}
			height = features.size() * 10;
		}
		
		public void onKeyPress(int keyCode)
		{
			switch(keyCode)
			{
				case GLFW.GLFW_KEY_DOWN:
				if(selected < features.size() - 1)
					selected++;
				else
					selected = 0;
				break;
				
				case GLFW.GLFW_KEY_UP:
				if(selected > 0)
					selected--;
				else
					selected = features.size() - 1;
				break;
				
				case GLFW.GLFW_KEY_ENTER:
				onEnter();
				break;
			}
		}
		
		public void onEnter()
		{
			Feature feature = features.get(selected);
			
			TooManyHaxHack tooManyHax =
				BurstClient.INSTANCE.getHax().getTooManyHaxHack();
			if(tooManyHax.isEnabled() && tooManyHax.isBlocked(feature))
			{
				ChatUtils
					.error(feature.getName() + " is blocked by TooManyHax.");
				return;
			}
			
			feature.doPrimaryAction();
		}
		
		public void add(Feature feature)
		{
			features.add(feature);
		}
	}
}
