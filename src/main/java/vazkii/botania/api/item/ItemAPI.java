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
import net.minecraft.item.Item;
import net.minecraft.util.LazyValue;

import org.apache.logging.log4j.LogManager;

import vazkii.botania.api.InterfaceRegistry;

public interface ItemAPI {
	LazyValue<ItemAPI> INSTANCE = new LazyValue<>(() -> {
		try {
			return (ItemAPI) Class.forName("vazkii.botania.common.impl.item.ItemAPIImpl").getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			LogManager.getLogger().warn("Unable to find ItemAPIImpl, using a dummy");
			return new ItemAPI() {};
		}
	});

	static ItemAPI instance() {
		return INSTANCE.getValue();
	}

	default InterfaceRegistry<Item, IAncientWillContainer> getAncientWillContainerRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IAvatarWieldable> getAvatarWieldableRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IBlockProvider> getBlockProviderRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, ICosmeticAttachable> getCosmeticAttachableRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, ICosmeticBauble> getCosmeticBaubleRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IFlowerPlaceable> getFlowerPlaceableRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Block, IHourglassTrigger> getHourglassTriggerRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Block, IPetalApothecary> getPetalApothecaryRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IRelic> getRelicRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IPhantomInkable> getPhantomInkableRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Block, IHornHarvestable> getHornHarvestableRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IManaDissolvable> getManaDissolvableRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, ISequentialBreaker> getSequentialBreakerRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IManaProficiencyArmor> getManaProficiencyArmorRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, ISortableTool> getSortableToolRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}

	default InterfaceRegistry<Item, IWireframeCoordinateListProvider> getWireframeCoordinateListProviderRegistry() {
		return new InterfaceRegistry.Dummy<>();
	}
}
