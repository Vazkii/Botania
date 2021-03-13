/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import vazkii.botania.api.item.IAncientWillContainer;
import vazkii.botania.common.item.ItemAncientWill;

import javax.annotation.Nonnull;

public class AncientWillRecipe extends SpecialRecipe {
	public static final SpecialRecipeSerializer<AncientWillRecipe> SERIALIZER = new SpecialRecipeSerializer<>(AncientWillRecipe::new);

	public AncientWillRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
		boolean foundWill = false;
		boolean foundItem = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ItemAncientWill && !foundWill) {
					foundWill = true;
				} else if (!foundItem) {
					if (IAncientWillContainer.registry().has(stack.getItem())) {
						foundItem = true;
					} else {
						return false;
					}
				}
			}
		}

		return foundWill && foundItem;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
		ItemStack item = ItemStack.EMPTY;
		IAncientWillContainer.AncientWillType will = null;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (IAncientWillContainer.registry().has(stack.getItem()) && item.isEmpty()) {
					item = stack;
				} else {
					will = ((ItemAncientWill) stack.getItem()).type; // we already verified this is a will in matches()
				}
			}
		}

		;
		IAncientWillContainer container = IAncientWillContainer.registry().get(item.getItem());
		if (container.hasAncientWill(item, will)) {
			return ItemStack.EMPTY;
		}

		ItemStack copy = item.copy();
		container.addAncientWill(copy, will);
		return copy;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width > 1 || height > 1;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
