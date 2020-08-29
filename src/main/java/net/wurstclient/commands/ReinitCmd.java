/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.wurstclient.BurstClient;
import net.wurstclient.BurstFeature;
import net.wurstclient.command.*;
import net.wurstclient.hacks.ProtectHack;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.FakePlayerEntity;

@BurstFeature(name="reinit", description = "Reinitializes Burst. Do this to reload features",
        category = "cmd")
@BurstCmd(syntax = ".reinit")
public final class ReinitCmd extends Command
{
    @Override
    public void call(String[] args) throws CmdException
    {
        BurstClient.INSTANCE.purgeEvents();
        BurstClient.INSTANCE.loadFeatures();
        BurstClient.guiInitialized = false;
        ChatUtils.message("Reinitialized");
    }
}
