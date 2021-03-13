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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.InterfaceRegistry;

/**
 * A Block that implements this can be uprooted by the various horns in Botania.
 */
public interface IHornHarvestable {
	static InterfaceRegistry<Block, IHornHarvestable> registry() {
		return ItemAPI.instance().getHornHarvestableRegistry();
	}

	/**
	 * Returns true if this block can be uprooted.
	 * Note that the stack param can be empty if it's a drum breaking it.
	 */
	boolean canHornHarvest(World world, BlockPos pos, ItemStack stack, EnumHornType hornType);

	/**
	 * Returns true if harvestByHorn() should be called. If false it just uses the normal
	 * block breaking method.
	 * Note that the stack param can be empty if it's a drum breaking it.
	 */
	boolean hasSpecialHornHarvest(World world, BlockPos pos, ItemStack stack, EnumHornType hornType);

	/**
	 * Called to harvest by a horn.
	 * Note that the stack param can be empty if it's a drum breaking it.
	 */
	void harvestByHorn(World world, BlockPos pos, ItemStack stack, EnumHornType hornType);

	enum EnumHornType {

		/**
		 * Horn of the Wild, for grass and crops
		 */
		WILD,

		/**
		 * Horn of the Canopy, for leaves
		 */
		CANOPY,

		/**
		 * Horn of the Covering, for snow
		 */
		COVERING
	};

}
