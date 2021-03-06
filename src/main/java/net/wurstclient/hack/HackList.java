/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hack;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.burstclient.EvalError;
import io.github.burstclient.hacks.EvalHack;
import io.github.burstclient.hacks.InventoryHudHack;
import io.github.burstclient.hacks.PlayerHudHack;
import io.github.burstclient.hacks.ScaffoldWalkHack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.wurstclient.BurstClient;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hacks.*;
import net.wurstclient.util.MultiProcessingUtils;
import net.wurstclient.util.json.JsonException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


public final class HackList implements UpdateListener
{
	//Not actually used. Just here to speed up the hotcode reloading
	public final AntiAfkHack antiAfkHack = new AntiAfkHack();
	public final AntiBlindHack antiBlindHack = new AntiBlindHack();
	public final AntiCactusHack antiCactusHack = new AntiCactusHack();
	public final AntiKnockbackHack antiKnockbackHack = new AntiKnockbackHack();
	public final AntiSpamHack antiSpamHack = new AntiSpamHack();
	public final AntiWaterPushHack antiWaterPushHack = new AntiWaterPushHack();
	public final AntiWobbleHack antiWobbleHack = new AntiWobbleHack();
	public final AutoArmorHack autoArmorHack = new AutoArmorHack();
	public final AutoBuildHack autoBuildHack = new AutoBuildHack();
	public final AutoDropHack autoDropHack = new AutoDropHack();
	public final AutoLeaveHack autoLeaveHack = new AutoLeaveHack();
	public final AutoEatHack autoEatHack = new AutoEatHack();
	public final AutoFarmHack autoFarmHack = new AutoFarmHack();
	public final AutoFishHack autoFishHack = new AutoFishHack();
	public final AutoMineHack autoMineHack = new AutoMineHack();
	public final AutoPotionHack autoPotionHack = new AutoPotionHack();
	public final AutoReconnectHack autoReconnectHack = new AutoReconnectHack();
	public final AutoRespawnHack autoRespawnHack = new AutoRespawnHack();
	public final AutoSignHack autoSignHack = new AutoSignHack();
	public final AutoSoupHack autoSoupHack = new AutoSoupHack();
	public final AutoSprintHack autoSprintHack = new AutoSprintHack();
	public final AutoStealHack autoStealHack = new AutoStealHack();
	public final AutoSwimHack autoSwimHack = new AutoSwimHack();
	public final AutoSwitchHack autoSwitchHack = new AutoSwitchHack();
	public final AutoSwordHack autoSwordHack = new AutoSwordHack();
	public final AutoToolHack autoToolHack = new AutoToolHack();
	public final AutoTotemHack autoTotemHack = new AutoTotemHack();
	public final AutoWalkHack autoWalkHack = new AutoWalkHack();
	public final BaseFinderHack baseFinderHack = new BaseFinderHack();
	public final BlinkHack blinkHack = new BlinkHack();
	public final BoatFlyHack boatFlyHack = new BoatFlyHack();
	public final BonemealAuraHack bonemealAuraHack = new BonemealAuraHack();
	public final BowAimbotHack bowAimbotHack = new BowAimbotHack();
	public final BuildRandomHack buildRandomHack = new BuildRandomHack();
	public final BunnyHopHack bunnyHopHack = new BunnyHopHack();
	public final CameraNoClipHack cameraNoClipHack = new CameraNoClipHack();
	public final CaveFinderHack caveFinderHack = new CaveFinderHack();
	public final ChatTranslatorHack chatTranslatorHack =
			new ChatTranslatorHack();
	public final ChestEspHack chestEspHack = new ChestEspHack();
	public final ClickAuraHack clickAuraHack = new ClickAuraHack();
	public final ClickGuiHack clickGuiHack = new ClickGuiHack();
	public final CrashChestHack crashChestHack = new CrashChestHack();
	public final CriticalsHack criticalsHack = new CriticalsHack();
	public final DerpHack derpHack = new DerpHack();
	public final DolphinHack dolphinHack = new DolphinHack();
	public final ExcavatorHack excavatorHack = new ExcavatorHack();
	public final ExtraElytraHack extraElytraHack = new ExtraElytraHack();
	public final FancyChatHack fancyChatHack = new FancyChatHack();
	public final FastBreakHack fastBreakHack = new FastBreakHack();
	public final FastLadderHack fastLadderHack = new FastLadderHack();
	public final FastPlaceHack fastPlaceHack = new FastPlaceHack();
	public final FightBotHack fightBotHack = new FightBotHack();
	public final FishHack fishHack = new FishHack();
	public final FlightHack flightHack = new FlightHack();
	public final FollowHack followHack = new FollowHack();
	public final ForceOpHack forceOpHack = new ForceOpHack();
	public final FreecamHack freecamHack = new FreecamHack();
	public final FullbrightHack fullbrightHack = new FullbrightHack();
	public final GlideHack glideHack = new GlideHack();
	public final HandNoClipHack handNoClipHack = new HandNoClipHack();
	public final HeadRollHack headRollHack = new HeadRollHack();
	public final HealthTagsHack healthTagsHack = new HealthTagsHack();
	public final HighJumpHack highJumpHack = new HighJumpHack();
	public final InfiniChatHack infiniChatHack = new InfiniChatHack();
	public final InstantBunkerHack instantBunkerHack = new InstantBunkerHack();
	public final ItemEspHack itemEspHack = new ItemEspHack();
	public final ItemGeneratorHack itemGeneratorHack = new ItemGeneratorHack();
	public final JesusHack jesusHack = new JesusHack();
	public final JetpackHack jetpackHack = new JetpackHack();
	public final KaboomHack kaboomHack = new KaboomHack();
	public final KillauraLegitHack killauraLegitHack = new KillauraLegitHack();
	public final KillauraHack killauraHack = new KillauraHack();
	public final KillPotionHack killPotionHack = new KillPotionHack();
	public final LiquidsHack liquidsHack = new LiquidsHack();
	public final LsdHack lsdHack = new LsdHack();
	public final MassTpaHack massTpaHack = new MassTpaHack();
	public final MileyCyrusHack mileyCyrusHack = new MileyCyrusHack();
	public final MobEspHack mobEspHack = new MobEspHack();
	public final MobSpawnEspHack mobSpawnEspHack = new MobSpawnEspHack();
	public final MultiAuraHack multiAuraHack = new MultiAuraHack();
	public final NameProtectHack nameProtectHack = new NameProtectHack();
	public final NameTagsHack nameTagsHack = new NameTagsHack();
	public final NavigatorHack navigatorHack = new NavigatorHack();
	public final NoClipHack noClipHack = new NoClipHack();
	public final NoFallHack noFallHack = new NoFallHack();
	public final NoFireOverlayHack noFireOverlayHack = new NoFireOverlayHack();
	public final NoHurtcamHack noHurtcamHack = new NoHurtcamHack();
	public final NoOverlayHack noOverlayHack = new NoOverlayHack();
	public final NoPumpkinHack noPumpkinHack = new NoPumpkinHack();
	public final NoSlowdownHack noSlowdownHack = new NoSlowdownHack();
	public final NoWeatherHack noWeatherHack = new NoWeatherHack();
	public final NoWebHack noWebHack = new NoWebHack();
	public final NukerHack nukerHack = new NukerHack();
	public final NukerLegitHack nukerLegitHack = new NukerLegitHack();
	public final OverlayHack overlayHack = new OverlayHack();
	public final PanicHack panicHack = new PanicHack();
	public final ParkourHack parkourHack = new ParkourHack();
	public final PlayerEspHack playerEspHack = new PlayerEspHack();
	public final PlayerFinderHack playerFinderHack = new PlayerFinderHack();
	public final PotionSaverHack potionSaverHack = new PotionSaverHack();
	public final ProphuntEspHack prophuntEspHack = new ProphuntEspHack();
	public final ProtectHack protectHack = new ProtectHack();
	public final RadarHack radarHack = new RadarHack();
	public final RainbowUiHack rainbowUiHack = new RainbowUiHack();
	public final ReachHack reachHack = new ReachHack();
	public final RemoteViewHack remoteViewHack = new RemoteViewHack();
	public final SafeWalkHack safeWalkHack = new SafeWalkHack();
	public final ScaffoldWalkHack scaffoldWalkHack = new ScaffoldWalkHack();
	public final SearchHack searchHack = new SearchHack();
	public final ServerCrasherHack serverCrasherHack = new ServerCrasherHack();
	public final SkinDerpHack skinDerpHack = new SkinDerpHack();
	public final SneakHack sneakHack = new SneakHack();
	public final SpeedHackHack speedHackHack = new SpeedHackHack();
	public final SpeedNukerHack speedNukerHack = new SpeedNukerHack();
	public final SpiderHack spiderHack = new SpiderHack();
	public final StepHack stepHack = new StepHack();
	public final ThrowHack throwHack = new ThrowHack();
	public final TimerHack timerHack = new TimerHack();
	public final TiredHack tiredHack = new TiredHack();
	public final TooManyHaxHack tooManyHaxHack = new TooManyHaxHack();
	public final TpAuraHack tpAuraHack = new TpAuraHack();
	public final TrajectoriesHack trajectoriesHack = new TrajectoriesHack();
	public final TriggerBotHack triggerBotHack = new TriggerBotHack();
	public final TrollPotionHack trollPotionHack = new TrollPotionHack();
	public final TrueSightHack trueSightHack = new TrueSightHack();
	public final TunnellerHack tunnellerHack = new TunnellerHack();
	public final XRayHack xRayHack = new XRayHack();

	public final EvalHack evalHack = new EvalHack();
	public final InventoryHudHack inventoryHudHack = new InventoryHudHack();
	public final PlayerHudHack playerHudHack = new PlayerHudHack();

	public TreeMap<String, Hack> hax =
		new TreeMap<>((o1, o2) -> o1.compareToIgnoreCase(o2));
	public TreeMap<String, Hack> enabledHax =
			new TreeMap<>((o1, o2) -> o1.compareToIgnoreCase(o2));
	
	private final EnabledHacksFile enabledHacksFile;
	private final Path profilesFolder =
		BurstClient.INSTANCE.getWurstFolder().resolve("enabled hacks");
	
	private final EventManager eventManager =
		BurstClient.INSTANCE.getEventManager();

	public void loadJava(String packageName){
		System.out.println("Loading Java hacks");
		for(Field field : HackList.class.getDeclaredFields())
		{
			try {
				if (!field.getName().endsWith("Hack"))
					continue;

				Hack hack = (Hack) field.get(this);
				hack.initAnotations();
				hax.put(hack.getName(), hack);
				System.out.println("Successfully loaded \'" + hack.getName() + "\' module");
			}catch (Exception e){
				String message = "Initializing Wurst hacks";
				CrashReport report = CrashReport.create(e, message);
			}
		}
/*
		Reflections reflections = new Reflections(packageName);

		Set<Class<? extends Hack>> subTypes = reflections.getSubTypesOf(Hack.class);

		//All the mods in the hashmap are referenced via their keys and are gaurenteed to have a name/desc
		for(Class<?> modClass : subTypes){
			//Create new instances of each mod
			try {
				Hack mod = (Hack) modClass.newInstance();
				mod.initAnotations();

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
		}*/
	}

	public void loadJs(String jsDirectory){
		System.out.println("Loading JS mods from \'" + System.getProperty("user.dir") + "/" + jsDirectory + "\' vis Nashorn");


		File folder = new File(jsDirectory);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				try {
					ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
					Invocable invoker = (Invocable) engine;
					engine.eval(new FileReader(fileEntry));
					Hack modObj = (Hack) invoker.invokeFunction("hack");
					modObj.initAnotations();
					modObj.init();
					hax.put(modObj.getName(), modObj);
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

	public HackList(Path enabledHacksFile)
	{
		this.enabledHacksFile = new EnabledHacksFile(enabledHacksFile);
		
		try
		{
			loadJava("net.wurstclient");
			loadJs("scripts/mods");

		}catch(Exception e)
		{
			String message = "Initializing Wurst hacks";
			CrashReport report = CrashReport.create(e, message);
			throw new CrashException(report);
		}
		
		eventManager.add(UpdateListener.class, this);
	}

	public void remove(String h){
		hax.remove(h);
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
