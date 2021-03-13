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
import net.minecraft.util.math.BlockPos;

import vazkii.botania.api.InterfaceRegistry;

import javax.annotation.Nullable;

import java.util.List;

/**
 * An item that implements this will allow for various wireframes to be drawn
 * around the player. This will be called while the item is in the inventory,
 * armor slot or bauble slots.
 */
public interface IWireframeCoordinateListProvider {
	static InterfaceRegistry<Item, IWireframeCoordinateListProvider> registry() {
		return ItemAPI.instance().getWireframeCoordinateListProviderRegistry();
	}

	/**
	 * Returns a list of BlockPos for the wireframes to draw.
	 * Will not be null.
	 */
	List<BlockPos> getWireframesToDraw(PlayerEntity player, ItemStack stack);

	/**
	 * Gets a wireframe to draw thicker than the rest.
	 * This is useful to indicate the precedence of some position over the others.
	 * 
	 * @return The position of a single wireframe to draw thicker than all the others.
	 */
	@Nullable
	default BlockPos getSourceWireframe(PlayerEntity player, ItemStack stack) {
		return null;
	}

}
