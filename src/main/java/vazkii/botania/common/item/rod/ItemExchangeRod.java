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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

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

	public ItemExchangeRod(Settings props) {
		super(props);
		AttackBlockCallback.EVENT.register(this::onLeftClick);
	}

	@Nonnull
	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		PlayerEntity player = ctx.getPlayer();
		ItemStack stack = ctx.getStack();
		BlockState wstate = world.getBlockState(pos);

		if (player != null && player.isSneaking()) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile == null) {
				if (BlockPlatform.isValidBlock(wstate, world, pos)) {
					setBlock(stack, wstate);

					displayRemainderCounter(player, stack);
					return ActionResult.SUCCESS;
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

		return ActionResult.SUCCESS;
	}

	private ActionResult onLeftClick(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (player.isSpectator()) {
			return ActionResult.PASS;
		}

		ItemStack stack = player.getStackInHand(hand);
		if (!stack.isEmpty() && stack.getItem() == this) {
			// Returning SUCCESS or FAIL from this callback prevents vanilla from sending the C2S packet for block
			// breaking. Returning PASS does a bunch of things we don't want, like creative block breaking and action
			// acknowledgements, so send a packet directly to trigger this event on the server.
			if (world.isClient()) {
				if (!(player instanceof ClientPlayerEntity)) {
					return ActionResult.PASS; // impossible
				}
				((ClientPlayerEntity) player).networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
				return ActionResult.SUCCESS;
			}

			if (canExchange(stack) && ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, false)) {
				if (exchange(world, player, pos, stack, getState(stack))) {
					ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, true);
				}
			}
			// Always return SUCCESS with rod in hand to prevent any vanilla block breaking, esp. on second packet
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
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

			BlockPos coords = swap.get(world.random.nextInt(swap.size()));
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

						if (!Block.isFaceFullSquare(adjState.getSidesShape(world, pos), dir.getOpposite())) {
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
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile != null) {
			return false;
		}

		ItemStack placeStack = removeFromInventory(player, stack, state.getBlock(), false);
		if (!placeStack.isEmpty()) {
			BlockState stateAt = world.getBlockState(pos);
			if (!stateAt.isAir() && stateAt.calcBlockBreakingDelta(player, world, pos) > 0 && stateAt != state) {
				if (!world.isClient) {
					world.breakBlock(pos, !player.abilities.creativeMode);
					if (!player.abilities.creativeMode) {
						removeFromInventory(player, stack, state.getBlock(), true);
					}
					world.setBlockState(pos, state);
					state.getBlock().onPlaced(world, pos, state, player, placeStack);
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

	public static ItemStack removeFromInventory(PlayerEntity player, Inventory inv, ItemStack stack, Block block, boolean doit) {
		List<ItemStack> providers = new ArrayList<>();
		for (int i = inv.size() - 1; i >= 0; i--) {
			ItemStack invStack = inv.getStack(i);
			if (invStack.isEmpty()) {
				continue;
			}

			Item item = invStack.getItem();
			if (item == block.asItem()) {
				ItemStack ret;
				if (doit) {
					ret = inv.removeStack(i, 1);
				} else {
					ret = invStack.copy();
					ret.setCount(1);
				}
				return ret;
			}

			if (item instanceof IBlockProvider) {
				providers.add(invStack);
			}
		}

		for (ItemStack provStack : providers) {
			IBlockProvider prov = (IBlockProvider) provStack.getItem();
			if (prov.provideBlock(player, stack, provStack, block, doit)) {
				return new ItemStack(block);
			}
		}

		return ItemStack.EMPTY;
	}

	public static ItemStack removeFromInventory(PlayerEntity player, ItemStack stack, Block block, boolean doit) {
		if (player.abilities.creativeMode) {
			return new ItemStack(block);
		}

		ItemStack outStack = removeFromInventory(player, BotaniaAPI.instance().getAccessoriesInventory(player), stack, block, doit);
		if (outStack.isEmpty()) {
			outStack = removeFromInventory(player, player.inventory, stack, block, doit);
		}
		return outStack;
	}

	public static int getInventoryItemCount(PlayerEntity player, ItemStack stack, Block block) {
		if (player.abilities.creativeMode) {
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

	public static int getInventoryItemCount(PlayerEntity player, Inventory inv, ItemStack stack, Block block) {
		if (player.abilities.creativeMode) {
			return -1;
		}

		int count = 0;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack invStack = inv.getStack(i);
			if (invStack.isEmpty()) {
				continue;
			}

			Item item = invStack.getItem();
			if (item == block.asItem()) {
				count += invStack.getCount();
			}

			if (item instanceof IBlockProvider) {
				IBlockProvider prov = (IBlockProvider) item;
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
		if (!player.world.isClient) {
			ItemsRemainingRenderHandler.send(player, new ItemStack(block), count);
		}
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

	private void setBlock(ItemStack stack, BlockState state) {
		ItemNBTHelper.setCompound(stack, TAG_BLOCK_NAME, NbtHelper.fromBlockState(state));
	}

	@Nonnull
	@Override
	public Text getName(@Nonnull ItemStack stack) {
		BlockState state = getState(stack);
		MutableText cmp = super.getName(stack).shallowCopy();
		if (!state.isAir()) {
			cmp.append(" (");
			Text sub = new ItemStack(state.getBlock()).getName();
			cmp.append(sub.shallowCopy().formatted(Formatting.GREEN));
			cmp.append(")");
		}
		return cmp;
	}

	public static BlockState getState(ItemStack stack) {
		return NbtHelper.toBlockState(ItemNBTHelper.getCompound(stack, TAG_BLOCK_NAME, false));
	}

	private void setTarget(ItemStack stack, Block block) {
		ItemNBTHelper.setString(stack, TAG_TARGET_BLOCK_NAME, Registry.BLOCK.getId(block).toString());
	}

	public static Block getTargetState(ItemStack stack) {
		Identifier id = new Identifier(ItemNBTHelper.getString(stack, TAG_TARGET_BLOCK_NAME, "minecraft:air"));
		return Registry.BLOCK.get(id);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public List<BlockPos> getWireframesToDraw(PlayerEntity player, ItemStack stack) {
		ItemStack holding = player.getMainHandStack();
		if (holding != stack || !canExchange(stack)) {
			return ImmutableList.of();
		}

		BlockState state = getState(stack);

		HitResult pos = MinecraftClient.getInstance().crosshairTarget;
		if (pos != null && pos.getType() == HitResult.Type.BLOCK) {
			BlockPos bPos = ((BlockHitResult) pos).getBlockPos();
			Block target = MinecraftClient.getInstance().world.getBlockState(bPos).getBlock();
			if (ItemNBTHelper.getBoolean(stack, TAG_SWAPPING, false)) {
				bPos = new BlockPos(
						ItemNBTHelper.getInt(stack, TAG_SELECT_X, 0),
						ItemNBTHelper.getInt(stack, TAG_SELECT_Y, 0),
						ItemNBTHelper.getInt(stack, TAG_SELECT_Z, 0)
				);
				target = getTargetState(stack);
			}

			if (!player.world.isAir(bPos)) {
				List<BlockPos> coordsList = getTargetPositions(player.world, stack, state, bPos, target);
				coordsList.removeIf(bPos::equals);
				return coordsList;
			}

		}
		return ImmutableList.of();
	}

}
