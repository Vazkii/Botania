/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.item.ISparkEntity;
import vazkii.botania.common.network.PacketBotaniaEffect;
import vazkii.botania.common.network.PacketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LensPaint extends Lens {

	@Override
	public boolean collideBurst(IManaBurst burst, RayTraceResult pos, boolean isManaBlock, boolean dead, ItemStack stack) {
		Entity entity = burst.entity();
		int storedColor = ItemLens.getStoredColor(stack);
		if (!entity.world.isRemote && !burst.isFake() && storedColor > -1 && storedColor < 17) {
			if (pos.getType() == RayTraceResult.Type.ENTITY) {
				Entity collidedWith = ((EntityRayTraceResult) pos).getEntity();
				if (collidedWith instanceof SheepEntity) {
					int r = 20;
					SheepEntity sheep = (SheepEntity) collidedWith;
					DyeColor sheepColor = sheep.getFleeceColor();
					List<SheepEntity> sheepList = entity.world.getEntitiesWithinAABB(SheepEntity.class,
							new AxisAlignedBB(sheep.getPosX() - r, sheep.getPosY() - r, sheep.getPosZ() - r,
									sheep.getPosX() + r, sheep.getPosY() + r, sheep.getPosZ() + r));
					for (SheepEntity other : sheepList) {
						if (other.getFleeceColor() == sheepColor) {
							other.setFleeceColor(DyeColor.byId(storedColor == 16 ? other.world.rand.nextInt(16) : storedColor));
						}
					}
					dead = true;
				} else if (collidedWith instanceof ISparkEntity) {
					((ISparkEntity) collidedWith).setNetwork(DyeColor.byId(storedColor == 16 ? collidedWith.world.rand.nextInt(16) : storedColor));
				}
			} else if (pos.getType() == RayTraceResult.Type.BLOCK) {
				BlockPos hit = ((BlockRayTraceResult) pos).getPos();
				BlockState state = entity.world.getBlockState(hit);
				ResourceLocation blockId = Registry.BLOCK.getKey(state.getBlock());

				if (BotaniaAPI.instance().getPaintableBlocks().containsKey(blockId)) {
					List<BlockPos> coordsToPaint = new ArrayList<>();
					List<BlockPos> coordsFound = new ArrayList<>();
					coordsFound.add(hit);

					do {
						List<BlockPos> iterCoords = new ArrayList<>(coordsFound);
						for (BlockPos coords : iterCoords) {
							coordsFound.remove(coords);
							coordsToPaint.add(coords);

							for (Direction dir : Direction.values()) {
								BlockPos coords_ = coords.offset(dir);
								BlockState state_ = entity.world.getBlockState(coords_);
								if (state_ == state && !coordsFound.contains(coords_) && !coordsToPaint.contains(coords_)) {
									coordsFound.add(coords_);
								}
							}
						}
					} while (!coordsFound.isEmpty() && coordsToPaint.size() < 1000);

					for (BlockPos coords : coordsToPaint) {
						DyeColor placeColor = DyeColor.byId(storedColor == 16 ? entity.world.rand.nextInt(16) : storedColor);
						BlockState stateThere = entity.world.getBlockState(coords);

						Function<DyeColor, Block> f = BotaniaAPI.instance().getPaintableBlocks().get(blockId);
						Block newBlock = f.apply(placeColor);
						if (newBlock != stateThere.getBlock()) {
							entity.world.setBlockState(coords, newBlock.getDefaultState());
							PacketHandler.sendToNearby(entity.world, coords,
									new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.PAINT_LENS, coords.getX(), coords.getY(), coords.getZ(), placeColor.getId()));
						}
					}
				}
			}
		}

		return dead;
	}

}
