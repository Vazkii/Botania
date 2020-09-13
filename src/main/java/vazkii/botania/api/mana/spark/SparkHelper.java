/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana.spark;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Stream;

public final class SparkHelper {

	public static final int SPARK_SCAN_RANGE = 12;

	public static Stream<IManaSpark> getSparksAround(World world, double x, double y, double z, DyeColor color) {
		return getSparksAround(world, x, y, z).stream().filter(s -> s.getNetwork() == color);
	}

	public static List<IManaSpark> getSparksAround(World world, double x, double y, double z) {
		return SparkHelper.getEntitiesAround(IManaSpark.class, world, x, y, z);
	}

	public static <T> List<T> getEntitiesAround(Class<? extends T> clazz, World world, double x, double y, double z) {
		int r = SPARK_SCAN_RANGE;
		@SuppressWarnings("unchecked")
		List<T> entities = (List<T>) (List<?>) world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r), Predicates.instanceOf(clazz));
		return entities;
	}

}
