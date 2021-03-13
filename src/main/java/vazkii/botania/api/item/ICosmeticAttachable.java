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

import vazkii.botania.api.InterfaceRegistry;

/**
 * An Item that implements this interface can have cosmetic items attached to it.
 * The item does not have to be a bauble, but it is the render is automatically
 * handled by botania internally. Recipes to add and remove the cosmetic items will be
 * handled internally, but the cosmetic item has to be set as a container item.
 */
public interface ICosmeticAttachable {
	static InterfaceRegistry<Item, ICosmeticAttachable> registry() {
		return ItemAPI.instance().getCosmeticAttachableRegistry();
	}

	/**
	 * Gets the cosmetic item stored in the stack passed in.
	 */
	ItemStack getCosmeticItem(ItemStack stack);

	/**
	 * Sets the stack's cosmetic item to the one passed in.
	 */
	void setCosmeticItem(ItemStack stack, ItemStack cosmetic);

}
