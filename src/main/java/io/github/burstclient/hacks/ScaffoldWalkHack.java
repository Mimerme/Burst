/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package io.github.burstclient.hacks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EmptyBlockView;
import net.wurstclient.SearchTags;
import net.wurstclient.events.BurstListener;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.util.RotationUtils.Rotation;

@SearchTags({"scaffold walk", "BridgeWalk", "bridge walk", "AutoBridge",
	"auto bridge", "tower"})
public final class ScaffoldWalkHack extends Hack implements GUIRenderListener
{
	public ScaffoldWalkHack()
	{
		super("ScaffoldWalk", "Automatically places blocks below your feet.");
		setCategory("Blocks");
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(GUIRenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(GUIRenderListener.class, this);
	}
	
	@Override
	public void onRenderGUI(MatrixStack matrixStack, float partialTicks)
	{
		BlockPos belowPlayer = new BlockPos(MC.player.getPos()).down();
		
		// check if block is already placed
		if(!BlockUtils.getState(belowPlayer).getMaterial().isReplaceable())
			return;

		// search blocks in hotbar
		int newSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			// filter out non-block items
			ItemStack stack = MC.player.inventory.getStack(i);
			if(stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
				continue;
			
			// filter out non-solid blocks
			Block block = Block.getBlockFromItem(stack.getItem());
			BlockState state = block.getDefaultState();
			if(!state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN))
				continue;
			
			// filter out blocks that would fall
			if(block instanceof FallingBlock && FallingBlock
				.canFallThrough(BlockUtils.getState(belowPlayer.down())))
				continue;
			
			newSlot = i;
			break;
		}
		
		// check if any blocks were found
		if(newSlot == -1)
			return;
		
		// set slot
		int oldSlot = MC.player.inventory.selectedSlot;
		MC.player.inventory.selectedSlot = newSlot;
		
		placeBlock(belowPlayer);
		
		// reset slot
		MC.player.inventory.selectedSlot = oldSlot;
	}
	
	private boolean placeBlock(BlockPos pos) {
		Vec3d eyesPos = new Vec3d(MC.player.getX(),
				MC.player.getY() + MC.player.getEyeHeight(MC.player.getPose()),
				MC.player.getZ());

			BlockPos neighbor = pos.offset(Direction.DOWN);
			Direction side2 = Direction.DOWN.getOpposite();

			Vec3d hitVec = Vec3d.ofCenter(neighbor)
					.add(Vec3d.of(side2.getVector()).multiply(0.5));

			// check if hitVec is within range (4.25 blocks)
			if (eyesPos.squaredDistanceTo(hitVec) > 18.0625)
				return false;

			// place block
			Rotation rotation = RotationUtils.getNeededRotations(hitVec);
			PlayerMoveC2SPacket.LookOnly packet =
					new PlayerMoveC2SPacket.LookOnly(rotation.getYaw(),
							rotation.getPitch(), MC.player.isOnGround());
			MC.player.networkHandler.sendPacket(packet);
			IMC.getInteractionManager().rightClickBlock(MC.player.getBlockPos().down(1), Direction.DOWN,
					hitVec);
			MC.player.swingHand(Hand.MAIN_HAND);
			IMC.setItemUseCooldown(4);

			return true;

	}
}
