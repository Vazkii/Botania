/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.subtile.functional;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import vazkii.botania.api.item.IFlowerPlaceable;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.common.block.ModSubtiles;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.mixin.AccessorItemEntity;

import javax.annotation.Nonnull;

import java.util.*;

public class SubTileRannuncarpus extends TileEntityFunctionalFlower {
	private static final int PICKUP_RANGE = 2;
	private static final int PICKUP_RANGE_Y = 3;
	private static final int RANGE_PLACE_MANA = 8;
	private static final int RANGE_PLACE = 6;
	private static final int RANGE_PLACE_Y = 6;

	private static final int RANGE_PLACE_MANA_MINI = 3;
	private static final int RANGE_PLACE_MINI = 2;
	private static final int RANGE_PLACE_Y_MINI = 2;

	public SubTileRannuncarpus(TileEntityType<?> type) {
		super(type);
	}

	public SubTileRannuncarpus() {
		this(ModSubtiles.RANNUNCARPUS);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getWorld().isRemote || redstoneSignal > 0) {
			return;
		}

		if (ticksExisted % 10 == 0) {
			List<ItemEntity> items = getWorld().getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(getEffectivePos().add(-PICKUP_RANGE, -PICKUP_RANGE_Y, -PICKUP_RANGE), getEffectivePos().add(PICKUP_RANGE + 1, PICKUP_RANGE_Y + 1, PICKUP_RANGE + 1)));
			List<BlockPos> validPositions = getCandidatePositions();
			int slowdown = getSlowdownFactor();

			for (ItemEntity item : items) {
				int age = ((AccessorItemEntity) item).getAge();
				if (age < 60 + slowdown || !item.isAlive() || item.getItem().isEmpty()) {
					continue;
				}

				ItemStack stack = item.getItem();
				Item stackItem = stack.getItem();
				ResourceLocation id = Registry.ITEM.getKey(stackItem);
				if (ConfigHandler.blacklistedRannuncarpusModIds.contains(id.getNamespace())
						|| ConfigHandler.blacklistedRannuncarpusItems.contains(id)) {
					continue;
				}

				IFlowerPlaceable placeable = IFlowerPlaceable.registry().get(stackItem);
				if (stackItem instanceof BlockItem || placeable != null) {
					if (!validPositions.isEmpty()) {
						BlockPos coords = validPositions.get(getWorld().rand.nextInt(validPositions.size()));
						BlockRayTraceResult ray = new BlockRayTraceResult(new Vector3d(coords.getX() + 0.5, coords.getY() + 1, coords.getZ() + 0.5), Direction.UP, coords, false);
						BlockItemUseContext ctx = new RannuncarpusPlaceContext(getWorld(), stack, ray, pos);

						boolean success = false;
						if (placeable != null) {
							success = placeable.tryPlace(this, ctx);
						}
						if (stackItem instanceof BlockItem) {
							success = ((BlockItem) stackItem).tryPlace(ctx).isSuccessOrConsume();
						}

						if (success) {
							if (ConfigHandler.COMMON.blockBreakParticles.get()) {
								BlockState state = getWorld().getBlockState(ctx.getPos());
								getWorld().playEvent(2001, coords, Block.getStateId(state));
							}
							validPositions.remove(coords);
							if (getMana() > 1) {
								addMana(-1);
							}
							return;
						}
					}
				}
			}
		}
	}

	public BlockState getUnderlyingBlock() {
		return getWorld().getBlockState(getEffectivePos().down(isFloating() ? 1 : 2));
	}

	private List<BlockPos> getCandidatePositions() {
		int rangePlace = getPlaceRange();
		int rangePlaceY = getVerticalPlaceRange();
		BlockPos center = getEffectivePos();
		BlockState filter = getUnderlyingBlock();
		List<BlockPos> ret = new ArrayList<>();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(center.add(-rangePlace, -rangePlaceY, -rangePlace),
				center.add(rangePlace, rangePlaceY, rangePlace))) {
			BlockState state = getWorld().getBlockState(pos);
			BlockState up = getWorld().getBlockState(pos.up());
			if (filter == state && (up.isAir(getWorld(), pos.up()) || up.getMaterial().isReplaceable())) {
				ret.add(pos.toImmutable());
			}
		}
		return ret;
	}

	@Override
	public boolean acceptsRedstone() {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHUD(MatrixStack ms, Minecraft mc) {
		super.renderHUD(ms, mc);

		BlockState filter = getUnderlyingBlock();
		ItemStack recieverStack = new ItemStack(filter.getBlock());
		int color = getColor();

		if (!recieverStack.isEmpty()) {
			ITextComponent stackName = recieverStack.getDisplayName();
			int width = 16 + mc.fontRenderer.getStringPropertyWidth(stackName) / 2;
			int x = mc.getMainWindow().getScaledWidth() / 2 - width;
			int y = mc.getMainWindow().getScaledHeight() / 2 + 30;

			mc.fontRenderer.func_243246_a(ms, stackName, x + 20, y + 5, color);
			mc.getItemRenderer().renderItemAndEffectIntoGUI(recieverStack, x, y);
		}

	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(getEffectivePos(), getPlaceRange());
	}

	@Override
	public RadiusDescriptor getSecondaryRadius() {
		if (getPlaceRange() == PICKUP_RANGE) {
			return null;
		}
		return new RadiusDescriptor.Square(getEffectivePos(), PICKUP_RANGE);
	}

	public int getPlaceRange() {
		return getMana() > 0 ? RANGE_PLACE_MANA : RANGE_PLACE;
	}

	public int getVerticalPlaceRange() {
		return RANGE_PLACE_Y;
	}

	@Override
	public int getMaxMana() {
		return 20;
	}

	@Override
	public int getColor() {
		return 0xFFB27F;
	}

	public static class Mini extends SubTileRannuncarpus {
		public Mini() {
			super(ModSubtiles.RANNUNCARPUS_CHIBI);
		}

		@Override
		public int getPlaceRange() {
			return getMana() > 0 ? RANGE_PLACE_MANA_MINI : RANGE_PLACE_MINI;
		}

		@Override
		public int getVerticalPlaceRange() {
			return RANGE_PLACE_Y_MINI;
		}
	}

	// BlockItemUseContext uses a nullable player field without checking it -.-
	private static class RannuncarpusPlaceContext extends BlockItemUseContext {
		private final Direction[] lookDirs;
		private final float placementYaw;

		public RannuncarpusPlaceContext(World world, ItemStack stack, BlockRayTraceResult rtr, BlockPos flowerPos) {
			super(world, null, Hand.MAIN_HAND, stack, rtr);
			int dx = rtr.getPos().getX() - flowerPos.getX();
			int dy = rtr.getPos().getY() - flowerPos.getY();
			int dz = rtr.getPos().getZ() - flowerPos.getZ();

			Direction xClosest = dx >= 0 ? Direction.EAST : Direction.WEST;
			Direction yClosest = dy >= 0 ? Direction.UP : Direction.DOWN;
			Direction zClosest = dz >= 0 ? Direction.SOUTH : Direction.NORTH;

			List<Direction> directions = sortThree(xClosest, yClosest, zClosest, Math.abs(dx), Math.abs(dy), Math.abs(dz));

			Direction first = directions.get(0);
			Direction second = directions.get(1);
			Direction third = directions.get(2);

			lookDirs = new Direction[] {
					first,
					second,
					third,
					third.getOpposite(),
					second.getOpposite(),
					first.getOpposite()
			};

			placementYaw = (float) (-MathHelper.atan2(dx, dz) * 180 / Math.PI);
		}

		/**
		 * Arrange a, b and c such that their corresponding ints (a -> aInt) are in descending order.
		 */
		private static <T> List<T> sortThree(T a, T b, T c, int aInt, int bInt, int cInt) {
			if (aInt >= bInt) {
				if (bInt >= cInt) {
					return ImmutableList.of(a, b, c);
				} else {
					return cInt >= aInt ? ImmutableList.of(c, a, b) : ImmutableList.of(a, c, b);
				}
			} else if (bInt >= cInt) {
				return cInt >= aInt ? ImmutableList.of(b, c, a) : ImmutableList.of(b, a, c);
			} else {
				return ImmutableList.of(c, b, a);
			}
		}

		@Nonnull
		@Override
		public Direction getNearestLookingDirection() {
			return getNearestLookingDirections()[0];
		}

		@Nonnull
		@Override
		public Direction[] getNearestLookingDirections() {
			return lookDirs;
		}

		@Nonnull
		@Override
		public Direction getPlacementHorizontalFacing() {
			return getNearestLookingDirection().getAxis().isHorizontal() ? getNearestLookingDirection() : getNearestLookingDirections()[1];
		}

		@Override
		public float getPlacementYaw() {
			return placementYaw;
		}
	}

}
