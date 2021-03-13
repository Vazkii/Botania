/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import vazkii.botania.api.InterfaceRegistry;

/**
 * An Item that implements this can be wielded by an Avatar.
 */
public interface IAvatarWieldable {
	static InterfaceRegistry<Item, IAvatarWieldable> registry() {
		return ItemAPI.instance().getAvatarWieldableRegistry();
	}

	/**
	 * Called on update of the avatar tile.
	 */
	void onAvatarUpdate(IAvatarTile tile, ItemStack stack);

	/**
	 * Gets the overlay resource to render on top of the avatar tile.
	 */
	ResourceLocation getOverlayResource(IAvatarTile tile, ItemStack stack);

}
