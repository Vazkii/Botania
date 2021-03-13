/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.InterfaceRegistry;

/**
 * Have a block implement this class to make it do something when an adjacent
 * Hovering Hourglass turns.
 */
public interface IHourglassTrigger {
	static InterfaceRegistry<Block, IHourglassTrigger> registry() {
		return ItemAPI.instance().getHourglassTriggerRegistry();
	}

	void onTriggeredByHourglass(World world, BlockPos pos, TileEntity hourglass);

}
