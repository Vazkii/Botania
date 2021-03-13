/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import vazkii.botania.api.InterfaceRegistry;
import vazkii.botania.api.mana.IManaPool;

/**
 * An item that implements this will have a function called when its standing atop
 * a mana pool.
 */
public interface IManaDissolvable {
	static InterfaceRegistry<Item, IManaDissolvable> registry() {
		return ItemAPI.instance().getManaDissolvableRegistry();
	}

	/**
	 * Called for every tick the item is on a mana pool. If the stack has stack
	 * size 0 the item is killed. This is called serverside.
	 */
	void onDissolveTick(IManaPool pool, ItemStack stack, ItemEntity item);

}
