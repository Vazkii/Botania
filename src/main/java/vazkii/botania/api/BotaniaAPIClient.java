/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.logging.log4j.LogManager;

import vazkii.botania.api.item.IFloatingFlower;

import java.util.Collections;
import java.util.Map;

/**
 * Class for API calls that must be made clientside
 */
@OnlyIn(Dist.CLIENT)
public interface BotaniaAPIClient {
	LazyValue<BotaniaAPIClient> INSTANCE = new LazyValue<>(() -> {
		try {
			return (BotaniaAPIClient) Class.forName("vazkii.botania.client.impl.BotaniaAPIClientImpl").getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			LogManager.getLogger().warn("Unable to find BotaniaAPIClientImpl, using a dummy");
			return new BotaniaAPIClient() {};
		}
	});

	static BotaniaAPIClient instance() {
		return INSTANCE.getValue();
	}

	/**
	 * Registers your model for island type islandType here.
	 * Call this during {@link net.minecraftforge.client.event.ModelRegistryEvent}.
	 *
	 * @param islandType The islandtype to register
	 * @param model      The model, only {@link ResourceLocation} allowed, no {@link ModelResourceLocation} allowed.
	 */
	default void registerIslandTypeModel(IFloatingFlower.IslandType islandType, ResourceLocation model) {}

	/**
	 * @return An immutable and live view of the registered island type model map
	 */
	default Map<IFloatingFlower.IslandType, ResourceLocation> getRegisteredIslandTypeModels() {
		return Collections.emptyMap();
	}

	/**
	 * Draw a mana bar on the screen
	 */
	default void drawSimpleManaHUD(MatrixStack ms, int color, int mana, int maxMana, String name) {}

	/**
	 * Performs the effects of {@link #drawSimpleManaHUD}, then renders {@code bindDisplay}, and a checkmark or x-mark
	 * dependong on the value of {@code properlyBound}.
	 */
	default void drawComplexManaHUD(MatrixStack ms, int color, int mana, int maxMana, String name, ItemStack bindDisplay, boolean properlyBound) {}
}
