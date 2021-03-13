/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.impl;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import vazkii.botania.api.InterfaceRegistry;
import vazkii.botania.common.Botania;

import javax.annotation.Nullable;

import java.util.HashMap;

public class InterfaceRegistryBlock<V> implements InterfaceRegistry<Block, V> {
	private final Class<V> vClass;
	private HashMap<ResourceLocation, V> registry = new HashMap<>();

	public InterfaceRegistryBlock(Class<V> vClass) {
		this.vClass = vClass;
	}

	@Override
	public void register(Block block, V iface) {
		ResourceLocation resLoc = Registry.BLOCK.getKey(block);
		if (!registry.containsKey(resLoc) && !vClass.isInstance(block)) {
			registry.put(resLoc, iface);
		} else {
			Botania.LOGGER.warn("Redundant " + vClass + " registration: " + resLoc);
		}
	}

	@Nullable
	@Override
	public V get(Block block) {
		if (vClass.isInstance(block)) {
			return vClass.cast(block);
		} else {
			return registry.get(Registry.BLOCK.getKey(block));
		}
	}
}
