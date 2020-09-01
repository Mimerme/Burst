/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.burstclient.AutoExecProcessor;
import io.github.burstclient.EvalError;
import io.github.burstclient.EvalScreen;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import net.minecraft.client.gui.hud.InGameHud;
import net.wurstclient.hacks.RainbowUiHack;
import net.wurstclient.util.ForceOpDialog;
import net.wurstclient.util.MultiProcessingUtils;
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
import javax.swing.*;

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

	private AutoExecProcessor autoExecer;

	private boolean enabled = true;

	//Since we can't intialize clickGuis before the game initialize's its TextRenderer
	//we need to initalize the gui whenever a value read occurs and it still isn't initialized
	public static boolean guiInitialized;
	private WurstUpdater updater;
	private Path wurstFolder;

	private KeyBinding zoomKey;

	//JS engine stuff
/*
	public static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	public static Invocable invoker = (Invocable) engine;
*/

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

		if(gui == null) {
			System.out.println("clickgui.js failed to initialize. Using fallback");
			gui = new ClickGui(guiFile);
		}

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

		eventManager.add(GUIRenderListener.class, )


		//Load the clickGui object if there exists one
		File ingameFile = new File("ingamehud.js");
		if (ingameFile.exists())
			hud = loadInGameHud("ingamehud.js");
		else
			hud = new IngameHUD();

		if(hud == null) {
			System.out.println("ingamehud.js failed to initialize. Using fallback");
			hud = new IngameHUD();
		}

		autoExecer = new AutoExecProcessor();
		eventManager.add(GUIRenderListener.class, autoExecer);

		rotationFaker = new RotationFaker();
		eventManager.add(PreMotionListener.class, rotationFaker);
		eventManager.add(PostMotionListener.class, rotationFaker);

		Path altsFile = wurstFolder.resolve("alts.encrypted_json");
		Path encFolder = createEncryptionFolder();
		altManager = new AltManager(altsFile, encFolder);
	}

	//Purge all the registered events associated with the current initialized features
	public void purgeEvents(){
		eventManager.remove(ChatOutputListener.class, cmdProcessor);
		eventManager.remove(GUIRenderListener.class, hud);

		eventManager.remove(PreMotionListener.class, rotationFaker);
		eventManager.remove(PostMotionListener.class, rotationFaker);
		eventManager.remove(GUIRenderListener.class, autoExecer);
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
			try {
				guiInitialized = true;
				gui.init();
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				guiInitialized = false;
				System.out.println(sw.toString());

				try {
					Process process = MultiProcessingUtils.startProcessWithIO(
							EvalError.class, sw.toString());
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}


				gui = new ClickGui();
				System.out.println("clickgui.js exception. falling back to default");
			}
		}

		return gui;
	}

	public ClickGui loadClickGui(String filename){
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Invocable invoker = (Invocable) engine;

		try {
			engine.eval(new FileReader(filename));
			ClickGui modObj = (ClickGui) invoker.invokeFunction("gui");
			System.out.println("Successfully loaded " + filename);
			return modObj;
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

			System.out.println(sw);
			System.out.println("failed to load clickgui.js. falling back to default");
		}

		//If loading the new gui fails just return the old gui
		//TODO: this causes the guis to stack
		return null;
	}


	public IngameHUD loadInGameHud(String filename){
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Invocable invoker = (Invocable) engine;

		try {
			engine.eval(new FileReader(filename));
			IngameHUD hudObj = (IngameHUD) invoker.invokeFunction("hud");
			System.out.println("Successfully loaded " + filename);
			return hudObj;
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

			System.out.println(sw);
			System.out.println("failed to load ingamehud.js. falling back to default");
		}

		//If loading the new gui fails just return the old gui
		//TODO: this causes the guis to stack
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
