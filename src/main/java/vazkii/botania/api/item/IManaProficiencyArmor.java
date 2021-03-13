/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import vazkii.botania.api.InterfaceRegistry;
import vazkii.botania.api.mana.ManaProficiencyEvent;

/**
 * An armor item that implements this gives the player wearing it mana proficiency, by
 * which it makes Rods the player use have a stronger effect. This is a boolean, and only
 * one armor piece with this returning true is required for the player to be considered
 * to be having proficiency, by which it's suggested that this is used alongside an
 * armor set, where only one piece implements it.
 */
public interface IManaProficiencyArmor {
	static InterfaceRegistry<Item, IManaProficiencyArmor> registry() {
		return ItemAPI.instance().getManaProficiencyArmorRegistry();
	}

	default boolean shouldGiveProficiency(ItemStack armorStack, EquipmentSlotType slot, PlayerEntity player, ItemStack rod) {
		return false;
	}

	static boolean hasProficiency(PlayerEntity player, ItemStack rod) {
		boolean proficient = false;

		for (EquipmentSlotType e : EquipmentSlotType.values()) {
			if (e.getSlotType() != EquipmentSlotType.Group.ARMOR) {
				continue;
			}
			ItemStack armor = player.getItemStackFromSlot(e);
			if (!armor.isEmpty()) {
				Item item = armor.getItem();
				IManaProficiencyArmor proficiencyArmor = IManaProficiencyArmor.registry().get(item);
				if (proficiencyArmor != null && proficiencyArmor.shouldGiveProficiency(armor, e, player, rod)) {
					proficient = true;
					break;
				}
			}
		}

		ManaProficiencyEvent event = new ManaProficiencyEvent(player, rod, proficient);
		MinecraftForge.EVENT_BUS.post(event);

		return event.isProficient();
	}

}
