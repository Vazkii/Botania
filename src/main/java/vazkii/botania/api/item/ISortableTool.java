/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import vazkii.botania.api.InterfaceRegistry;

/**
 * This interface describes a tool that can be sorted by the Ring of
 * Correction.
 */
public interface ISortableTool {
	static InterfaceRegistry<Item, ISortableTool> registry() {
		return ItemAPI.instance().getSortableToolRegistry();
	}

	/**
	 * Gets the priority that this tool should have when being sorted. The
	 * tool with the highest priority will be picked. The way this is specified
	 * should be (tool-level) * 100 + (tool-modifier) * 10 + (efficiency-level).
	 * <br>
	 * <br>
	 * For example, a <b>Manasteel Pickaxe</b> is tool-level 10 and it doesn't have
	 * modifiers. Assuming Efficiency 4, the priority should be 10 * 100 + 4 = <b>1004</b>.
	 * This will rate higher than a similar pickaxe with Efficiency 3.<br>
	 * A <b>Terra Shatterer</b> has a modifier, its rank and is tool-level 20. With Efficiency
	 * 5 and rank B (2) the priority should be 20 * 100 + 2 * 10 + 5 = <b>2025</b>.
	 * <br>
	 * <br>
	 * All intermediate tool levels are there for other mod tools that wish to occupy the spots inbetween.
	 * Of course, you don't have to always adhere to this. Tools like the <b>Vitreous Pickaxe</b> don't,
	 * that one in particular is priority 0, unless looking at glass, in which case it's {@link Integer#MAX_VALUE}.
	 *
	 * @param state The blockstate being broken.
	 */
	default int getSortingPriority(ItemStack stack, BlockState state) {
		return getSortingPriority(stack);
	}

	/**
	 * @deprecated Override {@link #getSortingPriority(ItemStack, BlockState)} instead.
	 */
	@Deprecated
	default int getSortingPriority(ItemStack stack) {
		return 0;
	}

}
