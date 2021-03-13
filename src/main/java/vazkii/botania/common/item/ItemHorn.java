/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.item.IHornHarvestable;
import vazkii.botania.api.item.IHornHarvestable.EnumHornType;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemHorn extends Item {
	public ItemHorn(Properties props) {
		super(props);
	}

	@Nonnull
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		player.setActiveHand(hand);
		return ActionResult.resultConsume(player.getHeldItem(hand));
	}

	@Override
	public void onUse(World world, @Nonnull LivingEntity player, @Nonnull ItemStack stack, int time) {
		if (!world.isRemote) {
			if (time != getUseDuration(stack) && time % 5 == 0) {
				breakGrass(world, stack, player.getPosition());
			}
			world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1F, 0.001F);
		}
	}

	public static void breakGrass(World world, ItemStack stack, BlockPos srcPos) {
		EnumHornType type = null;
		if (stack.getItem() == ModItems.grassHorn) {
			type = EnumHornType.WILD;
		} else if (stack.getItem() == ModItems.leavesHorn) {
			type = EnumHornType.CANOPY;
		} else if (stack.getItem() == ModItems.snowHorn) {
			type = EnumHornType.COVERING;
		}

		int range = 12 - type.ordinal() * 3;
		int rangeY = 3 + type.ordinal() * 4;
		List<BlockPos> coords = new ArrayList<>();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(srcPos.add(-range, -rangeY, -range),
				srcPos.add(range, rangeY, range))) {
			Block block = world.getBlockState(pos).getBlock();
			IHornHarvestable harvestable = IHornHarvestable.registry().get(block);
			if (harvestable != null
					? harvestable.canHornHarvest(world, pos, stack, type)
					: type == EnumHornType.WILD && block instanceof BushBlock && !block.isIn(ModTags.Blocks.SPECIAL_FLOWERS)
							|| type == EnumHornType.CANOPY && BlockTags.LEAVES.contains(block)
							|| type == EnumHornType.COVERING && block == Blocks.SNOW) {
				coords.add(pos.toImmutable());
			}
		}

		Collections.shuffle(coords, world.rand);

		int count = Math.min(coords.size(), 32 + type.ordinal() * 16);
		for (int i = 0; i < count; i++) {
			BlockPos currCoords = coords.get(i);
			BlockState state = world.getBlockState(currCoords);
			Block block = state.getBlock();

			IHornHarvestable harvestable = IHornHarvestable.registry().get(block);
			if (harvestable != null && harvestable.hasSpecialHornHarvest(world, currCoords, stack, type)) {
				harvestable.harvestByHorn(world, currCoords, stack, type);
			} else {
				world.destroyBlock(currCoords, true);
			}
		}
	}

}
