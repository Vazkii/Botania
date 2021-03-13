/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import vazkii.botania.api.item.ISequentialBreaker;
import vazkii.botania.api.item.IWireframeCoordinateListProvider;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.advancements.LokiPlaceTrigger;
import vazkii.botania.common.core.handler.EquipmentHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.PlayerHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class ItemLokiRing extends ItemRelicBauble implements IWireframeCoordinateListProvider, IManaUsingItem {

	private static final String TAG_CURSOR_LIST = "cursorList";
	private static final String TAG_CURSOR_PREFIX = "cursor";
	private static final String TAG_CURSOR_COUNT = "cursorCount";
	private static final String TAG_X_OFFSET = "xOffset";
	private static final String TAG_Y_OFFSET = "yOffset";
	private static final String TAG_Z_OFFSET = "zOffset";
	private static final String TAG_X_ORIGIN = "xOrigin";
	private static final String TAG_Y_ORIGIN = "yOrigin";
	private static final String TAG_Z_ORIGIN = "zOrigin";

	private static boolean recCall = false;

	public ItemLokiRing(Properties props) {
		super(props);
	}

	public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		PlayerEntity player = event.getPlayer();
		ItemStack lokiRing = getLokiRing(player);
		if (lokiRing.isEmpty() || !player.isSneaking()) {
			return;
		}

		ItemStack stack = event.getItemStack();
		BlockRayTraceResult lookPos = ToolCommons.raytraceFromEntity(player, 10F, true);
		List<BlockPos> cursors = getCursorList(lokiRing);

		if (lookPos.getType() != RayTraceResult.Type.BLOCK) {
			return;
		}

		BlockPos hit = lookPos.getPos();
		if (stack.isEmpty() && event.getHand() == Hand.MAIN_HAND) {
			BlockPos originCoords = getBindingCenter(lokiRing);
			if (!event.getWorld().isRemote) {
				if (originCoords.getY() == -1) {
					// Initiate a new pending list of positions
					setBindingCenter(lokiRing, hit);
					setCursorList(lokiRing, null);
				} else {
					if (originCoords.equals(hit)) {
						// Finalize the pending list of positions
						exitBindingMode(lokiRing);
					} else {
						// Toggle offsets on or off from the pending list of positions
						BlockPos relPos = hit.subtract(originCoords);

						boolean removed = cursors.remove(relPos);
						if (!removed) {
							cursors.add(relPos);
						}
						setCursorList(lokiRing, cursors);
					}
				}
			}

			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		} else {
			int cost = Math.min(cursors.size(), (int) Math.pow(Math.E, cursors.size() * 0.25));
			ItemStack original = stack.copy();
			int successes = 0;
			for (BlockPos cursor : cursors) {
				BlockPos pos = hit.add(cursor);
				if (ManaItemHandler.instance().requestManaExact(lokiRing, player, cost, false)) {
					Vector3d lookHit = lookPos.getHitVec();
					Vector3d newHitVec = new Vector3d(pos.getX() + MathHelper.frac(lookHit.getX()), pos.getY() + MathHelper.frac(lookHit.getY()), pos.getZ() + MathHelper.frac(lookHit.getZ()));
					BlockRayTraceResult newHit = new BlockRayTraceResult(newHitVec, lookPos.getFace(), pos, false);
					ItemUseContext ctx = new ItemUseContext(player, event.getHand(), newHit);

					ActionResultType result;
					if (player.isCreative()) {
						result = PlayerHelper.substituteUse(ctx, original.copy());
					} else {
						result = stack.onItemUse(ctx);
					}

					if (result.isSuccessOrConsume()) {
						ManaItemHandler.instance().requestManaExact(lokiRing, player, cost, true);
						successes++;
					}
				} else {
					break;
				}
			}
			if (player instanceof ServerPlayerEntity) {
				LokiPlaceTrigger.INSTANCE.trigger((ServerPlayerEntity) player, lokiRing, successes);
			}
		}
	}

	public static void breakOnAllCursors(PlayerEntity player, ItemStack stack, BlockPos pos, Direction side) {
		Item item = stack.getItem();
		ItemStack lokiRing = getLokiRing(player);
		ISequentialBreaker breaker = ISequentialBreaker.registry().get(item);
		if (lokiRing.isEmpty() || player.world.isRemote || breaker == null) {
			return;
		}

		if (recCall) {
			return;
		}
		recCall = true;

		List<BlockPos> cursors = getCursorList(lokiRing);

		try {
			for (BlockPos offset : cursors) {
				BlockPos coords = pos.add(offset);
				BlockState state = player.world.getBlockState(coords);
				breaker.breakOtherBlock(player, stack, coords, pos, side);
				ToolCommons.removeBlockWithDrops(player, stack, player.world, coords,
						s -> s.getBlock() == state.getBlock() && s.getMaterial() == state.getMaterial());
			}
		} finally {
			recCall = false;
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, LivingEntity living) {
		setCursorList(stack, null);
	}

	// onUnequipped has itemstack identity issues and doesn't actually fully work, so do this here every tick.
	// This prevents a player from accidentally entering binding mode, then forgetting where the binding center
	// is and thus being unable to exit binding mode.
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean held) {
		super.inventoryTick(stack, world, entity, slot, held);
		// Curios actually calls this method, but with a negative slot, so we can check if we're in the "real" inventory this way
		if (slot >= 0) {
			exitBindingMode(stack);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public List<BlockPos> getWireframesToDraw(PlayerEntity player, ItemStack stack) {
		if (getLokiRing(player) != stack) {
			return ImmutableList.of();
		}

		RayTraceResult lookPos = Minecraft.getInstance().objectMouseOver;

		if (lookPos != null
				&& lookPos.getType() == RayTraceResult.Type.BLOCK
				&& !player.world.isAirBlock(((BlockRayTraceResult) lookPos).getPos())) {
			List<BlockPos> list = getCursorList(stack);
			BlockPos origin = getBindingCenter(stack);

			for (int i = 0; i < list.size(); i++) {
				if (origin.getY() != -1) {
					list.set(i, list.get(i).add(origin));
				} else {
					list.set(i, list.get(i).add(((BlockRayTraceResult) lookPos).getPos()));
				}
			}

			return list;
		}

		return ImmutableList.of();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockPos getSourceWireframe(PlayerEntity player, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		if (getLokiRing(player) == stack) {
			BlockPos currentBuildCenter = getBindingCenter(stack);
			if (currentBuildCenter.getY() != -1) {
				return currentBuildCenter;
			} else if (mc.objectMouseOver instanceof BlockRayTraceResult
					&& mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK
					&& !getCursorList(stack).isEmpty()) {
				return ((BlockRayTraceResult) mc.objectMouseOver).getPos();
			}
		}

		return null;
	}

	private static ItemStack getLokiRing(PlayerEntity player) {
		return EquipmentHandler.findOrEmpty(ModItems.lokiRing, player);
	}

	private static BlockPos getBindingCenter(ItemStack stack) {
		int x = ItemNBTHelper.getInt(stack, TAG_X_ORIGIN, 0);
		int y = ItemNBTHelper.getInt(stack, TAG_Y_ORIGIN, -1);
		int z = ItemNBTHelper.getInt(stack, TAG_Z_ORIGIN, 0);
		return new BlockPos(x, y, z);
	}

	private static void exitBindingMode(ItemStack stack) {
		setBindingCenter(stack, new BlockPos(0, -1, 0));
	}

	private static void setBindingCenter(ItemStack stack, BlockPos pos) {
		ItemNBTHelper.setInt(stack, TAG_X_ORIGIN, pos.getX());
		ItemNBTHelper.setInt(stack, TAG_Y_ORIGIN, pos.getY());
		ItemNBTHelper.setInt(stack, TAG_Z_ORIGIN, pos.getZ());
	}

	private static List<BlockPos> getCursorList(ItemStack stack) {
		CompoundNBT cmp = ItemNBTHelper.getCompound(stack, TAG_CURSOR_LIST, false);
		List<BlockPos> cursors = new ArrayList<>();

		int count = cmp.getInt(TAG_CURSOR_COUNT);
		for (int i = 0; i < count; i++) {
			CompoundNBT cursorCmp = cmp.getCompound(TAG_CURSOR_PREFIX + i);
			int x = cursorCmp.getInt(TAG_X_OFFSET);
			int y = cursorCmp.getInt(TAG_Y_OFFSET);
			int z = cursorCmp.getInt(TAG_Z_OFFSET);
			cursors.add(new BlockPos(x, y, z));
		}

		return cursors;
	}

	private static void setCursorList(ItemStack stack, @Nullable List<BlockPos> cursors) {
		CompoundNBT cmp = new CompoundNBT();
		if (cursors != null) {
			int i = 0;
			for (BlockPos cursor : cursors) {
				CompoundNBT cursorCmp = cursorToCmp(cursor);
				cmp.put(TAG_CURSOR_PREFIX + i, cursorCmp);
				i++;
			}
			cmp.putInt(TAG_CURSOR_COUNT, i);
		}

		ItemNBTHelper.setCompound(stack, TAG_CURSOR_LIST, cmp);
	}

	private static CompoundNBT cursorToCmp(BlockPos pos) {
		CompoundNBT cmp = new CompoundNBT();
		cmp.putInt(TAG_X_OFFSET, pos.getX());
		cmp.putInt(TAG_Y_OFFSET, pos.getY());
		cmp.putInt(TAG_Z_OFFSET, pos.getZ());
		return cmp;
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

	@Override
	public ResourceLocation getAdvancement() {
		return prefix("challenge/loki_ring");
	}

}
