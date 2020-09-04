/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.wurstclient.Feature;
import net.wurstclient.clickgui.BurstWindow;
import net.wurstclient.clickgui.Window;
import net.wurstclient.hacks.NavigatorHack;
import net.wurstclient.hacks.TooManyHaxHack;

public abstract class Hack extends Feature
{
	public boolean enabled;
	public  boolean stateSaved =
		!getClass().isAnnotationPresent(DontSaveState.class);
	public  ArrayList<Window> hackWindows = new ArrayList<>();
	public Hack(){
		this("","");
	}

	public Hack(String name, String description)
	{
		this.name = Objects.requireNonNull(name);
		this.description = Objects.requireNonNull(description);
		addPossibleKeybind(name, "Toggle " + name);
		this.init();
	}

	public String getDescription(){
		return super.getDescription();
	}

	public void setDescription(String desc){
		super.description = desc;
	}

	public void setName(String name){
		super.name = name;
	}

	public String getName(){
		return super.name;
	}


	public String getCategory(){
		return super.category;
	}

	//Used by the nashorn API
	public void init(){}

	@Override
	public void initAnotations() {
		super.initAnotations();

		//Initialize settings here
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields){
			if (f.getAnnotation(Setting.class) != null){
				f.setAccessible(true);
				try {
					Object settingObj = f.get(this);
					if (!(settingObj instanceof net.wurstclient.settings.Setting))
						throw new RuntimeException("@Setting field on Non-Setting type");

					addSetting((net.wurstclient.settings.Setting) settingObj);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<Window> getWindows(){
		ArrayList<Window> windows = this.hackWindows;
		for (Field f : this.getClass().getDeclaredFields()){
			if (f.getAnnotation(BurstWindow.class) != null) {
				f.setAccessible(true);
				try {
					windows.add((Window) f.get(this));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return windows;
	}

	public void addWindow(Window window){
		hackWindows.add(window);
	}

	public String getRenderName()
	{
		return name;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		if(this.enabled == enabled)
			return;
		
		TooManyHaxHack tooManyHax = WURST.getHax().getTooManyHaxHack();
		if(enabled && tooManyHax.isEnabled() && tooManyHax.isBlocked(this))
			return;
		
		this.enabled = enabled;
		
		if(!(this instanceof NavigatorHack))
			WURST.getHud().getHackList().updateState(this);
		
		if(enabled)
			onEnable();
		else
			onDisable();
		
		if(stateSaved)
			WURST.getHax().saveEnabledHax();
	}
	
	@Override
	public  String getPrimaryAction()
	{
		return enabled ? "Disable" : "Enable";
	}
	
	@Override
	public void doPrimaryAction()
	{
		setEnabled(!enabled);
	}
	
	public boolean isStateSaved()
	{
		return stateSaved;
	}
	
	protected void onEnable()
	{
		
	}

	protected void onDisable()
	{
		
	}
}
