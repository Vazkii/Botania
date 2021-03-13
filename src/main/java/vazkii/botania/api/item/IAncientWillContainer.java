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
 * An item that implements this can have Ancient Wills
 * crafted onto it.
 */
public interface IAncientWillContainer {
	static InterfaceRegistry<Item, IAncientWillContainer> registry() {
		return ItemAPI.instance().getAncientWillContainerRegistry();
	}

	enum AncientWillType {
		AHRIM,
		DHAROK,
		GUTHAN,
		TORAG,
		VERAC,
		KARIL
	}

	void addAncientWill(ItemStack stack, AncientWillType will);

	boolean hasAncientWill(ItemStack stack, AncientWillType will);

}
