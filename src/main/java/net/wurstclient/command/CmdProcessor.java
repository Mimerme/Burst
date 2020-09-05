/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import io.github.burstclient.EvalError;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.wurstclient.BurstClient;
import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.hacks.TooManyHaxHack;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.MultiProcessingUtils;

public final class CmdProcessor implements ChatOutputListener
{
	private final CmdList cmds;
	
	public CmdProcessor(CmdList cmds)
	{
		this.cmds = cmds;
	}
	
	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(!BurstClient.INSTANCE.isEnabled())
			return;
		
		String message = event.getOriginalMessage().trim();
		if(!message.startsWith("."))
			return;
		
		event.cancel();
		process(message.substring(1));
	}
	
	public void process(String input)
	{
		try
		{
			Command cmd = parseCmd(input);
			
			TooManyHaxHack tooManyHax =
				BurstClient.INSTANCE.getHax().getTooManyHaxHack();
			if(tooManyHax.isEnabled() && tooManyHax.isBlocked(cmd))
			{
				ChatUtils.error(cmd.getName() + " is blocked by TooManyHax.");
				return;
			}
			
			runCmd(cmd, input);
			
		}catch(CmdNotFoundException e)
		{
			e.printToChat();
		}
	}
	
	private Command parseCmd(String input) throws CmdNotFoundException
	{
		List<String> splits = ArgumentTokenizer.tokenize(input);

		String cmdName = splits.get(0);
		Command cmd = cmds.getCmdByName(cmdName);
		
		if(cmd == null)
			throw new CmdNotFoundException(input);
		
		return cmd;
	}
	
	private void runCmd(Command cmd, String input)
	{
		List<String> splits = ArgumentTokenizer.tokenize(input);
		Object[] objs = splits.subList(1, splits.size()).toArray();
		String[] args = Arrays.copyOf(objs,
				objs.length,
				String[].class);


		try
		{
			cmd.call(args);
			
		}catch(CmdException e)
		{
			e.printToChat(cmd);
			
		}catch(Throwable e)
		{

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println(sw.toString());

			try {
				Process process = MultiProcessingUtils.startProcessWithIO(
						EvalError.class, sw.toString());
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	
	private static class CmdNotFoundException extends Exception
	{
		private final String input;
		
		public CmdNotFoundException(String input)
		{
			super();
			this.input = input;
		}
		
		public void printToChat()
		{
			String cmdName = input.split(" ")[0];
			ChatUtils.error("Unknown command: ." + cmdName);
			
			StringBuilder helpMsg = new StringBuilder();
			
			if(input.startsWith("/"))
			{
				helpMsg.append("Use \".say " + input + "\"");
				helpMsg.append(" to send it as a chat command.");
				
			}else
			{
				helpMsg.append("Type \".help\" for a list of commands or ");
				helpMsg.append("\".say ." + input + "\"");
				helpMsg.append(" to send it as a chat message.");
			}
			
			ChatUtils.message(helpMsg.toString());
		}
	}
}
