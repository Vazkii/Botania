/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.impl.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import vazkii.botania.api.InterfaceRegistry;
import vazkii.botania.api.item.*;
import vazkii.botania.common.impl.InterfaceRegistryBlock;
import vazkii.botania.common.impl.InterfaceRegistryItem;

public class ItemAPIImpl implements ItemAPI {
	InterfaceRegistryItem<IAncientWillContainer> ANCIENT_WILL_CONTAINERS = new InterfaceRegistryItem<>(IAncientWillContainer.class);
	InterfaceRegistryItem<IAvatarWieldable> AVATAR_WIELDABLES = new InterfaceRegistryItem<>(IAvatarWieldable.class);
	InterfaceRegistryItem<IBlockProvider> BLOCK_PROVIDERS = new InterfaceRegistryItem<>(IBlockProvider.class);
	InterfaceRegistryItem<ICosmeticAttachable> COSMETIC_ATTACHABLES = new InterfaceRegistryItem<>(ICosmeticAttachable.class);
	InterfaceRegistryItem<ICosmeticBauble> COSMETIC_BAUBLES = new InterfaceRegistryItem<>(ICosmeticBauble.class);
	InterfaceRegistryItem<IFlowerPlaceable> FLOWER_PLACEABLES = new InterfaceRegistryItem<>(IFlowerPlaceable.class);
	InterfaceRegistryBlock<IHourglassTrigger> HOURGLASS_TRIGGERS = new InterfaceRegistryBlock<>(IHourglassTrigger.class);
	InterfaceRegistryBlock<IPetalApothecary> PETAL_APOTHECARIES = new InterfaceRegistryBlock<>(IPetalApothecary.class);
	InterfaceRegistryItem<IRelic> RELICS = new InterfaceRegistryItem<>(IRelic.class);
	InterfaceRegistryItem<IPhantomInkable> PHANTOM_INKABLES = new InterfaceRegistryItem<>(IPhantomInkable.class);
	InterfaceRegistryBlock<IHornHarvestable> HORN_HARVESTABLES = new InterfaceRegistryBlock<>(IHornHarvestable.class);
	InterfaceRegistryItem<IManaDissolvable> MANA_DISSOLVABLES = new InterfaceRegistryItem<>(IManaDissolvable.class);
	InterfaceRegistryItem<ISequentialBreaker> SEQUENTIAL_BREAKERS = new InterfaceRegistryItem<>(ISequentialBreaker.class);
	InterfaceRegistryItem<IManaProficiencyArmor> MANA_PROFICIENCY_ARMORS = new InterfaceRegistryItem<>(IManaProficiencyArmor.class);
	InterfaceRegistryItem<ISortableTool> SORTABLE_TOOLS = new InterfaceRegistryItem<>(ISortableTool.class);
	InterfaceRegistryItem<IWireframeCoordinateListProvider> WIREFRAME_COORDINATE_LIST_PROVIDERS = new InterfaceRegistryItem<>(IWireframeCoordinateListProvider.class);

	@Override
	public InterfaceRegistry<Item, IAncientWillContainer> getAncientWillContainerRegistry() {
		return ANCIENT_WILL_CONTAINERS;
	}

	@Override
	public InterfaceRegistry<Item, IAvatarWieldable> getAvatarWieldableRegistry() {
		return AVATAR_WIELDABLES;
	}

	@Override
	public InterfaceRegistry<Item, IBlockProvider> getBlockProviderRegistry() {
		return BLOCK_PROVIDERS;
	}

	@Override
	public InterfaceRegistry<Item, ICosmeticAttachable> getCosmeticAttachableRegistry() {
		return COSMETIC_ATTACHABLES;
	}

	@Override
	public InterfaceRegistry<Item, ICosmeticBauble> getCosmeticBaubleRegistry() {
		return COSMETIC_BAUBLES;
	}

	@Override
	public InterfaceRegistry<Item, IFlowerPlaceable> getFlowerPlaceableRegistry() {
		return FLOWER_PLACEABLES;
	}

	@Override
	public InterfaceRegistry<Block, IHourglassTrigger> getHourglassTriggerRegistry() {
		return HOURGLASS_TRIGGERS;
	}

	@Override
	public InterfaceRegistry<Block, IPetalApothecary> getPetalApothecaryRegistry() {
		return PETAL_APOTHECARIES;
	}

	@Override
	public InterfaceRegistry<Item, IRelic> getRelicRegistry() {
		return RELICS;
	}

	@Override
	public InterfaceRegistry<Item, IPhantomInkable> getPhantomInkableRegistry() {
		return PHANTOM_INKABLES;
	}

	@Override
	public InterfaceRegistry<Block, IHornHarvestable> getHornHarvestableRegistry() {
		return HORN_HARVESTABLES;
	}

	@Override
	public InterfaceRegistry<Item, IManaDissolvable> getManaDissolvableRegistry() {
		return MANA_DISSOLVABLES;
	}

	@Override
	public InterfaceRegistry<Item, ISequentialBreaker> getSequentialBreakerRegistry() {
		return SEQUENTIAL_BREAKERS;
	}

	@Override
	public InterfaceRegistry<Item, IManaProficiencyArmor> getManaProficiencyArmorRegistry() {
		return MANA_PROFICIENCY_ARMORS;
	}

	@Override
	public InterfaceRegistry<Item, ISortableTool> getSortableToolRegistry() {
		return SORTABLE_TOOLS;
	}

	@Override
	public InterfaceRegistry<Item, IWireframeCoordinateListProvider> getWireframeCoordinateListProviderRegistry() {
		return WIREFRAME_COORDINATE_LIST_PROVIDERS;
	}
}
