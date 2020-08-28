/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.Text;
import net.wurstclient.BurstClient;
import net.wurstclient.hacks.AutoSignHack;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen
{
	@Shadow
	@Final
	private String[] field_24285;
	
	private SignEditScreenMixin(BurstClient wurst, Text text_1)
	{
		super(text_1);
	}
	
	@Inject(at = {@At("HEAD")}, method = {"init()V"})
	private void onInit(CallbackInfo ci)
	{
		AutoSignHack autoSignHack = BurstClient.INSTANCE.getHax().getAutoSignHack();
		
		String[] autoSignText = autoSignHack.getSignText();
		if(autoSignText == null)
			return;
		
		field_24285 = autoSignText;
		finishEditing();
	}
	
	@Inject(at = {@At("HEAD")}, method = {"finishEditing()V"})
	private void onFinishEditing(CallbackInfo ci)
	{
		BurstClient.INSTANCE.getHax().getAutoSignHack().setSignText(field_24285);
	}
	
	@Shadow
	private void finishEditing()
	{
		
	}
}
