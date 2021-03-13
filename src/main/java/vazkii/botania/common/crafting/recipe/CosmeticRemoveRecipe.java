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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.item.ICosmeticBauble;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;

import javax.annotation.Nonnull;

public class CosmeticRemoveRecipe extends SpecialRecipe {
	public static final SpecialRecipeSerializer<CosmeticRemoveRecipe> SERIALIZER = new SpecialRecipeSerializer<>(CosmeticRemoveRecipe::new);

	public CosmeticRemoveRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
		boolean foundAttachable = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				ICosmeticAttachable attachable = ICosmeticAttachable.registry().get(stack.getItem());
				if (attachable != null && !ICosmeticBauble.registry().has(stack.getItem()) && !attachable.getCosmeticItem(stack).isEmpty()) {
					foundAttachable = true;
				} else {
					return false;
				}
			}
		}

		return foundAttachable;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
		ItemStack attachableItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				attachableItem = stack;
			}
		}

		ICosmeticAttachable attachable = ICosmeticAttachable.registry().get(attachableItem.getItem());
		if (attachable.getCosmeticItem(attachableItem).isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack copy = attachableItem.copy();
		copy.setCount(1);
		attachable.setCosmeticItem(copy, ItemStack.EMPTY);
		return copy;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height > 0;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInventory inv) {
		return RecipeUtils.getRemainingItemsSub(inv, s -> {
			if (s.getItem() instanceof ItemBauble) {
				ItemStack stack = ((ItemBauble) s.getItem()).getCosmeticItem(s);
				stack.setCount(1);
				return stack;
			}
			return null;
		});
	}
}
