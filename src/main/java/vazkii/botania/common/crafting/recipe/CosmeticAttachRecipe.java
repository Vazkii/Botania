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

import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.item.ICosmeticBauble;

import javax.annotation.Nonnull;

public class CosmeticAttachRecipe extends SpecialRecipe {
	public static final SpecialRecipeSerializer<CosmeticAttachRecipe> SERIALIZER = new SpecialRecipeSerializer<>(CosmeticAttachRecipe::new);

	public CosmeticAttachRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
		boolean foundCosmetic = false;
		boolean foundAttachable = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (ICosmeticBauble.registry().has(stack.getItem()) && !foundCosmetic) {
					foundCosmetic = true;
				} else if (!foundAttachable) {
					ICosmeticAttachable attachable = ICosmeticAttachable.registry().get(stack.getItem());
					if (attachable != null && !ICosmeticBauble.registry().has(stack.getItem()) && attachable.getCosmeticItem(stack).isEmpty()) {
						foundAttachable = true;
					} else {
						return false;
					}
				}
			}
		}

		return foundCosmetic && foundAttachable;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
		ItemStack cosmeticItem = ItemStack.EMPTY;
		ItemStack attachableItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (ICosmeticBauble.registry().has(stack.getItem()) && cosmeticItem.isEmpty()) {
					cosmeticItem = stack;
				} else {
					attachableItem = stack;
				}
			}
		}

		if (!ICosmeticAttachable.registry().has(attachableItem.getItem())) {
			return ItemStack.EMPTY;
		}

		ICosmeticAttachable attachable = ICosmeticAttachable.registry().get(attachableItem.getItem());
		if (!attachable.getCosmeticItem(attachableItem).isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack copy = attachableItem.copy();
		attachable.setCosmeticItem(copy, cosmeticItem);
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
