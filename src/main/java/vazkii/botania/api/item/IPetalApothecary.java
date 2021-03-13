/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.IStringSerializable;

import vazkii.botania.api.InterfaceRegistry;

import javax.annotation.Nonnull;

import java.util.Locale;

/**
 * Base Interface for the Petal Apothecary block. Can
 * be safely casted to TileEntity.
 */
public interface IPetalApothecary {
	static InterfaceRegistry<Block, IPetalApothecary> registry() {
		return ItemAPI.instance().getPetalApothecaryRegistry();
	}

	enum State implements IStringSerializable {
		EMPTY,
		WATER,
		LAVA;

		@Nonnull
		@Override
		public String getString() {
			return name().toLowerCase(Locale.ROOT);
		}

		public Fluid asVanilla() {
			switch (this) {
			default:
			case EMPTY:
				return Fluids.EMPTY;
			case WATER:
				return Fluids.WATER;
			case LAVA:
				return Fluids.LAVA;
			}
		}
	}

	/**
	 * Set the contained fluid.
	 */
	void setFluid(State fluid);

	/**
	 * Get the contained fluid.
	 */
	State getFluid();
}
