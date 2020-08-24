/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.Text;
import net.wurstclient.BurstClient;
import net.wurstclient.hacks.AutoSignHack;
import net.wurstclient.mixinterface.ISignBlockEntity;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen
{
	@Shadow
	private SignBlockEntity sign;
	
	private SignEditScreenMixin(BurstClient wurst, Text text_1)
	{
		super(text_1);
	}
	
	@Inject(at = {@At("HEAD")}, method = {"init()V"})
	private void onInit(CallbackInfo ci)
	{
		AutoSignHack autoSignHack = BurstClient.INSTANCE.getHax().getAutoSignHack();
		
		Text[] autoSignText = autoSignHack.getSignText();
		if(autoSignText == null)
			return;
		
		for(int i = 0; i < 4; i++)
			sign.setTextOnRow(i, autoSignText[i]);
		
		finishEditing();
	}
	
	@Inject(at = {@At("HEAD")}, method = {"finishEditing()V"})
	private void onFinishEditing(CallbackInfo ci)
	{
		Text[] allRows = ((ISignBlockEntity)sign).getTextOnAllRows();
		BurstClient.INSTANCE.getHax().getAutoSignHack().setSignText(allRows);
	}
	
	@Shadow
	private void finishEditing()
	{
		
	}
}
