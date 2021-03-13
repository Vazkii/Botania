/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;

import vazkii.botania.api.InterfaceRegistry;
import vazkii.botania.api.subtile.TileEntitySpecialFlower;

/**
 * An Item that implements this can be placed by a Rannuncarpus.
 */
public interface IFlowerPlaceable {
	static InterfaceRegistry<Item, IFlowerPlaceable> registry() {
		return ItemAPI.instance().getFlowerPlaceableRegistry();
	}

	/**
	 * @param ctx The placement context, aiming downwards towards the downward neighbor position
	 * @return Whether the block was placed successfully
	 */
	boolean tryPlace(TileEntitySpecialFlower flower, BlockItemUseContext ctx);
}
