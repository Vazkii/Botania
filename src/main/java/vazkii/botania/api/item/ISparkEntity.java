/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;

/**
 * An Entity that implements this is considered a Spark.
 */
public interface ISparkEntity {

	/**
	 * @return The position of the block that this spark is attached to
	 */
	BlockPos getAttachPos();

	/**
	 * Gets the network that this spark is on, or the color it's displaying. Sparks may only connect to others
	 * of the same network.
	 */
	DyeColor getNetwork();

	void setNetwork(DyeColor color);

	/**
	 * @return this spark as an Entity
	 */
	default Entity entity() {
		return (Entity) this;
	}
}
