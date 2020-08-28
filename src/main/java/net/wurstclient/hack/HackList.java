/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.wurstclient.BurstClient;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hacks.*;
import net.wurstclient.util.json.JsonException;
import org.reflections.Reflections;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;



public final class HackList implements UpdateListener
{
	//JS engine stuff
	private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	Invocable invoker = (Invocable) engine;

	private final TreeMap<String, Hack> hax =
		new TreeMap<>((o1, o2) -> o1.compareToIgnoreCase(o2));
	
	private final EnabledHacksFile enabledHacksFile;
	private final Path profilesFolder =
		BurstClient.INSTANCE.getWurstFolder().resolve("enabled hacks");
	
	private final EventManager eventManager =
		BurstClient.INSTANCE.getEventManager();

	public void loadJava(String packageName){
		System.out.println("Loading Java hacks from \'" + packageName + "\'");
		Reflections reflections = new Reflections(packageName);

		Set<Class<? extends Hack>> subTypes = reflections.getSubTypesOf(Hack.class);

		//All the mods in the hashmap are referenced via their keys and are gaurenteed to have a name/desc
		for(Class<?> modClass : subTypes){
			//Create new instances of each mod
			try {
				Hack mod = (Hack) modClass.newInstance();

				String modName = mod.getName();

				if (hax.containsKey(modName))
					throw new RuntimeException("Duplicate mod \'" + modName + "\'");

				hax.put(modName, mod);

				System.out.println("Successfully loaded \'" + modName + "\' module");
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadJs(String jsDirectory){
		System.out.println("Loading JS mods from \'" + System.getProperty("user.dir") + "/" + jsDirectory + "\' vis Nashorn");

		File folder = new File(jsDirectory);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				//TODO: error handling is a little harsh rn. Loosen it up
				try {
					engine.eval(new FileReader(fileEntry));
					Hack modObj = (Hack) invoker.invokeFunction("hack");
					hax.put(modObj.getName(), modObj);
					System.out.println("Successfully loaded \'" + fileEntry.getName() + "\' module");
				} catch (ScriptException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public HackList(Path enabledHacksFile)
	{
		this.enabledHacksFile = new EnabledHacksFile(enabledHacksFile);
		
		try
		{
			loadJava("net.wurstclient.hacks");
			//loadJs("hacks");

		}catch(Exception e)
		{
			String message = "Initializing Wurst hacks";
			CrashReport report = CrashReport.create(e, message);
			throw new CrashException(report);
		}
		
		eventManager.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		enabledHacksFile.load(this);
		eventManager.remove(UpdateListener.class, this);
	}
	
	public void saveEnabledHax()
	{
		enabledHacksFile.save(this);
	}
	
	public Hack getHackByName(String name)
	{
		return hax.get(name);
	}
	
	public Collection<Hack> getAllHax()
	{
		return Collections.unmodifiableCollection(hax.values());
	}
	
	public int countHax()
	{
		return hax.size();
	}
	
	public ArrayList<Path> listProfiles()
	{
		if(!Files.isDirectory(profilesFolder))
			return new ArrayList<>();
		
		try(Stream<Path> files = Files.list(profilesFolder))
		{
			return files.filter(Files::isRegularFile)
				.collect(Collectors.toCollection(() -> new ArrayList<>()));
			
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void loadProfile(String fileName) throws IOException, JsonException
	{
		enabledHacksFile.loadProfile(this, profilesFolder.resolve(fileName));
	}
	
	public void saveProfile(String fileName) throws IOException, JsonException
	{
		enabledHacksFile.saveProfile(this, profilesFolder.resolve(fileName));
	}

	//TODO: these methods are here for legacy support.
	// Try to get rid of these eventually
	public TooManyHaxHack getTooManyHaxHack(){
		return (TooManyHaxHack) hax.get("TooManyHax");
	}

	public FlightHack getFlightHack() {
		return (FlightHack) hax.get("Flight");
	}

	public PanicHack getPanicHack() {
		return (PanicHack) hax.get("Panic");
	}

	public ClickGuiHack getClickGuiHack() {
		return (ClickGuiHack) hax.get("ClickGui");
	}

	public RainbowUiHack getRainbowUiHack() {return (RainbowUiHack) hax.get("RainbowUi"); }

	public JetpackHack getJetpackHack() {return (JetpackHack) hax.get("Jetpack"); }
	public BlinkHack getBlinkHack() {return (BlinkHack) hax.get("Blink"); }

	public ExcavatorHack getExcavatorHack() {return (ExcavatorHack) hax.get("Excavator"); }

	public FollowHack getFollowHack() {return (FollowHack) hax.get("Follow"); }
	public ProtectHack getProtectHack() {return (ProtectHack) hax.get("Protect"); }

	public RemoteViewHack getRemotViewHack() {return (RemoteViewHack) hax.get("RemoteView"); }

	public JesusHack getJesusHack() {return (JesusHack) hax.get("Jesus"); }

	public FreecamHack getFreecamHack() {return (FreecamHack) hax.get("Freecam"); }

	public XRayHack getXRayHack() {return (XRayHack) hax.get("XRay"); }

	public TimerHack getTimerHack() {return (TimerHack) hax.get("Timer"); }

	public NoFireOverlayHack getNoFireOverlayHack() {return (NoFireOverlayHack) hax.get("NoFireOverlay"); }

	public NoOverlayHack getNoOverlayHack() {return (NoOverlayHack) hax.get("NoOverlay"); }

	public NoPumpkinHack getNoPumpkinHack() {return (NoPumpkinHack) hax.get("NoPumpkin"); }

	public NameTagsHack getNameTagsHack() {return (NameTagsHack) hax.get("NameTags");}
	public HealthTagsHack getHealTagsHack() {return (HealthTagsHack) hax.get("HealthTags");}

	public AutoReconnectHack getAutoReconnectHack() {return (AutoReconnectHack) hax.get("AutoReconnect");}
	public NoSlowdownHack getNoSlowdownHack() {return (NoSlowdownHack) hax.get("NoSlowdown");}
	public StepHack getStepHack() {return (StepHack) hax.get("Step");}

	public AutoStealHack getAutoStealHack() {return (AutoStealHack) hax.get("AutoSteal");}
	public NoWeatherHack getNoWeatherHack() {return (NoWeatherHack) hax.get("NoWeather");}


	public AutoSignHack getAutoSignHack() {return (AutoSignHack) hax.get("AutoSign");}



}
