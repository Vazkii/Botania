/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;

import vazkii.botania.api.item.IAvatarTile;
import vazkii.botania.api.item.IAvatarWieldable;

public class TileAvatar extends TileSimpleInventory implements IAvatarTile, ITickableTileEntity {
	private static final int MAX_MANA = 6400;

	private static final String TAG_ENABLED = "enabled";
	private static final String TAG_TICKS_ELAPSED = "ticksElapsed";
	private static final String TAG_MANA = "mana";

	private boolean enabled;
	private int ticksElapsed;
	private int mana;

	public TileAvatar() {
		super(ModTiles.AVATAR);
	}

	@Override
	public void tick() {
		enabled = true;
		for (Direction dir : Direction.values()) {
			int redstoneSide = world.getRedstonePower(pos.offset(dir), dir);
			if (redstoneSide > 0) {
				enabled = false;
				break;
			}
		}

		ItemStack stack = getItemHandler().getStackInSlot(0);
		IAvatarWieldable wieldable = IAvatarWieldable.registry().get(stack.getItem());
		if (!stack.isEmpty() && wieldable != null) {
			;
			wieldable.onAvatarUpdate(this, stack);
		}

		if (enabled) {
			ticksElapsed++;
		}
	}

	@Override
	public void writePacketNBT(CompoundNBT tag) {
		super.writePacketNBT(tag);
		tag.putBoolean(TAG_ENABLED, enabled);
		tag.putInt(TAG_TICKS_ELAPSED, ticksElapsed);
		tag.putInt(TAG_MANA, mana);
	}

	@Override
	public void readPacketNBT(CompoundNBT tag) {
		super.readPacketNBT(tag);
		enabled = tag.getBoolean(TAG_ENABLED);
		ticksElapsed = tag.getInt(TAG_TICKS_ELAPSED);
		mana = tag.getInt(TAG_MANA);
	}

	@Override
	protected Inventory createItemHandler() {
		return new Inventory(1) {
			@Override
			public int getInventoryStackLimit() {
				return 1;
			}
		};
	}

	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.min(3 * MAX_MANA, this.mana + mana);
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return !getItemHandler().getStackInSlot(0).isEmpty();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public IInventory getInventory() {
		return getItemHandler();
	}

	@Override
	public Direction getAvatarFacing() {
		return getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public int getElapsedFunctionalTicks() {
		return ticksElapsed;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
