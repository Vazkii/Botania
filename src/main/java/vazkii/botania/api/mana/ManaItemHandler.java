/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;

import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.List;

public interface ManaItemHandler {
	LazyValue<ManaItemHandler> INSTANCE = new LazyValue<>(() -> {
		try {
			return (ManaItemHandler) Class.forName("vazkii.botania.common.impl.mana.ManaItemHandlerImpl").getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			LogManager.getLogger().warn("Unable to find ManaItemHandlerImpl, using a dummy");
			return new ManaItemHandler() {};
		}
	});

	static ManaItemHandler instance() {
		return INSTANCE.getValue();
	}

	/**
	 * Gets a list containing all mana-holding items in a player's inventory.
	 * Also includes a call to ManaItemsEvent, so other mods can add items from
	 * their player-associated inventories.
	 * 
	 * @return The list of items
	 */
	default List<ItemStack> getManaItems(PlayerEntity player) {
		return Collections.emptyList();
	}

	/**
	 * Gets a list containing all mana-holding items in a player's accessories inventory.
	 * 
	 * @return The list of items
	 */
	default List<ItemStack> getManaAccesories(PlayerEntity player) {
		return Collections.emptyList();
	}

	/**
	 * Requests mana from items in a given player's inventory.
	 * 
	 * @param manaToGet How much mana is to be requested, if less mana exists than this amount,
	 *                  the amount of mana existent will be returned instead, if you want exact values use
	 *                  {@link #requestManaExact}.
	 * @param remove    If true, the mana will be removed from the target item. Set to false to just check.
	 * @return The amount of mana received from the request.
	 */
	default int requestMana(ItemStack stack, PlayerEntity player, int manaToGet, boolean remove) {
		return 0;
	}

	/**
	 * Requests an exact amount of mana from items in a given player's inventory.
	 * 
	 * @param manaToGet How much mana is to be requested, if less mana exists than this amount,
	 *                  false will be returned instead, and nothing will happen.
	 * @param remove    If true, the mana will be removed from the target item. Set to false to just check.
	 * @return If the request was succesful.
	 */
	default boolean requestManaExact(ItemStack stack, PlayerEntity player, int manaToGet, boolean remove) {
		return false;
	}

	/**
	 * Dispatches mana to items in a given player's inventory. Note that this method
	 * does not automatically remove mana from the item which is exporting.
	 * 
	 * @param manaToSend How much mana is to be sent.
	 * @param add        If true, the mana will be added from the target item. Set to false to just check.
	 * @return The amount of mana actually sent.
	 */
	default int dispatchMana(ItemStack stack, PlayerEntity player, int manaToSend, boolean add) {
		return 0;
	}

	/**
	 * Dispatches an exact amount of mana to items in a given player's inventory. Note that this method
	 * does not automatically remove mana from the item which is exporting.
	 * 
	 * @param manaToSend How much mana is to be sent.
	 * @param add        If true, the mana will be added from the target item. Set to false to just check.
	 * @return If an item received the mana sent.
	 */
	default boolean dispatchManaExact(ItemStack stack, PlayerEntity player, int manaToSend, boolean add) {
		return false;
	}

	/**
	 * Requests mana from items in a given player's inventory. This version also
	 * checks for IManaDiscountArmor items equipped to lower the cost.
	 * 
	 * @param manaToGet How much mana is to be requested, if less mana exists than this amount,
	 *                  the amount of mana existent will be returned instead, if you want exact values use
	 *                  requestManaExact.
	 * @param remove    If true, the mana will be removed from the target item. Set to false to just check.
	 * @return The amount of mana received from the request.
	 */
	default int requestManaForTool(ItemStack stack, PlayerEntity player, int manaToGet, boolean remove) {
		return 0;
	}

	/**
	 * Requests an exact amount of mana from items in a given player's inventory. This version also
	 * checks for IManaDiscountArmor items equipped to lower the cost.
	 * 
	 * @param manaToGet How much mana is to be requested, if less mana exists than this amount,
	 *                  false will be returned instead, and nothing will happen.
	 * @param remove    If true, the mana will be removed from the target item. Set to false to just check.
	 * @return If the request was succesful.
	 */
	default boolean requestManaExactForTool(ItemStack stack, PlayerEntity player, int manaToGet, boolean remove) {
		return false;
	}

	/**
	 * Determines how many times the given tool can get the requested amount of mana. Takes discounts into
	 * consideration.
	 * 
	 * @param manaToGet How much mana is to be requested per invocation
	 * @return The number of invocations that could be executed before exhausting the player's mana available
	 */
	default int getInvocationCountForTool(ItemStack stack, PlayerEntity player, int manaToGet) {
		return 0;
	}

	/**
	 * Gets the sum of all the discounts on IManaDiscountArmor items equipped
	 * on the player passed in. This discount can vary based on what the passed tool is.
	 */
	default float getFullDiscountForTools(PlayerEntity player, ItemStack tool) {
		return 0;
	}
}
