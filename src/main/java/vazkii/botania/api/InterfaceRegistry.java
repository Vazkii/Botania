/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api;

import javax.annotation.Nullable;

public interface InterfaceRegistry<T, V> {
	@Nullable
	V get(T item);

	default boolean has(T item) {
		return get(item) != null;
	}

	void register(T t, V v);

	class Dummy<T, V> implements InterfaceRegistry<T, V> {
		@Override
		@Nullable
		public V get(T item) {
			return null;
		}

		@Override
		public void register(T t, V v) {

		}
	}
}
