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

import vazkii.botania.api.item.IPhantomInkable;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

public class PhantomInkRecipe extends SpecialRecipe {
	public static final SpecialRecipeSerializer<PhantomInkRecipe> SERIALIZER = new SpecialRecipeSerializer<>(PhantomInkRecipe::new);

	public PhantomInkRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory var1, @Nonnull World var2) {
		boolean foundInk = false;
		boolean foundItem = false;

		for (int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == ModItems.phantomInk && !foundInk) {
					foundInk = true;
				} else if (!foundItem) {
					if (IPhantomInkable.registry().has(stack.getItem()) && stack.getItem().getContainerItem(stack).isEmpty()) {
						foundItem = true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}

		return foundInk && foundItem;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory var1) {
		ItemStack item = ItemStack.EMPTY;

		for (int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if (!stack.isEmpty() && IPhantomInkable.registry().has(stack.getItem()) && item.isEmpty()) {
				item = stack;
			}
		}

		IPhantomInkable inkable = IPhantomInkable.registry().get(item.getItem());
		ItemStack copy = item.copy();
		inkable.setPhantomInk(copy, !inkable.hasPhantomInk(item));
		return copy;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
