/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.command;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.util.ChatUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.wurstclient.commands.*;
import net.wurstclient.hack.Hack;
import net.wurstclient.util.ChatUtils;
import org.reflections.Reflections;

public final class CmdList
{
	private final TreeMap<String, Command> cmds =
		new TreeMap<>((o1, o2) -> o1.compareToIgnoreCase(o2));

	public Command get(String cmd){
		cmd = "." + cmd.toLowerCase();

		if (!cmds.containsKey(cmd))
			ChatUtils.message("Missing dependency \'" + cmd +"\'");
		else
			return cmds.get(cmd);

		return null;
	}

	public CmdList()
	{
		try
		{
			loadJava("net.wurstclient.commands");
			
		}catch(Exception e)
		{
			String message = "Initializing Wurst commands";
			CrashReport report = CrashReport.create(e, message);
			throw new CrashException(report);
		}
	}

	public void loadJava(String packageName){
		System.out.println("Loading Java cmds from \'" + packageName + "\'");
		Reflections reflections = new Reflections(packageName);

		Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);

		for(Class<?> modClass : subTypes){
			try {
				Command cmd = (Command) modClass.newInstance();
				cmd.initAnotations();

				String cmdName = cmd.getName();
				cmdName = cmdName.toLowerCase();

				if (cmds.containsKey(cmdName))
					throw new RuntimeException("Duplicate cmd \'" + cmdName + "\'");

				cmds.put(cmdName, cmd);

				System.out.println("Successfully loaded \'" + cmdName + "\' command");
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Command getCmdByName(String name)
	{
		return cmds.get("." + name);
	}
	
	public Collection<Command> getAllCmds()
	{
		return cmds.values();
	}
	
	public int countCmds()
	{
		return cmds.size();
	}

	public void addCommand(String name, Command cmd){
		cmds.put(name, cmd);
	}
}
