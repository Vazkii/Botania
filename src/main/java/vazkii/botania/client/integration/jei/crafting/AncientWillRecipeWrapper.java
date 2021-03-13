/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei.crafting;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import vazkii.botania.api.item.IAncientWillContainer;
import vazkii.botania.common.crafting.recipe.AncientWillRecipe;
import vazkii.botania.common.item.ItemAncientWill;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

public class AncientWillRecipeWrapper implements ICustomCraftingCategoryExtension {
	private final ResourceLocation name;

	public AncientWillRecipeWrapper(AncientWillRecipe recipe) {
		this.name = recipe.getId();
	}

	@Override
	public void setIngredients(@Nonnull IIngredients ingredients) {
		ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
		ImmutableList.Builder<ItemStack> helmets = ImmutableList.builder();
		ImmutableList.Builder<ItemStack> wills = ImmutableList.builder();
		helmets.add(new ItemStack(ModItems.terrasteelHelm));
		wills.add(new ItemStack(ModItems.ancientWillAhrim));
		wills.add(new ItemStack(ModItems.ancientWillDharok));
		wills.add(new ItemStack(ModItems.ancientWillGuthan));
		wills.add(new ItemStack(ModItems.ancientWillTorag));
		wills.add(new ItemStack(ModItems.ancientWillVerac));
		wills.add(new ItemStack(ModItems.ancientWillKaril));

		builder.add(helmets.build());
		builder.add(wills.build());

		ingredients.setInputLists(VanillaTypes.ITEM, builder.build());
		ingredients.setOutputLists(VanillaTypes.ITEM, ImmutableList.of(helmets.build()));
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return name;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IIngredients ingredients) {
		IFocus<?> focus = recipeLayout.getFocus();
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		group.set(ingredients);

		if (focus != null) {
			ItemStack focused = (ItemStack) focus.getValue();

			if (focus.getMode() == IFocus.Mode.INPUT && focused.getItem() instanceof ItemAncientWill) {
				ItemStack copy = focused.copy();
				copy.setCount(1);
				group.set(2, copy);
				group.set(0, getHelmetsWithWill(((ItemAncientWill) focused.getItem()).type, ingredients));
			} else if (IAncientWillContainer.registry().has(focused.getItem())) { //helmet
				group.set(1, new ItemStack(focused.getItem()));
				group.set(0, getWillsOnHelmet(focused.getItem()));
			}
		}
	}

	private List<ItemStack> getHelmetsWithWill(IAncientWillContainer.AncientWillType type, IIngredients ingredients) {
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		for (ItemStack itemStack : ingredients.getOutputs(VanillaTypes.ITEM).get(0)) {
			ItemStack toAdd = itemStack.copy();
			IAncientWillContainer container = IAncientWillContainer.registry().get(toAdd.getItem());
			container.addAncientWill(toAdd, type);
			builder.add(toAdd);
		}
		return builder.build();
	}

	private List<ItemStack> getWillsOnHelmet(Item item) {
		IAncientWillContainer container = IAncientWillContainer.registry().get(item);
		if (container != null) {
			ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
			for (IAncientWillContainer.AncientWillType type : IAncientWillContainer.AncientWillType.values()) {
				ItemStack stack = new ItemStack(item);
				container.addAncientWill(stack, type);
				builder.add(stack);
			}
			return builder.build();
		}
		return ImmutableList.of();
	}
}
