/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.spark.IManaSpark;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.SparkUpgradeType;
import vazkii.botania.common.item.ItemSparkUpgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TileSparkChanger extends TileExposedSimpleInventory {
	public TileSparkChanger() {
		super(ModTiles.SPARK_CHANGER);
	}

	public void doSwap() {
		if (world.isRemote) {
			return;
		}

		ItemStack changeStack = getItemHandler().getStackInSlot(0);
		List<ISparkAttachable> attachables = new ArrayList<>();
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			TileEntity tile = world.getTileEntity(pos.offset(dir));
			if (tile instanceof ISparkAttachable) {
				ISparkAttachable attach = (ISparkAttachable) tile;
				IManaSpark spark = attach.getAttachedSpark();
				if (spark != null) {
					SparkUpgradeType upg = spark.getUpgrade();
					SparkUpgradeType newUpg = changeStack.isEmpty() ? SparkUpgradeType.NONE : ((ItemSparkUpgrade) changeStack.getItem()).type;
					if (upg != newUpg) {
						attachables.add(attach);
					}
				}
			}
		}

		if (attachables.size() > 0) {
			ISparkAttachable attach = attachables.get(world.rand.nextInt(attachables.size()));
			IManaSpark spark = attach.getAttachedSpark();
			SparkUpgradeType upg = spark.getUpgrade();
			ItemStack sparkStack = ItemSparkUpgrade.getByType(upg);
			SparkUpgradeType newUpg = changeStack.isEmpty() ? SparkUpgradeType.NONE : ((ItemSparkUpgrade) changeStack.getItem()).type;
			spark.setUpgrade(newUpg);
			Collection<IManaSpark> transfers = spark.getTransfers();
			if (transfers != null) {
				transfers.clear();
			}
			getItemHandler().setInventorySlotContents(0, sparkStack);
		}
	}

	@Override
	protected Inventory createItemHandler() {
		return new Inventory(1) {
			@Override
			public int getInventoryStackLimit() {
				return 1;
			}

			@Override
			public boolean isItemValidForSlot(int index, ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() instanceof ItemSparkUpgrade;
			}
		};
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world != null && !world.isRemote) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}

}
