/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.wurstclient.event.EventManager;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.mixinterface.IMinecraftClient;
import net.wurstclient.settings.Setting;

public abstract class Feature
{
	public static  BurstClient WURST = BurstClient.INSTANCE;
	public static  EventManager EVENTS = WURST.getEventManager();
	public static  MinecraftClient MC = BurstClient.MC;
	public static  IMinecraftClient IMC = BurstClient.IMC;

	public String name;
	public String description;
	public String category;
	
	public  LinkedHashMap<String, Setting> settings =
		new LinkedHashMap<>();
	public  LinkedHashSet<PossibleKeybind> possibleKeybinds =
		new LinkedHashSet<>();
	
	public  String searchTags =
		getClass().isAnnotationPresent(SearchTags.class) ? String.join("\u00a7",
			getClass().getAnnotation(SearchTags.class).value()) : "";
	
	public  boolean safeToBlock =
		!getClass().isAnnotationPresent(DontBlock.class);
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getCategory() {
		return category;
	}
	
	public abstract String getPrimaryAction();

	public void initAnotations(){
		//Initialize hack settings and category annotations here
		BurstFeature featureInfo = this.getClass().getAnnotation(BurstFeature.class);
		if (featureInfo == null)
			return;

		this.name =  featureInfo.name();
		this.description = featureInfo.description();
		this.category = featureInfo.category();
	}

	public void doPrimaryAction()
	{
		
	}
	
	public boolean isEnabled()
	{
		return false;
	}
	
	public  Map<String, Setting> getSettings()
	{
		return Collections.unmodifiableMap(settings);
	}
	
	public  void addSetting(Setting setting)
	{
		String key = setting.getName().toLowerCase();
		
		if(settings.containsKey(key))
			throw new IllegalArgumentException(
				"Duplicate setting: " + getName() + " " + key);
		
		settings.put(key, setting);
		possibleKeybinds.addAll(setting.getPossibleKeybinds(getName()));
	}
	
	public  void addPossibleKeybind(String command, String description)
	{
		possibleKeybinds.add(new PossibleKeybind(command, description));
	}
	
	public  Set<PossibleKeybind> getPossibleKeybinds()
	{
		return Collections.unmodifiableSet(possibleKeybinds);
	}
	
	public  String getSearchTags()
	{
		return searchTags;
	}
	
	public  boolean isSafeToBlock()
	{
		return safeToBlock;
	}
}
