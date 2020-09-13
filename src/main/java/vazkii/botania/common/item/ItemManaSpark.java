/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.common.entity.EntityManaSpark;

import javax.annotation.Nonnull;

public class ItemManaSpark extends Item {

	public ItemManaSpark(Properties builder) {
		super(builder);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		return attachSpark(ctx.getWorld(), ctx.getPos(), ctx.getItem()) ? ActionResultType.func_233537_a_(ctx.getWorld().isRemote) : ActionResultType.PASS;
	}

	public static boolean attachSpark(World world, BlockPos pos, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof ISparkAttachable) {
			ISparkAttachable attach = (ISparkAttachable) tile;
			if (attach.canAttachSpark(stack) && attach.getAttachedSpark() == null) {
				if (!world.isRemote) {
					stack.shrink(1);
					EntityManaSpark spark = new EntityManaSpark(world);
					spark.setPosition(pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5);
					world.addEntity(spark);
					attach.attachSpark(spark);
				}
				return true;
			}
		}
		return false;
	}
}
