/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile.mana;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.item.IManaDissolvable;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.mana.BlockPool;
import vazkii.botania.common.block.tile.ModTiles;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.core.handler.ManaNetworkHandler;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.ItemManaTablet;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.mixin.AccessorItemEntity;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TilePool extends TileMod implements IManaPool, IKeyLocked, ISparkAttachable, IThrottledPacket, ITickableTileEntity {
	public static final int PARTICLE_COLOR = 0x00C6FF;
	public static final int MAX_MANA = 1000000;
	private static final int MAX_MANA_DILLUTED = 10000;

	private static final String TAG_MANA = "mana";
	private static final String TAG_OUTPUTTING = "outputting";
	private static final String TAG_COLOR = "color";
	private static final String TAG_MANA_CAP = "manaCap";
	private static final String TAG_CAN_ACCEPT = "canAccept";
	private static final String TAG_CAN_SPARE = "canSpare";
	private static final String TAG_FRAGILE = "fragile";
	private static final String TAG_INPUT_KEY = "inputKey";
	private static final String TAG_OUTPUT_KEY = "outputKey";
	private static final int CRAFT_EFFECT_EVENT = 0;
	private static final int CHARGE_EFFECT_EVENT = 1;

	private boolean outputting = false;

	public DyeColor color = DyeColor.WHITE;
	private int mana;

	public int manaCap = -1;
	private int soundTicks = 0;
	private boolean canAccept = true;
	private boolean canSpare = true;
	public boolean fragile = false;
	boolean isDoingTransfer = false;
	int ticksDoingTransfer = 0;

	private String inputKey = "";
	private final String outputKey = "";

	private int ticks = 0;
	private boolean sendPacket = false;

	public TilePool() {
		super(ModTiles.POOL);
	}

	@Override
	public boolean isFull() {
		Block blockBelow = world.getBlockState(pos.down()).getBlock();
		return blockBelow != ModBlocks.manaVoid && getCurrentMana() >= manaCap;
	}

	@Override
	public void receiveMana(int mana) {
		int old = this.mana;
		this.mana = Math.max(0, Math.min(getCurrentMana() + mana, manaCap));
		if (old != this.mana) {
			markDirty();
			markDispatchable();
		}
	}

	@Override
	public void remove() {
		super.remove();
		ManaNetworkEvent.removePool(this);
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		ManaNetworkEvent.removePool(this);
	}

	public static int calculateComparatorLevel(int mana, int max) {
		int val = (int) ((double) mana / (double) max * 15.0);
		if (mana > 0) {
			val = Math.max(val, 1);
		}
		return val;
	}

	public static List<IManaInfusionRecipe> manaInfusionRecipes(World world) {
		return ModRecipeTypes.getRecipes(world, ModRecipeTypes.MANA_INFUSION_TYPE).values().stream()
				.filter(r -> r instanceof IManaInfusionRecipe)
				.map(r -> (IManaInfusionRecipe) r)
				.collect(Collectors.toList());
	}

	public IManaInfusionRecipe getMatchingRecipe(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		List<IManaInfusionRecipe> matchingNonCatRecipes = new ArrayList<>();
		List<IManaInfusionRecipe> matchingCatRecipes = new ArrayList<>();

		for (IManaInfusionRecipe recipe : manaInfusionRecipes(world)) {
			if (recipe.matches(stack)) {
				if (recipe.getCatalyst() == null) {
					matchingNonCatRecipes.add(recipe);
				} else if (recipe.getCatalyst() == state) {
					matchingCatRecipes.add(recipe);
				}
			}
		}

		// Recipes with matching catalyst take priority above recipes with no catalyst specified
		return !matchingCatRecipes.isEmpty() ? matchingCatRecipes.get(0) : !matchingNonCatRecipes.isEmpty() ? matchingNonCatRecipes.get(0) : null;
	}

	public boolean collideEntityItem(ItemEntity item) {
		if (world.isRemote || !item.isAlive() || item.getItem().isEmpty()) {
			return false;
		}

		ItemStack stack = item.getItem();

		IManaDissolvable dissolvable = IManaDissolvable.registry().get(stack.getItem());
		if (dissolvable != null) {
			dissolvable.onDissolveTick(this, stack, item);
		}

		int age = ((AccessorItemEntity) item).getAge();
		if (age > 100 && age < 130) {
			return false;
		}

		IManaInfusionRecipe recipe = getMatchingRecipe(stack, world.getBlockState(pos.down()));

		if (recipe != null) {
			int mana = recipe.getManaToConsume();
			if (getCurrentMana() >= mana) {
				receiveMana(-mana);

				stack.shrink(1);
				item.setOnGround(false); //Force entity collision update to run every tick if crafting is in progress

				ItemStack output = recipe.getRecipeOutput().copy();
				ItemEntity outputItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, output);
				((AccessorItemEntity) outputItem).setAge(105);
				world.addEntity(outputItem);

				craftingFanciness();
				return true;
			}
		}

		return false;
	}

	private void craftingFanciness() {
		if (soundTicks == 0) {
			world.playSound(null, pos, ModSounds.manaPoolCraft, SoundCategory.BLOCKS, 0.4F, 4F);
			soundTicks = 6;
		}

		world.addBlockEvent(getPos(), getBlockState().getBlock(), CRAFT_EFFECT_EVENT, 0);
	}

	@Override
	public boolean receiveClientEvent(int event, int param) {
		switch (event) {
		case CRAFT_EFFECT_EVENT: {
			if (world.isRemote) {
				for (int i = 0; i < 25; i++) {
					float red = (float) Math.random();
					float green = (float) Math.random();
					float blue = (float) Math.random();
					SparkleParticleData data = SparkleParticleData.sparkle((float) Math.random(), red, green, blue, 10);
					world.addParticle(data, pos.getX() + 0.5 + Math.random() * 0.4 - 0.2, pos.getY() + 0.75, pos.getZ() + 0.5 + Math.random() * 0.4 - 0.2, 0, 0, 0);
				}
			}

			return true;
		}
		case CHARGE_EFFECT_EVENT: {
			if (world.isRemote) {
				if (ConfigHandler.COMMON.chargingAnimationEnabled.get()) {
					boolean outputting = param == 1;
					Vector3 itemVec = Vector3.fromBlockPos(pos).add(0.5, 0.5 + Math.random() * 0.3, 0.5);
					Vector3 tileVec = Vector3.fromBlockPos(pos).add(0.2 + Math.random() * 0.6, 0, 0.2 + Math.random() * 0.6);
					Botania.proxy.lightningFX(outputting ? tileVec : itemVec,
							outputting ? itemVec : tileVec, 80, world.rand.nextLong(), 0x4400799c, 0x4400C6FF);
				}
			}
			return true;
		}
		default:
			return super.receiveClientEvent(event, param);
		}
	}

	@Override
	public void tick() {
		if (manaCap == -1) {
			manaCap = ((BlockPool) getBlockState().getBlock()).variant == BlockPool.Variant.DILUTED ? MAX_MANA_DILLUTED : MAX_MANA;
		}

		if (!ManaNetworkHandler.instance.isPoolIn(this) && !isRemoved()) {
			ManaNetworkEvent.addPool(this);
		}

		if (world.isRemote) {
			double particleChance = 1F - (double) getCurrentMana() / (double) manaCap * 0.1;
			if (Math.random() > particleChance) {
				float red = (PARTICLE_COLOR >> 16 & 0xFF) / 255F;
				float green = (PARTICLE_COLOR >> 8 & 0xFF) / 255F;
				float blue = (PARTICLE_COLOR & 0xFF) / 255F;
				WispParticleData data = WispParticleData.wisp((float) Math.random() / 3F, red, green, blue, 2F);
				world.addParticle(data, pos.getX() + 0.3 + Math.random() * 0.5, pos.getY() + 0.6 + Math.random() * 0.25, pos.getZ() + Math.random(), 0, (float) Math.random() / 25F, 0);
			}
			return;
		}

		boolean wasDoingTransfer = isDoingTransfer;
		isDoingTransfer = false;

		if (soundTicks > 0) {
			soundTicks--;
		}

		if (sendPacket && ticks % 10 == 0) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			sendPacket = false;
		}

		List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
		for (ItemEntity item : items) {
			if (!item.isAlive()) {
				continue;
			}

			ItemStack stack = item.getItem();
			if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
				IManaItem mana = (IManaItem) stack.getItem();
				if (outputting && mana.canReceiveManaFromPool(stack, this) || !outputting && mana.canExportManaToPool(stack, this)) {
					boolean didSomething = false;

					int bellowCount = 0;
					if (outputting) {
						for (Direction dir : Direction.Plane.HORIZONTAL) {
							TileEntity tile = world.getTileEntity(pos.offset(dir));
							if (tile instanceof TileBellows && ((TileBellows) tile).getLinkedTile() == this) {
								bellowCount++;
							}
						}
					}
					int transfRate = 1000 * (bellowCount + 1);

					if (outputting) {
						if (canSpare) {
							if (getCurrentMana() > 0 && mana.getMana(stack) < mana.getMaxMana(stack)) {
								didSomething = true;
							}

							int manaVal = Math.min(transfRate, Math.min(getCurrentMana(), mana.getMaxMana(stack) - mana.getMana(stack)));
							mana.addMana(stack, manaVal);
							receiveMana(-manaVal);
						}
					} else {
						if (canAccept) {
							if (mana.getMana(stack) > 0 && !isFull()) {
								didSomething = true;
							}

							int manaVal = Math.min(transfRate, Math.min(manaCap - getCurrentMana(), mana.getMana(stack)));
							mana.addMana(stack, -manaVal);
							receiveMana(manaVal);
						}
					}

					if (didSomething) {
						if (ConfigHandler.COMMON.chargingAnimationEnabled.get() && world.rand.nextInt(20) == 0) {
							world.addBlockEvent(getPos(), getBlockState().getBlock(), CHARGE_EFFECT_EVENT, outputting ? 1 : 0);
						}
						isDoingTransfer = outputting;
					}
				}
			}
		}

		if (isDoingTransfer) {
			ticksDoingTransfer++;
		} else {
			ticksDoingTransfer = 0;
			if (wasDoingTransfer) {
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			}
		}

		ticks++;
	}

	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		cmp.putInt(TAG_MANA, mana);
		cmp.putBoolean(TAG_OUTPUTTING, outputting);
		cmp.putInt(TAG_COLOR, color.getId());

		cmp.putInt(TAG_MANA_CAP, manaCap);
		cmp.putBoolean(TAG_CAN_ACCEPT, canAccept);
		cmp.putBoolean(TAG_CAN_SPARE, canSpare);
		cmp.putBoolean(TAG_FRAGILE, fragile);

		cmp.putString(TAG_INPUT_KEY, inputKey);
		cmp.putString(TAG_OUTPUT_KEY, outputKey);
	}

	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		mana = cmp.getInt(TAG_MANA);
		outputting = cmp.getBoolean(TAG_OUTPUTTING);
		color = DyeColor.byId(cmp.getInt(TAG_COLOR));

		if (cmp.contains(TAG_MANA_CAP)) {
			manaCap = cmp.getInt(TAG_MANA_CAP);
		}
		if (cmp.contains(TAG_CAN_ACCEPT)) {
			canAccept = cmp.getBoolean(TAG_CAN_ACCEPT);
		}
		if (cmp.contains(TAG_CAN_SPARE)) {
			canSpare = cmp.getBoolean(TAG_CAN_SPARE);
		}
		fragile = cmp.getBoolean(TAG_FRAGILE);

		if (cmp.contains(TAG_INPUT_KEY)) {
			inputKey = cmp.getString(TAG_INPUT_KEY);
		}
		if (cmp.contains(TAG_OUTPUT_KEY)) {
			inputKey = cmp.getString(TAG_OUTPUT_KEY);
		}

	}

	public void onWanded(PlayerEntity player) {
		if (player == null || player.isSneaking()) {
			outputting = !outputting;
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void renderHUD(MatrixStack ms, Minecraft mc) {
		ItemStack pool = new ItemStack(getBlockState().getBlock());
		String name = pool.getDisplayName().getString();
		int color = 0x4444FF;
		BotaniaAPIClient.instance().drawSimpleManaHUD(ms, color, getCurrentMana(), manaCap, name);

		int x = Minecraft.getInstance().getMainWindow().getScaledWidth() / 2 - 11;
		int y = Minecraft.getInstance().getMainWindow().getScaledHeight() / 2 + 30;

		int u = outputting ? 22 : 0;
		int v = 38;

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		mc.textureManager.bindTexture(HUDHandler.manaBar);
		RenderHelper.drawTexturedModalRect(ms, x, y, u, v, 22, 15);
		RenderSystem.color4f(1F, 1F, 1F, 1F);

		ItemStack tablet = new ItemStack(ModItems.manaTablet);
		ItemManaTablet.setStackCreative(tablet);

		mc.getItemRenderer().renderItemAndEffectIntoGUI(tablet, x - 20, y);
		mc.getItemRenderer().renderItemAndEffectIntoGUI(pool, x + 26, y);

		RenderSystem.disableLighting();
		RenderSystem.disableBlend();
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return true;
	}

	@Override
	public boolean isOutputtingPower() {
		return outputting;
	}

	@Override
	public int getCurrentMana() {
		if (getBlockState().getBlock() instanceof BlockPool) {
			return ((BlockPool) getBlockState().getBlock()).variant == BlockPool.Variant.CREATIVE ? MAX_MANA : mana;
		}
		return 0;
	}

	@Override
	public String getInputKey() {
		return inputKey;
	}

	@Override
	public String getOutputKey() {
		return outputKey;
	}

	@Override
	public boolean canAttachSpark(ItemStack stack) {
		return true;
	}

	@Override
	public void attachSpark(ISparkEntity entity) {}

	@Override
	public ISparkEntity getAttachedSpark() {
		List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
		if (sparks.size() == 1) {
			Entity e = sparks.get(0);
			return (ISparkEntity) e;
		}

		return null;
	}

	@Override
	public boolean areIncomingTranfersDone() {
		return false;
	}

	@Override
	public int getAvailableSpaceForMana() {
		int space = Math.max(0, manaCap - getCurrentMana());
		if (space > 0) {
			return space;
		} else if (world.getBlockState(pos.down()).getBlock() == ModBlocks.manaVoid) {
			return manaCap;
		} else {
			return 0;
		}
	}

	@Override
	public DyeColor getColor() {
		return color;
	}

	@Override
	public void setColor(DyeColor color) {
		this.color = color;
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}

	@Override
	public void markDispatchable() {
		sendPacket = true;
	}
}
