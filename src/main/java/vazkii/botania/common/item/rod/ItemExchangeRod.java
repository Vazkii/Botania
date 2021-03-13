/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.rod;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.IBlockProvider;
import vazkii.botania.api.item.IManaProficiencyArmor;
import vazkii.botania.api.item.IWireframeCoordinateListProvider;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.handler.ItemsRemainingRenderHandler;
import vazkii.botania.common.block.BlockPlatform;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class ItemExchangeRod extends Item implements IManaUsingItem, IWireframeCoordinateListProvider {

	private static final int RANGE = 3;
	private static final int COST = 40;

	private static final String TAG_BLOCK_NAME = "blockName";
	private static final String TAG_TARGET_BLOCK_NAME = "targetBlock";
	private static final String TAG_SWAPPING = "swapping";
	private static final String TAG_SELECT_X = "selectX";
	private static final String TAG_SELECT_Y = "selectY";
	private static final String TAG_SELECT_Z = "selectZ";
	private static final String TAG_EXTRA_RANGE = "extraRange";

	public ItemExchangeRod(Properties props) {
		super(props);
		MinecraftForge.EVENT_BUS.addListener(this::onLeftClick);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		PlayerEntity player = ctx.getPlayer();
		ItemStack stack = ctx.getItem();
		BlockState wstate = world.getBlockState(pos);

		if (player != null && player.isSneaking()) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile == null) {
				if (BlockPlatform.isValidBlock(wstate, world, pos)) {
					setBlock(stack, wstate);

					displayRemainderCounter(player, stack);
					return ActionResultType.SUCCESS;
				}
			}
		} else if (canExchange(stack) && !ItemNBTHelper.getBoolean(stack, TAG_SWAPPING, false)) {
			BlockState state = getState(stack);
			List<BlockPos> swap = getTargetPositions(world, stack, state, pos, wstate.getBlock());
			if (swap.size() > 0) {
				ItemNBTHelper.setBoolean(stack, TAG_SWAPPING, true);
				ItemNBTHelper.setInt(stack, TAG_SELECT_X, pos.getX());
				ItemNBTHelper.setInt(stack, TAG_SELECT_Y, pos.getY());
				ItemNBTHelper.setInt(stack, TAG_SELECT_Z, pos.getZ());
				setTarget(stack, wstate.getBlock());
			}
		}

		return ActionResultType.SUCCESS;
	}

	private void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
		ItemStack stack = event.getItemStack();
		if (!stack.isEmpty() && stack.getItem() == this && canExchange(stack) && ManaItemHandler.instance().requestManaExactForTool(stack, event.getPlayer(), COST, false)) {
			if (exchange(event.getWorld(), event.getPlayer(), event.getPos(), stack, getState(stack))) {
				ManaItemHandler.instance().requestManaExactForTool(stack, event.getPlayer(), COST, true);
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		if (!canExchange(stack) || !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		int extraRange = ItemNBTHelper.getInt(stack, TAG_EXTRA_RANGE, 1);
		int extraRangeNew = IManaProficiencyArmor.hasProficiency(player, stack) ? 3 : 1;
		if (extraRange != extraRangeNew) {
			ItemNBTHelper.setInt(stack, TAG_EXTRA_RANGE, extraRangeNew);
		}

		BlockState state = getState(stack);
		if (ItemNBTHelper.getBoolean(stack, TAG_SWAPPING, false)) {
			if (!ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, false)) {
				ItemNBTHelper.setBoolean(stack, TAG_SWAPPING, false);
				return;
			}

			int x = ItemNBTHelper.getInt(stack, TAG_SELECT_X, 0);
			int y = ItemNBTHelper.getInt(stack, TAG_SELECT_Y, 0);
			int z = ItemNBTHelper.getInt(stack, TAG_SELECT_Z, 0);
			Block target = getTargetState(stack);
			List<BlockPos> swap = getTargetPositions(world, stack, state, new BlockPos(x, y, z), target);
			if (swap.size() == 0) {
				ItemNBTHelper.setBoolean(stack, TAG_SWAPPING, false);
				return;
			}

			BlockPos coords = swap.get(world.rand.nextInt(swap.size()));
			boolean exchange = exchange(world, player, coords, stack, state);
			if (exchange) {
				ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, true);
			} else {
				ItemNBTHelper.setBoolean(stack, TAG_SWAPPING, false);
			}
		}
	}

	public List<BlockPos> getTargetPositions(World world, ItemStack stack, BlockState toPlace, BlockPos pos, Block toReplace) {
		// Our result list
		List<BlockPos> coordsList = new ArrayList<>();

		// We subtract 1 from the effective range as the center tile is included
		// So, with a range of 3, we are visiting tiles at -2, -1, 0, 1, 2
		int effRange = RANGE + ItemNBTHelper.getInt(stack, TAG_EXTRA_RANGE, 1) - 1;

		// Iterate in all 3 dimensions through our possible positions.
		for (int offsetX = -effRange; offsetX <= effRange; offsetX++) {
			for (int offsetY = -effRange; offsetY <= effRange; offsetY++) {
				for (int offsetZ = -effRange; offsetZ <= effRange; offsetZ++) {
					BlockPos pos_ = pos.add(offsetX, offsetY, offsetZ);

					BlockState currentState = world.getBlockState(pos_);

					// If this block is not our target, ignore it, as we don't need
					// to consider replacing it
					if (currentState.getBlock() != toReplace) {
						continue;
					}

					// If this block is already the block we're swapping to,
					// we don't need to swap again
					if (currentState == toPlace) {
						continue;
					}

					// Check to see if the block is visible on any side:
					for (Direction dir : Direction.values()) {
						BlockPos adjPos = pos_.offset(dir);
						BlockState adjState = world.getBlockState(adjPos);

						if (!Block.doesSideFillSquare(adjState.getRenderShape(world, pos), dir.getOpposite())) {
							coordsList.add(pos_);
							break;
						}
					}
				}
			}
		}

		return coordsList;
	}

	public boolean exchange(World world, PlayerEntity player, BlockPos pos, ItemStack stack, BlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null) {
			return false;
		}

		ItemStack placeStack = removeFromInventory(player, stack, state.getBlock(), false);
		if (!placeStack.isEmpty()) {
			BlockState stateAt = world.getBlockState(pos);
			Block blockAt = stateAt.getBlock();
			if (!blockAt.isAir(world.getBlockState(pos), world, pos) && stateAt.getPlayerRelativeBlockHardness(player, world, pos) > 0 && stateAt != state) {
				if (!world.isRemote) {
					world.destroyBlock(pos, !player.abilities.isCreativeMode);
					if (!player.abilities.isCreativeMode) {
						removeFromInventory(player, stack, state.getBlock(), true);
					}
					world.setBlockState(pos, state);
					state.getBlock().onBlockPlacedBy(world, pos, state, player, placeStack);
				}
				displayRemainderCounter(player, stack);
				return true;
			}
		}

		return false;
	}

	public boolean canExchange(ItemStack stack) {
		return !getState(stack).isAir();
	}

	public static ItemStack removeFromInventory(PlayerEntity player, IInventory inv, ItemStack stack, Block block, boolean doit) {
		List<ItemStack> providers = new ArrayList<>();
		for (int i = inv.getSizeInventory() - 1; i >= 0; i--) {
			ItemStack invStack = inv.getStackInSlot(i);
			if (invStack.isEmpty()) {
				continue;
			}

			Item item = invStack.getItem();
			if (item == block.asItem()) {
				ItemStack ret;
				if (doit) {
					ret = inv.decrStackSize(i, 1);
				} else {
					ret = invStack.copy();
					ret.setCount(1);
				}
				return ret;
			}

			if (IBlockProvider.registry().has(item)) {
				providers.add(invStack);
			}
		}

		for (ItemStack provStack : providers) {
			IBlockProvider prov = IBlockProvider.registry().get(provStack.getItem());
			if (prov.provideBlock(player, stack, provStack, block, doit)) {
				return new ItemStack(block);
			}
		}

		return ItemStack.EMPTY;
	}

	public static ItemStack removeFromInventory(PlayerEntity player, ItemStack stack, Block block, boolean doit) {
		if (player.abilities.isCreativeMode) {
			return new ItemStack(block);
		}

		ItemStack outStack = removeFromInventory(player, BotaniaAPI.instance().getAccessoriesInventory(player), stack, block, doit);
		if (outStack.isEmpty()) {
			outStack = removeFromInventory(player, player.inventory, stack, block, doit);
		}
		return outStack;
	}

	public static int getInventoryItemCount(PlayerEntity player, ItemStack stack, Block block) {
		if (player.abilities.isCreativeMode) {
			return -1;
		}

		int baubleCount = getInventoryItemCount(player, BotaniaAPI.instance().getAccessoriesInventory(player), stack, block);
		if (baubleCount == -1) {
			return -1;
		}

		int count = getInventoryItemCount(player, player.inventory, stack, block);
		if (count == -1) {
			return -1;
		}

		return count + baubleCount;
	}

	public static int getInventoryItemCount(PlayerEntity player, IInventory inv, ItemStack stack, Block block) {
		if (player.abilities.isCreativeMode) {
			return -1;
		}

		int count = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack invStack = inv.getStackInSlot(i);
			if (invStack.isEmpty()) {
				continue;
			}

			Item item = invStack.getItem();
			if (item == block.asItem()) {
				count += invStack.getCount();
			}

			IBlockProvider prov = IBlockProvider.registry().get(item);
			if (prov != null) {
				int provCount = prov.getBlockCount(player, stack, invStack, block);
				if (provCount == -1) {
					return -1;
				}
				count += provCount;
			}
		}

		return count;
	}

	public void displayRemainderCounter(PlayerEntity player, ItemStack stack) {
		Block block = getState(stack).getBlock();
		int count = getInventoryItemCount(player, stack, block);
		if (!player.world.isRemote) {
			ItemsRemainingRenderHandler.send(player, new ItemStack(block), count);
		}
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

	private void setBlock(ItemStack stack, BlockState state) {
		ItemNBTHelper.setCompound(stack, TAG_BLOCK_NAME, NBTUtil.writeBlockState(state));
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
		BlockState state = getState(stack);
		IFormattableTextComponent cmp = super.getDisplayName(stack).deepCopy();
		if (!state.isAir()) {
			cmp.appendString(" (");
			ITextComponent sub = new ItemStack(state.getBlock()).getDisplayName();
			cmp.append(sub.deepCopy().mergeStyle(TextFormatting.GREEN));
			cmp.appendString(")");
		}
		return cmp;
	}

	public static BlockState getState(ItemStack stack) {
		return NBTUtil.readBlockState(ItemNBTHelper.getCompound(stack, TAG_BLOCK_NAME, false));
	}

	private void setTarget(ItemStack stack, Block block) {
		ItemNBTHelper.setString(stack, TAG_TARGET_BLOCK_NAME, Registry.BLOCK.getKey(block).toString());
	}

	public static Block getTargetState(ItemStack stack) {
		ResourceLocation id = new ResourceLocation(ItemNBTHelper.getString(stack, TAG_TARGET_BLOCK_NAME, "minecraft:air"));
		return Registry.BLOCK.getOrDefault(id);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public List<BlockPos> getWireframesToDraw(PlayerEntity player, ItemStack stack) {
		ItemStack holding = player.getHeldItemMainhand();
		if (holding != stack || !canExchange(stack)) {
			return ImmutableList.of();
		}

		BlockState state = getState(stack);

		RayTraceResult pos = Minecraft.getInstance().objectMouseOver;
		if (pos != null && pos.getType() == RayTraceResult.Type.BLOCK) {
			BlockPos bPos = ((BlockRayTraceResult) pos).getPos();
			Block target = Minecraft.getInstance().world.getBlockState(bPos).getBlock();
			if (ItemNBTHelper.getBoolean(stack, TAG_SWAPPING, false)) {
				bPos = new BlockPos(
						ItemNBTHelper.getInt(stack, TAG_SELECT_X, 0),
						ItemNBTHelper.getInt(stack, TAG_SELECT_Y, 0),
						ItemNBTHelper.getInt(stack, TAG_SELECT_Z, 0)
				);
				target = getTargetState(stack);
			}

			if (!player.world.isAirBlock(bPos)) {
				List<BlockPos> coordsList = getTargetPositions(player.world, stack, state, bPos, target);
				coordsList.removeIf(bPos::equals);
				return coordsList;
			}

		}
		return ImmutableList.of();
	}

}
