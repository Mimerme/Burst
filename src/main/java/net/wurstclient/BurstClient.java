/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.wurstclient.hacks.RainbowUiHack;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;
import net.wurstclient.altmanager.AltManager;
import net.wurstclient.analytics.WurstAnalytics;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.command.CmdList;
import net.wurstclient.command.CmdProcessor;
import net.wurstclient.command.Command;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.KeyPressListener;
import net.wurstclient.events.PostMotionListener;
import net.wurstclient.events.PreMotionListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.hack.HackList;
import net.wurstclient.hud.IngameHUD;
import net.wurstclient.keybinds.KeybindList;
import net.wurstclient.keybinds.KeybindProcessor;
import net.wurstclient.mixinterface.IMinecraftClient;
import net.wurstclient.navigator.Navigator;
import net.wurstclient.other_feature.OtfList;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.SettingsFile;
import net.wurstclient.update.WurstUpdater;
import net.wurstclient.util.json.JsonException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public enum BurstClient
{
	INSTANCE;
	
	public static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final IMinecraftClient IMC = (IMinecraftClient)MC;
	
	public static final String VERSION = "7.5";
	public static final String MC_VERSION = "1.16.2";
	
	private EventManager eventManager;
	private AltManager altManager;
	private HackList hax;
	private CmdList cmds;
	private OtfList otfs;
	private SettingsFile settingsFile;
	private Path settingsProfileFolder;
	private KeybindList keybinds;
	private ClickGui gui;
	private Navigator navigator;
	private CmdProcessor cmdProcessor;
	private IngameHUD hud;
	private RotationFaker rotationFaker;
	private FriendsList friends;
	
	private boolean enabled = true;

	//Since we can't intialize clickGuis before the game initialize's its TextRenderer
	//we need to initalize the gui whenever a value read occurs and it still isn't initialized
	public static boolean guiInitialized;
	private WurstUpdater updater;
	private Path wurstFolder;
	
	private KeyBinding zoomKey;

	//JS engine stuff
	public static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	public static Invocable invoker = (Invocable) engine;

	public void loadFeatures(){
		Path enabledHacksFile = wurstFolder.resolve("enabled-hacks.json");
		hax = new HackList(enabledHacksFile);
		cmds = new CmdList();
		otfs = new OtfList();

		Path settingsFile = wurstFolder.resolve("settings.json");
		settingsProfileFolder = wurstFolder.resolve("settings");
		this.settingsFile = new SettingsFile(settingsFile, hax, cmds, otfs);
		this.settingsFile.load();
		hax.getTooManyHaxHack().loadBlockedHacksFile();

		Path keybindsFile = wurstFolder.resolve("keybinds.json");
		keybinds = new KeybindList(keybindsFile);

		Path guiFile = wurstFolder.resolve("windows.json");

		//Load the clickGui object if there exists one
		File clickFile = new File("clickgui.js");
		if (clickFile.exists())
			gui = loadClickGui("clickgui.js");
		else
			gui = new ClickGui(guiFile);

		Path preferencesFile = wurstFolder.resolve("preferences.json");
		navigator = new Navigator(preferencesFile, hax, cmds, otfs);

		Path friendsFile = wurstFolder.resolve("friends.json");
		friends = new FriendsList(friendsFile);
		friends.load();

		cmdProcessor = new CmdProcessor(cmds);
		eventManager.add(ChatOutputListener.class, cmdProcessor);

		KeybindProcessor keybindProcessor =
				new KeybindProcessor(hax, keybinds, cmdProcessor);
		eventManager.add(KeyPressListener.class, keybindProcessor);

		hud = new IngameHUD();
		eventManager.add(GUIRenderListener.class, hud);

/*		rotationFaker = new RotationFaker();
		eventManager.add(PreMotionListener.class, rotationFaker);
		eventManager.add(PostMotionListener.class, rotationFaker);*/

		Path altsFile = wurstFolder.resolve("alts.encrypted_json");
		Path encFolder = createEncryptionFolder();
		altManager = new AltManager(altsFile, encFolder);
	}

	//Purge all the registered events associated with the current initialized features
	public void purgeEvents(){
		eventManager.remove(ChatOutputListener.class, cmdProcessor);
		eventManager.remove(GUIRenderListener.class, hud);
	}

	public void initialize()
	{
		System.out.println("Starting Wurst Client...");
		
		wurstFolder = createWurstFolder();

		eventManager = new EventManager(this);

		loadFeatures();
		
		zoomKey = new KeyBinding("key.wurst.zoom", InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V, "Zoom");
		KeyBindingHelper.registerKeyBinding(zoomKey);
	}
	
	private Path createWurstFolder()
	{
		Path dotMinecraftFolder = MC.runDirectory.toPath().normalize();
		Path wurstFolder = dotMinecraftFolder.resolve("wurst");
		
		try
		{
			Files.createDirectories(wurstFolder);
			
		}catch(IOException e)
		{
			throw new RuntimeException(
				"Couldn't create .minecraft/wurst folder.", e);
		}
		
		return wurstFolder;
	}
	
	private Path createEncryptionFolder()
	{
		Path encFolder =
			Paths.get(System.getProperty("user.home"), ".Wurst encryption")
				.normalize();
		
		try
		{
			Files.createDirectories(encFolder);
			if(Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS)
				Files.setAttribute(encFolder, "dos:hidden", true);
			
			Path readme = encFolder.resolve("READ ME I AM VERY IMPORTANT.txt");
			String readmeText = "DO NOT SHARE THESE FILES WITH ANYONE!\r\n"
				+ "They are encryption keys that protect your alt list file from being read by someone else.\r\n"
				+ "If someone is asking you to send these files, they are 100% trying to scam you.\r\n"
				+ "\r\n"
				+ "DO NOT EDIT, RENAME OR DELETE THESE FILES! (unless you know what you're doing)\r\n"
				+ "If you do, Wurst's Alt Manager can no longer read your alt list and will replace it with a blank one.\r\n"
				+ "In other words, YOUR ALT LIST WILL BE DELETED.";
			Files.write(readme, readmeText.getBytes("UTF-8"),
				StandardOpenOption.CREATE);
			
		}catch(IOException e)
		{
			throw new RuntimeException(
				"Couldn't create '.Wurst encryption' folder.", e);
		}
		
		return encFolder;
	}

	public EventManager getEventManager()
	{
		return eventManager;
	}
	
	public void saveSettings()
	{
		settingsFile.save();
	}
	
	public ArrayList<Path> listSettingsProfiles()
	{
		if(!Files.isDirectory(settingsProfileFolder))
			return new ArrayList<>();
		
		try(Stream<Path> files = Files.list(settingsProfileFolder))
		{
			return files.filter(Files::isRegularFile)
				.collect(Collectors.toCollection(() -> new ArrayList<>()));
			
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void loadSettingsProfile(String fileName)
		throws IOException, JsonException
	{
		settingsFile.loadProfile(settingsProfileFolder.resolve(fileName));
	}
	
	public void saveSettingsProfile(String fileName)
		throws IOException, JsonException
	{
		settingsFile.saveProfile(settingsProfileFolder.resolve(fileName));
	}
	
	public HackList getHax()
	{
		return hax;
	}
	
	public CmdList getCmds()
	{
		return cmds;
	}
	
	public OtfList getOtfs()
	{
		return otfs;
	}
	
	public Feature getFeatureByName(String name)
	{
		Hack hack = getHax().getHackByName(name);
		if(hack != null)
			return hack;
		
		Command cmd = getCmds().getCmdByName(name.substring(1));
		if(cmd != null)
			return cmd;
		
		OtherFeature otf = getOtfs().getOtfByName(name);
		if(otf != null)
			return otf;
		
		return null;
	}
	
	public KeybindList getKeybinds()
	{
		return keybinds;
	}

	public ClickGui getGui()
	{
		if(!guiInitialized)
		{
			guiInitialized = true;
			gui.init();
		}

		return gui;
	}

	public ClickGui loadClickGui(String jsDirectory){
		System.out.println("Loading new clickGui from \'" + System.getProperty("user.dir") + "/" + jsDirectory + "\' via Nashorn");

		File folder = new File(jsDirectory);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				//TODO: error handling is a little harsh rn. Loosen it up
				try {
					engine.eval(new FileReader(fileEntry));
					ClickGui modObj = (ClickGui) invoker.invokeFunction("gui");
					System.out.println("Successfully new click gui \'" + fileEntry.getName() + "\' module");
					return modObj;
				} catch (ScriptException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}


	public Navigator getNavigator()
	{
		return navigator;
	}
	
	public CmdProcessor getCmdProcessor()
	{
		return cmdProcessor;
	}
	
	public IngameHUD getHud()
	{
		return hud;
	}
	
	public RotationFaker getRotationFaker()
	{
		return rotationFaker;
	}
	
	public FriendsList getFriends()
	{
		return friends;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		
		if(!enabled)
		{
			hax.getPanicHack().setEnabled(true);
			hax.getPanicHack().onUpdate();
		}
	}
	
	public WurstUpdater getUpdater()
	{
		return updater;
	}
	
	public Path getWurstFolder()
	{
		return wurstFolder;
	}
	
	public KeyBinding getZoomKey()
	{
		return zoomKey;
	}
	
	public AltManager getAltManager()
	{
		return altManager;
	}

}
