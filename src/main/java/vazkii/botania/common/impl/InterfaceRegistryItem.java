/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.impl;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import vazkii.botania.api.InterfaceRegistry;
import vazkii.botania.common.Botania;

import javax.annotation.Nullable;

import java.util.HashMap;

public class InterfaceRegistryItem<V> implements InterfaceRegistry<Item, V> {
	private final Class<V> vClass;
	private HashMap<ResourceLocation, V> registry = new HashMap<>();

	public InterfaceRegistryItem(Class<V> vClass) {
		this.vClass = vClass;
	}

	@Override
	public void register(Item item, V iface) {
		ResourceLocation resLoc = Registry.ITEM.getKey(item);
		if (!registry.containsKey(resLoc) && !vClass.isInstance(item)) {
			registry.put(resLoc, iface);
		} else {
			Botania.LOGGER.warn("Redundant " + vClass + " registration: " + resLoc);
		}
	}

	@Nullable
	@Override
	public V get(Item item) {
		if (vClass.isInstance(item)) {
			return vClass.cast(item);
		} else {
			return registry.get(Registry.ITEM.getKey(item));
		}
	}
}
