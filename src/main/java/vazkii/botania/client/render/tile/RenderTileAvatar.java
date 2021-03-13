/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import vazkii.botania.api.item.IAvatarWieldable;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.client.model.ModelAvatar;
import vazkii.botania.common.block.tile.TileAvatar;

import javax.annotation.Nullable;

public class RenderTileAvatar extends TileEntityRenderer<TileAvatar> {

	private static final float[] ROTATIONS = new float[] {
			180F, 0F, 90F, 270F
	};

	private static final ResourceLocation texture = new ResourceLocation(LibResources.MODEL_AVATAR);
	private static final ModelAvatar model = new ModelAvatar();

	public RenderTileAvatar(TileEntityRendererDispatcher manager) {
		super(manager);
	}

	@Override
	public void render(@Nullable TileAvatar avatar, float pticks, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
		ms.push();
		Direction facing = avatar != null && avatar.getWorld() != null ? avatar.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING) : Direction.SOUTH;

		ms.translate(0.5F, 1.6F, 0.5F);
		ms.scale(1F, -1F, -1F);
		ms.rotate(Vector3f.YP.rotationDegrees(ROTATIONS[Math.max(Math.min(ROTATIONS.length - 1, facing.getIndex() - 2), 0)]));
		IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
		model.render(ms, buffer, light, overlay, 1, 1, 1, 1);

		if (avatar != null) {
			ItemStack stack = avatar.getItemHandler().getStackInSlot(0);
			if (!stack.isEmpty()) {
				ms.push();
				float s = 0.6F;
				ms.scale(s, s, s);
				ms.translate(-0.5F, 2F, -0.25F);
				ms.rotate(Vector3f.XP.rotationDegrees(-70));
				Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, light, overlay, ms, buffers);
				ms.pop();

				IAvatarWieldable wieldable = IAvatarWieldable.registry().get(stack.getItem());
				if (wieldable != null) {
					buffer = buffers.getBuffer(RenderType.getEntityTranslucent(wieldable.getOverlayResource(avatar, stack)));
					s = 1.01F;

					ms.push();
					ms.scale(s, s, s);
					ms.translate(0F, -0.01F, 0F);
					float alpha = (float) Math.sin(ClientTickHandler.ticksInGame / 20D) / 2F + 0.5F;
					model.render(ms, buffer, 0xF000F0, overlay, 1, 1, 1, alpha + 0.183F);
					ms.pop();
				}
			}
		}
		ms.pop();
	}

}
