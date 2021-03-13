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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import vazkii.botania.api.item.IBlockProvider;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.handler.ItemsRemainingRenderHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.rod.ItemExchangeRod;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class ItemAstrolabe extends Item {

	private static final String TAG_BLOCKSTATE = "blockstate";
	private static final String TAG_SIZE = "size";

	public ItemAstrolabe(Properties props) {
		super(props);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		ItemStack stack = ctx.getItem();
		BlockState state = ctx.getWorld().getBlockState(ctx.getPos());
		PlayerEntity player = ctx.getPlayer();

		if (player != null && player.isSneaking()) {
			if (setBlock(stack, state)) {
				displayRemainderCounter(player, stack);
				return ActionResultType.SUCCESS;
			}
		} else if (player != null) {
			boolean did = placeAllBlocks(stack, player);
			if (did) {
				displayRemainderCounter(player, stack);
			}

			return did ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}

		return ActionResultType.PASS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (playerIn.isSneaking()) {
			playerIn.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1F);
			if (!worldIn.isRemote) {
				int size = getSize(stack);
				int newSize = size == 11 ? 3 : size + 2;
				setSize(stack, newSize);
				ItemsRemainingRenderHandler.send(playerIn, stack, 0, new StringTextComponent(newSize + "x" + newSize));
			}

			return ActionResult.resultSuccess(stack);
		}

		return ActionResult.resultPass(stack);
	}

	public boolean placeAllBlocks(ItemStack stack, PlayerEntity player) {
		List<BlockPos> blocksToPlace = getBlocksToPlace(stack, player);
		if (!hasBlocks(stack, player, blocksToPlace)) {
			return false;
		}

		int size = getSize(stack);
		int cost = size * 320;
		if (!ManaItemHandler.instance().requestManaExact(stack, player, cost, false)) {
			return false;
		}

		ItemStack stackToPlace = new ItemStack(getBlock(stack));
		for (BlockPos coords : blocksToPlace) {
			placeBlockAndConsume(player, stack, stackToPlace, coords);
		}
		ManaItemHandler.instance().requestManaExact(stack, player, cost, true);

		return true;
	}

	private void placeBlockAndConsume(PlayerEntity player, ItemStack requestor, ItemStack blockToPlace, BlockPos coords) {
		if (blockToPlace.isEmpty()) {
			return;
		}

		Block block = Block.getBlockFromItem(blockToPlace.getItem());
		BlockState state = block.getDefaultState();
		player.world.setBlockState(coords, state);
		player.world.playEvent(2001, coords, Block.getStateId(state));

		if (player.abilities.isCreativeMode) {
			return;
		}

		List<ItemStack> stacksToCheck = new ArrayList<>();
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stackInSlot = player.inventory.getStackInSlot(i);
			if (!stackInSlot.isEmpty() && stackInSlot.getItem() == blockToPlace.getItem()) {
				stackInSlot.shrink(1);
				return;
			}

			if (!stackInSlot.isEmpty() && IBlockProvider.registry().has(stackInSlot.getItem())) {
				stacksToCheck.add(stackInSlot);
			}
		}

		for (ItemStack providerStack : stacksToCheck) {
			IBlockProvider prov = IBlockProvider.registry().get(providerStack.getItem());

			if (prov.provideBlock(player, requestor, providerStack, block, false)) {
				prov.provideBlock(player, requestor, providerStack, block, true);
				return;
			}
		}
	}

	public static boolean hasBlocks(ItemStack stack, PlayerEntity player, List<BlockPos> blocks) {
		if (player.abilities.isCreativeMode) {
			return true;
		}

		Block block = getBlock(stack);
		ItemStack reqStack = new ItemStack(block);

		int required = blocks.size();
		int current = 0;
		List<ItemStack> stacksToCheck = new ArrayList<>();
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stackInSlot = player.inventory.getStackInSlot(i);
			if (!stackInSlot.isEmpty() && stackInSlot.getItem() == reqStack.getItem()) {
				current += stackInSlot.getCount();
				if (current >= required) {
					return true;
				}
			}
			if (!stackInSlot.isEmpty() && IBlockProvider.registry().has(stackInSlot.getItem())) {
				stacksToCheck.add(stackInSlot);
			}
		}

		for (ItemStack providerStack : stacksToCheck) {
			IBlockProvider prov = IBlockProvider.registry().get(providerStack.getItem());
			int count = prov.getBlockCount(player, stack, providerStack, block);
			if (count == -1) {
				return true;
			}

			current += count;

			if (current >= required) {
				return true;
			}
		}

		return false;
	}

	public static List<BlockPos> getBlocksToPlace(ItemStack stack, PlayerEntity player) {
		List<BlockPos> coords = new ArrayList<>();
		BlockRayTraceResult rtr = ToolCommons.raytraceFromEntity(player, 5, true);
		if (rtr.getType() == RayTraceResult.Type.BLOCK) {
			BlockPos pos = rtr.getPos();
			BlockState state = player.world.getBlockState(pos);
			if (state.getMaterial().isReplaceable()) {
				pos = pos.down();
			}

			int range = (getSize(stack) ^ 1) / 2;

			Direction dir = rtr.getFace();
			Direction rotationDir = Direction.fromAngle(player.rotationYaw);

			boolean pitchedVertically = Math.abs(player.rotationPitch) > 50;

			boolean axisX = rotationDir.getAxis() == Axis.X;
			boolean axisZ = rotationDir.getAxis() == Axis.Z;

			int xOff = axisZ || pitchedVertically ? range : 0;
			int yOff = pitchedVertically ? 0 : range;
			int zOff = axisX || pitchedVertically ? range : 0;

			for (int x = -xOff; x < xOff + 1; x++) {
				for (int y = 0; y < yOff * 2 + 1; y++) {
					for (int z = -zOff; z < zOff + 1; z++) {
						int xp = pos.getX() + x + dir.getXOffset();
						int yp = pos.getY() + y + dir.getYOffset();
						int zp = pos.getZ() + z + dir.getZOffset();

						BlockPos newPos = new BlockPos(xp, yp, zp);
						BlockState state1 = player.world.getBlockState(newPos);
						if (player.world.getWorldBorder().contains(newPos)
								&& (state1.isAir(player.world, newPos) || state1.getMaterial().isReplaceable())) {
							coords.add(newPos);
						}
					}
				}
			}

		}

		return coords;
	}

	public void displayRemainderCounter(PlayerEntity player, ItemStack stack) {
		Block block = getBlock(stack);
		int count = ItemExchangeRod.getInventoryItemCount(player, stack, block);
		if (!player.world.isRemote) {
			ItemsRemainingRenderHandler.send(player, new ItemStack(block), count);
		}
	}

	private boolean setBlock(ItemStack stack, BlockState state) {
		if (!state.isAir()) {
			ItemNBTHelper.setCompound(stack, TAG_BLOCKSTATE, NBTUtil.writeBlockState(state));
			return true;
		}
		return false;
	}

	private static void setSize(ItemStack stack, int size) {
		ItemNBTHelper.setInt(stack, TAG_SIZE, size | 1);
	}

	public static int getSize(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_SIZE, 3) | 1;
	}

	public static Block getBlock(ItemStack stack) {
		return getBlockState(stack).getBlock();
	}

	public static BlockState getBlockState(ItemStack stack) {
		return NBTUtil.readBlockState(ItemNBTHelper.getCompound(stack, TAG_BLOCKSTATE, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tip, ITooltipFlag flags) {
		Block block = getBlock(stack);
		int size = getSize(stack);

		tip.add(new StringTextComponent(size + " x " + size));
		if (block != Blocks.AIR) {
			tip.add(new ItemStack(block).getDisplayName().copyRaw().mergeStyle(TextFormatting.GRAY));
		}
	}

}
