/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import vazkii.botania.api.InterfaceRegistry;

/**
 * An item that implements this can break multiple blocks at once
 * with a Ring of Loki. Usage of this interface requires an implementation
 * (see ItemTerraPick).
 */
public interface ISequentialBreaker {
	static InterfaceRegistry<Item, ISequentialBreaker> registry() {
		return ItemAPI.instance().getSequentialBreakerRegistry();
	}

	void breakOtherBlock(PlayerEntity player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side);

	@Deprecated // todo 1.17 remove
	default boolean disposeOfTrashBlocks(ItemStack stack) {
		return false;
	}

}
