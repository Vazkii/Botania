/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ToolType;

import vazkii.botania.api.item.ISortableTool;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

public class ItemSwapRing extends ItemBauble {

	public ItemSwapRing(Properties props) {
		super(props);
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity entity) {
		if (entity.world.isRemote && !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;
		ItemStack currentStack = player.getHeldItemMainhand();
		ISortableTool tool = ISortableTool.registry().get(currentStack.getItem());
		if (currentStack.isEmpty() || tool == null) {
			return;
		}

		BlockRayTraceResult pos = ToolCommons.raytraceFromEntity(player, 4.5F, false);

		ToolType typeToFind;
		BlockState state;
		if (player.isSwingInProgress && pos.getType() == RayTraceResult.Type.BLOCK) {
			state = entity.world.getBlockState(pos.getPos());

			Material mat = state.getMaterial();
			if (ToolCommons.materialsPick.contains(mat)) {
				typeToFind = ToolType.PICKAXE;
			} else if (ToolCommons.materialsShovel.contains(mat)) {
				typeToFind = ToolType.SHOVEL;
			} else if (ToolCommons.materialsAxe.contains(mat)) {
				typeToFind = ToolType.AXE;
			} else {
				return;
			}
		} else {
			return;
		}

		ItemStack bestTool = currentStack;
		int bestToolPriority = currentStack.getToolTypes().contains(typeToFind) ? tool.getSortingPriority(currentStack, state) : -1;
		int bestSlot = -1;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stackInSlot = player.inventory.getStackInSlot(i);
			ISortableTool toolInSlot = ISortableTool.registry().get(stackInSlot.getItem());
			if (!stackInSlot.isEmpty() && toolInSlot != null && stackInSlot != currentStack) {
				if (stackInSlot.getToolTypes().contains(typeToFind)) {
					int priority = toolInSlot.getSortingPriority(stackInSlot, state);
					if (priority > bestToolPriority) {
						bestTool = stackInSlot;
						bestToolPriority = priority;
						bestSlot = i;
					}
				}
			}
		}

		if (bestSlot != -1) {
			player.setHeldItem(Hand.MAIN_HAND, bestTool);
			player.inventory.setInventorySlotContents(bestSlot, currentStack);
		}
	}
}
