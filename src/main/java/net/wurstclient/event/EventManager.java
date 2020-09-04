/*
 * Copyright (C) 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import io.github.burstclient.EvalError;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.wurstclient.BurstClient;
import net.wurstclient.util.MultiProcessingUtils;

public final class EventManager
{
	private final BurstClient wurst;
	private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap =
		new HashMap<>();
	
	public EventManager(BurstClient wurst)
	{
		this.wurst = wurst;
	}
	
	public <L extends Listener, E extends Event<L>> void fire(E event)
	{
		if(!wurst.isEnabled())
			return;
		
		try
		{
			Class<L> type = event.getListenerType();
			@SuppressWarnings("unchecked")
			ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(type);
			
			if(listeners == null || listeners.isEmpty())
				return;
				
			// Creating a copy of the list to avoid concurrent modification
			// issues.
			ArrayList<L> listeners2 = new ArrayList<>(listeners);
			
			// remove() sets an element to null before removing it. When one
			// thread calls remove() while another calls fire(), it is possible
			// for this list to contain null elements, which need to be filtered
			// out.
			listeners2.removeIf(Objects::isNull);
			
			event.fire(listeners2);
			
		}catch(Exception e)
		{
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
		}
	}
	
	public <L extends Listener> void add(Class<L> type, L listener)
	{
		try
		{
			@SuppressWarnings("unchecked")
			ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(type);
			
			if(listeners == null)
			{
				listeners = new ArrayList<>(Arrays.asList(listener));
				listenerMap.put(type, listeners);
				return;
			}
			
			listeners.add(listener);
			
		}catch(Throwable e)
		{
			e.printStackTrace();
			
			CrashReport report =
				CrashReport.create(e, "Adding Wurst event listener");
			CrashReportSection section = report.addElement("Affected listener");
			section.add("Listener type", () -> type.getName());
			section.add("Listener class", () -> listener.getClass().getName());
			
			throw new CrashException(report);
		}
	}

	public void clear(){
		listenerMap.clear();
	}
	
	public <L extends Listener> void remove(Class<L> type, L listener)
	{
		try
		{
			@SuppressWarnings("unchecked")
			ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(type);
			
			if(listeners != null)
				listeners.remove(listener);
			
		}catch(Throwable e)
		{
			e.printStackTrace();
			
			CrashReport report =
				CrashReport.create(e, "Removing Wurst event listener");
			CrashReportSection section = report.addElement("Affected listener");
			section.add("Listener type", () -> type.getName());
			section.add("Listener class", () -> listener.getClass().getName());
			
			throw new CrashException(report);
		}
	}
}
