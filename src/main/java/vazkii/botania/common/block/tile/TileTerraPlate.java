/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.IManaSpark;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.api.recipe.ITerraPlateRecipe;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.network.PacketBotaniaEffect;
import vazkii.botania.common.network.PacketHandler;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileTerraPlate extends TileMod implements ISparkAttachable, ITickableTileEntity {
	public static final LazyValue<IMultiblock> MULTIBLOCK = new LazyValue<>(() -> PatchouliAPI.get().makeMultiblock(
			new String[][] {
					{
							"___",
							"_P_",
							"___"
					},
					{
							"RLR",
							"L0L",
							"RLR"
					}
			},
			'P', ModBlocks.terraPlate,
			'R', "#botania:terra_plate_base",
			'0', "#botania:terra_plate_base",
			'L', "#forge:storage_blocks/lapis"
	));

	private static final String TAG_MANA = "mana";

	private int mana;

	public TileTerraPlate() {
		super(ModTiles.TERRA_PLATE);
	}

	@Override
	public void tick() {
		if (world.isRemote) {
			return;
		}

		boolean removeMana = true;

		if (hasValidPlatform()) {
			List<ItemStack> items = getItems();
			Inventory inv = new Inventory(items.toArray(new ItemStack[0]));

			ITerraPlateRecipe recipe = getCurrentRecipe(inv);
			if (recipe != null) {
				removeMana = false;
				IManaSpark spark = getAttachedSpark();
				if (spark != null) {
					SparkHelper.getSparksAround(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spark.getNetwork())
							.filter(otherSpark -> spark != otherSpark && otherSpark.getAttachedTile() instanceof IManaPool)
							.forEach(os -> os.registerTransfer(spark));
				}
				if (mana > 0) {
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
					int proportion = Float.floatToIntBits(getCompletion());
					PacketHandler.sendToNearby(world, getPos(),
							new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.TERRA_PLATE, getPos().getX(), getPos().getY(), getPos().getZ(), proportion));
				}

				if (mana >= recipe.getMana()) {
					for (ItemStack item : items) {
						item.shrink(1);
					}
					ItemEntity item = new ItemEntity(world, getPos().getX() + 0.5, getPos().getY() + 0.2, getPos().getZ() + 0.5, recipe.getCraftingResult(inv));
					item.setMotion(Vector3d.ZERO);
					world.addEntity(item);
					world.playSound(null, item.getPosX(), item.getPosY(), item.getPosZ(), ModSounds.terrasteelCraft, SoundCategory.BLOCKS, 1, 1);
					mana = 0;
					world.updateComparatorOutputLevel(pos, getBlockState().getBlock());
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
				}
			}
		}

		if (removeMana) {
			receiveMana(-1000);
		}
	}

	private List<ItemStack> getItems() {
		List<ItemEntity> itemEntities = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)), EntityPredicates.IS_ALIVE);
		List<ItemStack> stacks = new ArrayList<>();
		for (ItemEntity entity : itemEntities) {
			if (!entity.getItem().isEmpty()) {
				stacks.add(entity.getItem());
			}
		}
		return stacks;
	}

	private Inventory getInventory() {
		List<ItemStack> items = getItems();
		return new Inventory(items.toArray(new ItemStack[0]));
	}

	@Nullable
	private ITerraPlateRecipe getCurrentRecipe(Inventory items) {
		return world.getRecipeManager().getRecipe(ModRecipeTypes.TERRA_PLATE_TYPE, items, world).orElse(null);
	}

	private boolean isActive() {
		return getCurrentRecipe(getInventory()) != null;
	}

	private boolean hasValidPlatform() {
		return MULTIBLOCK.getValue().validate(world, getPos().down()) != null;
	}

	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		cmp.putInt(TAG_MANA, mana);
	}

	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		mana = cmp.getInt(TAG_MANA);
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public boolean isFull() {
		ITerraPlateRecipe recipe = getCurrentRecipe(getInventory());
		return recipe == null || getCurrentMana() >= recipe.getMana();
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.max(0, this.mana + mana);
		world.updateComparatorOutputLevel(pos, getBlockState().getBlock());
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return isActive();
	}

	@Override
	public boolean canAttachSpark(ItemStack stack) {
		return true;
	}

	@Override
	public IManaSpark getAttachedSpark() {
		List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(IManaSpark.class));
		if (sparks.size() == 1) {
			Entity e = sparks.get(0);
			return (IManaSpark) e;
		}

		return null;
	}

	@Override
	public boolean areIncomingTranfersDone() {
		return !isActive();
	}

	@Override
	public int getAvailableSpaceForMana() {
		ITerraPlateRecipe recipe = getCurrentRecipe(getInventory());
		return recipe == null ? 0 : Math.max(0, recipe.getMana() - getCurrentMana());
	}

	public float getCompletion() {
		ITerraPlateRecipe recipe = getCurrentRecipe(getInventory());
		if (recipe == null) {
			return 0;
		}
		return ((float) getCurrentMana()) / recipe.getMana();
	}

	public int getComparatorLevel() {
		int val = (int) (getCompletion() * 15.0);
		if (getCurrentMana() > 0) {
			val = Math.max(val, 1);
		}
		return val;
	}

}
