/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.item.ISparkEntity;

public abstract class EntitySparkBase extends Entity implements ISparkEntity {
	private static final String TAG_INVIS = "invis";
	private static final String TAG_NETWORK = "network";
	private static final DataParameter<Integer> NETWORK = EntityDataManager.createKey(EntitySparkBase.class, DataSerializers.VARINT);

	public EntitySparkBase(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	protected void registerData() {
		dataManager.register(NETWORK, 0);
	}

	@Override
	public BlockPos getAttachPos() {
		return getPositionUnderneath();
	}

	@Override
	public DyeColor getNetwork() {
		return DyeColor.byId(dataManager.get(NETWORK));
	}

	@Override
	public void setNetwork(DyeColor color) {
		dataManager.set(NETWORK, color.getId());
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		setInvisible(compound.getBoolean(TAG_INVIS));
		setNetwork(DyeColor.byId(compound.getInt(TAG_NETWORK)));
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		compound.putBoolean(TAG_INVIS, isInvisible());
		compound.putInt(TAG_NETWORK, getNetwork().getId());
	}
}
