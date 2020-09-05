/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.command;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import io.github.burstclient.EvalError;
import io.github.burstclient.cmds.*;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.wurstclient.BurstClient;
import net.wurstclient.commands.*;
import net.wurstclient.hack.Hack;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.MultiProcessingUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public final class CmdList
{
	public final AddAltCmd addAltCmd = new AddAltCmd();
	public final AnnoyCmd annoyCmd = new AnnoyCmd();
	public final AuthorCmd authorCmd = new AuthorCmd();
	public final BindCmd bindCmd = new BindCmd();
	public final BindsCmd bindsCmd = new BindsCmd();
	public final BlinkCmd blinkCmd = new BlinkCmd();
	public final ClearCmd clearCmd = new ClearCmd();
	public final CopyItemCmd copyitemCmd = new CopyItemCmd();
	public final DamageCmd damageCmd = new DamageCmd();
	public final DigCmd digCmd = new DigCmd();
	public final DropCmd dropCmd = new DropCmd();
	public final EnabledHaxCmd enabledHaxCmd = new EnabledHaxCmd();
	public final EnchantCmd enchantCmd = new EnchantCmd();
	public final ExcavateCmd excavateCmd = new ExcavateCmd();
	public final FeaturesCmd featuresCmd = new FeaturesCmd();
	public final FollowCmd followCmd = new FollowCmd();
	public final FriendsCmd friendsCmd = new FriendsCmd();
	public final GetPosCmd getPosCmd = new GetPosCmd();
	public final GiveCmd giveCmd = new GiveCmd();
	public final GmCmd gmCmd = new GmCmd();
	public final GoToCmd goToCmd = new GoToCmd();
	public final HelpCmd helpCmd = new HelpCmd();
	public final InvseeCmd invseeCmd = new InvseeCmd();
	public final IpCmd ipCmd = new IpCmd();
	public final JumpCmd jumpCmd = new JumpCmd();
	public final LeaveCmd leaveCmd = new LeaveCmd();
	public final ModifyCmd modifyCmd = new ModifyCmd();
	public final PathCmd pathCmd = new PathCmd();
	public final PotionCmd potionCmd = new PotionCmd();
	public final ProtectCmd protectCmd = new ProtectCmd();
	public final RenameCmd renameCmd = new RenameCmd();
	public final RepairCmd repairCmd = new RepairCmd();
	public final RvCmd rvCmd = new RvCmd();
	public final SvCmd svCmd = new SvCmd();
	public final SayCmd sayCmd = new SayCmd();
	public final SetCheckboxCmd setCheckboxCmd = new SetCheckboxCmd();
	public final SetModeCmd setModeCmd = new SetModeCmd();
	public final SetSliderCmd setSliderCmd = new SetSliderCmd();
	public final SettingsCmd settingsCmd = new SettingsCmd();
	public final TacoCmd tacoCmd = new TacoCmd();
	public final TCmd tCmd = new TCmd();
	public final TooManyHaxCmd tooManyHaxCmd = new TooManyHaxCmd();
	public final TpCmd tpCmd = new TpCmd();
	public final UnbindCmd unbindCmd = new UnbindCmd();
	public final VClipCmd vClipCmd = new VClipCmd();
	public final ViewNbtCmd viewNbtCmd = new ViewNbtCmd();

	public final AliasCmd aliasCmd = new AliasCmd();
	public final ClickGuiCmd clickGuiCmd = new ClickGuiCmd();
	public final CwdCmd cwdCmd = new CwdCmd();
	public final EchoCmd echoCmd = new EchoCmd();
	public final ExecCmd execCmd = new ExecCmd();
	public final ModlistCmd modlistCmd = new ModlistCmd();
	public final ReinitCmd reinitCmd = new ReinitCmd();


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
			loadJs("scripts/cmds");
		}catch(Exception e)
		{
			String message = "Initializing Wurst commands";
			CrashReport report = CrashReport.create(e, message);
			throw new CrashException(report);
		}
	}

	public void loadJs(String jsDirectory){
		System.out.println("Loading JS cmds from \'" + System.getProperty("user.dir") + "\\" + jsDirectory + "\' vis Nashorn");

		File folder = new File(jsDirectory);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				try {
					ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
					Invocable invoker = (Invocable) engine;
					engine.eval(new FileReader(fileEntry));
					Command cmdObj = (Command) invoker.invokeFunction("cmd");
					cmdObj.initAnotations();
					cmdObj.init();
					cmds.put(cmdObj.getName(), cmdObj);
					System.out.println("Successfully loaded \'" + fileEntry.getName() + "\' module");
				} catch (Exception e) {
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
		}
	}

	public void loadJava(String packageName){
		System.out.println("Loading Java cmds from \'" + packageName + "\'");

		try
		{
			for(Field field : CmdList.class.getDeclaredFields())
			{
				if(!field.getName().endsWith("Cmd"))
					continue;

				Command cmd = (Command)field.get(this);
				cmd.initAnotations();
				cmds.put(cmd.getName(), cmd);
			}

		}catch(Exception e)
		{
			String message = "Initializing Wurst commands";
			CrashReport report = CrashReport.create(e, message);
			throw new CrashException(report);
		}

		/*Reflections reflections = new Reflections(packageName);

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
		}*/
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
