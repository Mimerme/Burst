/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.wurstclient.BurstClient;

public enum ChatUtils
{
	;
	
	private static final MinecraftClient MC = BurstClient.MC;
	
	public static final String WURST_PREFIX =
		"\u00A72[\u00A73Burst\u00A72]\u00A72 ";
	private static final String WARNING_PREFIX =
		"\u00A72[\u00A73\u00a7lWARNING\u00A72]\u00a7 ";
	private static final String ERROR_PREFIX =
		"\u00A72[\u00A73\u00a7lERROR\u00A72]\u00a7 ";
	private static final String SYNTAX_ERROR_PREFIX =
		"\u00A72Syntax error:\u00a7 ";
	
	private static boolean enabled = true;
	
	public static void setEnabled(boolean enabled)
	{
		ChatUtils.enabled = enabled;
	}
	
	public static void component(Text component)
	{
		if(!enabled)
			return;
		
		ChatHud chatHud = MC.inGameHud.getChatHud();
		LiteralText prefix = new LiteralText(WURST_PREFIX);
		chatHud.addMessage(prefix.append(component));
	}
	
	public static void message(String message)
	{
		component(new LiteralText(message));
	}
	
	public static void warning(String message)
	{
		message(WARNING_PREFIX + message);
	}
	
	public static void error(String message)
	{
		message(ERROR_PREFIX + message);
	}
	
	public static void syntaxError(String message)
	{
		message(SYNTAX_ERROR_PREFIX + message);
	}
}
