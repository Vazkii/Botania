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

import vazkii.botania.api.InterfaceRegistry;

/**
 * A bauble that counts as a cosmetic only item. These can be added to
 * other baubles to add the render to them. Other cosmetic baubles
 * can't be stacked on this.
 */
public interface ICosmeticBauble {
	static InterfaceRegistry<Item, ICosmeticBauble> registry() {
		return ItemAPI.instance().getCosmeticBaubleRegistry();
	}
}
