/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.events;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import io.github.burstclient.EvalError;
import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.BurstClient;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;
import net.wurstclient.util.MultiProcessingUtils;

public interface GUIRenderListener extends Listener
{
	public void onRenderGUI(MatrixStack matrixStack, float partialTicks);
	
	public static class GUIRenderEvent extends Event<GUIRenderListener>
	{
		private final float partialTicks;
		private final MatrixStack matrixStack;
		
		public GUIRenderEvent(MatrixStack matrixStack, float partialTicks)
		{
			this.matrixStack = matrixStack;
			this.partialTicks = partialTicks;
		}
		
		@Override
		public void fire(ArrayList<GUIRenderListener> listeners)
		{
			for(GUIRenderListener listener : listeners) {
				try {
					listener.onRenderGUI(matrixStack, partialTicks);
				}
				catch (Exception e) {
					BurstClient.INSTANCE.fallbackHud();

					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);

					try {
						Process process = MultiProcessingUtils.startProcessWithIO(
								EvalError.class, sw.toString());
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					BurstClient.INSTANCE.fallback();
					System.out.println(sw);
					System.out.println("failed to call onGuiRender in ingamehud.js. falling back to default");

				}
			}
		}
		
		@Override
		public Class<GUIRenderListener> getListenerType()
		{
			return GUIRenderListener.class;
		}
	}
}
