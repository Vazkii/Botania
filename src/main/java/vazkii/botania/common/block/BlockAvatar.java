/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import vazkii.botania.api.item.IAvatarWieldable;
import vazkii.botania.common.block.tile.TileAvatar;
import vazkii.botania.common.block.tile.TileSimpleInventory;

import javax.annotation.Nonnull;

public class BlockAvatar extends BlockModWaterloggable implements ITileEntityProvider {

	private static final VoxelShape X_AABB = makeCuboidShape(5, 0, 3.5, 11, 17, 12.5);
	private static final VoxelShape Z_AABB = makeCuboidShape(3.5, 0, 5, 12.5, 17, 11);

	protected BlockAvatar(Properties builder) {
		super(builder);
		setDefaultState(getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		if (state.get(BlockStateProperties.HORIZONTAL_FACING).getAxis() == Direction.Axis.X) {
			return X_AABB;
		} else {
			return Z_AABB;
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileAvatar avatar = (TileAvatar) world.getTileEntity(pos);
		ItemStack stackOnAvatar = avatar.getItemHandler().getStackInSlot(0);
		ItemStack stackOnPlayer = player.getHeldItem(hand);
		if (!stackOnAvatar.isEmpty()) {
			avatar.getItemHandler().setInventorySlotContents(0, ItemStack.EMPTY);
			player.inventory.placeItemBackInInventory(player.world, stackOnAvatar);
			return ActionResultType.SUCCESS;
		} else if (!stackOnPlayer.isEmpty() && IAvatarWieldable.registry().has(stackOnPlayer.getItem())) {
			avatar.getItemHandler().setInventorySlotContents(0, stackOnPlayer.split(1));
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public void onReplaced(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
		if (state.getBlock() != newstate.getBlock()) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileSimpleInventory) {
				InventoryHelper.dropInventoryItems(world, pos, ((TileSimpleInventory) te).getItemHandler());
			}
			super.onReplaced(state, world, pos, newstate, isMoving);
		}
	}

	@Nonnull
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Nonnull
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader world) {
		return new TileAvatar();
	}

	@Nonnull
	@Override
	public BlockState mirror(@Nonnull BlockState state, Mirror mirror) {
		return state.with(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(state.get(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Nonnull
	@Override
	public BlockState rotate(@Nonnull BlockState state, Rotation rot) {
		return state.with(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.get(BlockStateProperties.HORIZONTAL_FACING)));
	}
}
