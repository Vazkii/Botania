/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.common.entity.EntityManaSpark;

public class RenderManaSpark extends RenderSparkBase<EntityManaSpark> {

	public RenderManaSpark(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public TextureAtlasSprite getSpinningIcon(EntityManaSpark entity) {
		int upgrade = entity.getUpgrade().ordinal() - 1;
		return upgrade >= 0 && upgrade < MiscellaneousIcons.INSTANCE.sparkUpgradeIcons.length
				? MiscellaneousIcons.INSTANCE.sparkUpgradeIcons[upgrade].getSprite()
				: null;
	}

}
