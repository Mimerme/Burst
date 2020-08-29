/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.command;

import java.util.Objects;

import net.wurstclient.BurstFeature;
import net.wurstclient.Feature;
import net.wurstclient.util.ChatUtils;

public abstract class Command extends Feature
{
	private String[] syntax;
	private String category;

	public Command(){
		this("", "", "");
	}

	public Command(String name, String description, String... syntax)
	{
		this.name = Objects.requireNonNull(name);
		this.description = Objects.requireNonNull(description);

		Objects.requireNonNull(syntax);
		if(syntax.length > 0)
			syntax[0] = "Syntax: " + syntax[0];
		this.syntax = syntax;
	}

	@Override
	public void initAnotations() {
		//Initialize hack settings and category annotations here
		BurstCmd cmdInfo = this.getClass().getAnnotation(BurstCmd.class);
		if (cmdInfo == null)
			return;

		this.syntax =  cmdInfo.syntax();
		super.initAnotations();
	}

	public abstract void call(String[] args) throws CmdException;
	
	@Override
	public final String getName()
	{
		return "." + name;
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "";
	}
	
	@Override
	public final String getDescription()
	{
		String description = this.description;
		
		if(syntax.length > 0)
			description += "\n";
		
		for(String line : syntax)
			description += "\n" + line;
		
		return description;
	}
	
	public final String[] getSyntax()
	{
		return syntax;
	}
	
	public final void printHelp()
	{
		for(String line : description.split("\n"))
			ChatUtils.message(line);
		
		for(String line : syntax)
			ChatUtils.message(line);
	}
	
	@Override
	public final String getCategory()
	{
		return category;
	}
	
	protected final void setCategory(String category)
	{
		this.category = category;
	}
}
