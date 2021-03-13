/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import vazkii.botania.api.item.IRelic;
import vazkii.botania.client.core.handler.TooltipHandler;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemDice extends ItemRelic {
	public ItemDice(Properties props) {
		super(props);
	}

	public static Item[] getRelics() {
		return new Item[] {
				ModItems.infiniteFruit,
				ModItems.kingKey,
				ModItems.flugelEye,
				ModItems.thorRing,
				ModItems.odinRing,
				ModItems.lokiRing
		};
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (isRightPlayer(player, stack)) {
			if (world.isRemote) {
				return ActionResult.resultSuccess(stack);
			}

			world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));

			List<Integer> possible = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				if (!hasRelicAlready(player, i)) {
					possible.add(i);
				}
			}

			if (possible.isEmpty()) {
				player.sendMessage(new TranslationTextComponent("botaniamisc.dudDiceRoll", world.rand.nextInt(6) + 1).mergeStyle(TextFormatting.DARK_GREEN), Util.DUMMY_UUID);
				stack.shrink(1);
				return ActionResult.resultSuccess(stack);
			} else {
				int relic = possible.get(world.rand.nextInt(possible.size()));
				player.sendMessage(new TranslationTextComponent("botaniamisc.diceRoll", relic + 1).mergeStyle(TextFormatting.DARK_GREEN), Util.DUMMY_UUID);
				return ActionResult.resultSuccess(new ItemStack(getRelics()[relic]));
			}
		}

		return ActionResult.resultPass(stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flags) {
		super.addInformation(stack, world, tooltip, flags);
		tooltip.add(new StringTextComponent(""));
		TooltipHandler.addOnShift(tooltip, () -> {
			String name = stack.getTranslationKey() + ".poem";
			for (int i = 0; i < 4; i++) {
				tooltip.add(new TranslationTextComponent(name + i).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
			}
		});
	}

	@Override
	public boolean shouldDamageWrongPlayer() {
		return false;
	}

	private boolean hasRelicAlready(PlayerEntity player, int relic) {
		if (relic < 0 || relic > 6 || !(player instanceof ServerPlayerEntity)) {
			return true;
		}

		ServerPlayerEntity mpPlayer = (ServerPlayerEntity) player;
		Item item = getRelics()[relic];
		ResourceLocation advId = IRelic.registry().get(item).getAdvancement();

		if (advId != null) {
			Advancement adv = ServerLifecycleHooks.getCurrentServer().getAdvancementManager().getAdvancement(advId);
			return adv != null && mpPlayer.getAdvancements().getProgress(adv).isDone();
		}

		return false;
	}

}
