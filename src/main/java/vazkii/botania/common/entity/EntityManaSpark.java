/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.IManaSpark;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.api.mana.spark.SparkUpgradeType;
import vazkii.botania.common.item.ItemSparkUpgrade;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.network.PacketBotaniaEffect;
import vazkii.botania.common.network.PacketHandler;

import javax.annotation.Nonnull;

import java.util.*;
import java.util.stream.Collectors;

public class EntityManaSpark extends EntitySparkBase implements IManaSpark {
	private static final int TRANSFER_RATE = 1000;
	private static final String TAG_UPGRADE = "upgrade";
	private static final DataParameter<Integer> UPGRADE = EntityDataManager.createKey(EntityManaSpark.class, DataSerializers.VARINT);

	private final Set<IManaSpark> transfers = Collections.newSetFromMap(new WeakHashMap<>());

	private int removeTransferants = 2;

	public EntityManaSpark(EntityType<EntityManaSpark> type, World world) {
		super(type, world);
	}

	public EntityManaSpark(World world) {
		this(ModEntities.SPARK, world);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(UPGRADE, 0);
	}

	@Nonnull
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(ModItems.spark);
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isRemote) {
			return;
		}

		ISparkAttachable tile = getAttachedTile();
		if (tile == null) {
			dropAndKill();
			return;
		}

		SparkUpgradeType upgrade = getUpgrade();
		Collection<IManaSpark> transfers = getTransfers();

		switch (upgrade) {
		case DISPERSIVE: {
			List<PlayerEntity> players = SparkHelper.getEntitiesAround(PlayerEntity.class, world, getPosX(), getPosY() + (getHeight() / 2.0), getPosZ());

			Map<PlayerEntity, Map<ItemStack, Integer>> receivingPlayers = new HashMap<>();

			ItemStack input = new ItemStack(ModItems.spark);
			for (PlayerEntity player : players) {
				List<ItemStack> stacks = new ArrayList<>();
				stacks.addAll(player.inventory.mainInventory);
				stacks.addAll(player.inventory.armorInventory);

				IInventory inv = BotaniaAPI.instance().getAccessoriesInventory(player);
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					stacks.add(inv.getStackInSlot(i));
				}

				for (ItemStack stack : stacks) {
					if (stack.isEmpty() || !(stack.getItem() instanceof IManaItem)) {
						continue;
					}

					IManaItem manaItem = (IManaItem) stack.getItem();
					if (manaItem.canReceiveManaFromItem(stack, input)) {
						Map<ItemStack, Integer> receivingStacks;
						boolean add = false;
						if (!receivingPlayers.containsKey(player)) {
							add = true;
							receivingStacks = new HashMap<>();
						} else {
							receivingStacks = receivingPlayers.get(player);
						}

						int recv = Math.min(getAttachedTile().getCurrentMana(), Math.min(TRANSFER_RATE, manaItem.getMaxMana(stack) - manaItem.getMana(stack)));
						if (recv > 0) {
							receivingStacks.put(stack, recv);
							if (add) {
								receivingPlayers.put(player, receivingStacks);
							}
						}
					}
				}
			}

			if (!receivingPlayers.isEmpty()) {
				List<PlayerEntity> keys = new ArrayList<>(receivingPlayers.keySet());
				Collections.shuffle(keys);
				PlayerEntity player = keys.iterator().next();

				Map<ItemStack, Integer> items = receivingPlayers.get(player);
				ItemStack stack = items.keySet().iterator().next();
				int cost = items.get(stack);
				int manaToPut = Math.min(getAttachedTile().getCurrentMana(), cost);
				((IManaItem) stack.getItem()).addMana(stack, manaToPut);
				getAttachedTile().receiveMana(-manaToPut);
				particlesTowards(player);
			}

			break;
		}
		case DOMINANT: {
			List<IManaSpark> validSparks = SparkHelper.getSparksAround(world, getPosX(), getPosY() + (getHeight() / 2), getPosZ(), getNetwork())
					.filter(s -> {
						SparkUpgradeType otherUpgrade = s.getUpgrade();
						return s != this && otherUpgrade == SparkUpgradeType.NONE && s.getAttachedTile() instanceof IManaPool;
					})
					.collect(Collectors.toList());
			if (validSparks.size() > 0) {
				validSparks.get(world.rand.nextInt(validSparks.size())).registerTransfer(this);
			}

			break;
		}
		case RECESSIVE: {
			SparkHelper.getSparksAround(world, getPosX(), getPosY() + (getHeight() / 2), getPosZ(), getNetwork())
					.filter(s -> {
						SparkUpgradeType otherUpgrade = s.getUpgrade();
						return s != this
								&& otherUpgrade != SparkUpgradeType.DOMINANT
								&& otherUpgrade != SparkUpgradeType.RECESSIVE
								&& otherUpgrade != SparkUpgradeType.ISOLATED;
					})
					.forEach(transfers::add);
			break;
		}
		case NONE:
		default:
			break;
		}

		if (!transfers.isEmpty()) {
			int manaTotal = Math.min(TRANSFER_RATE * transfers.size(), tile.getCurrentMana());
			int count = transfers.size();
			int manaSpent = 0;

			if (manaTotal > 0) {
				for (IManaSpark spark : transfers) {
					count--;
					if (spark.getAttachedTile() == null || spark.getAttachedTile().isFull() || spark.areIncomingTransfersDone()) {
						continue;
					}

					ISparkAttachable attached = spark.getAttachedTile();
					int spend = Math.min(attached.getAvailableSpaceForMana(), (manaTotal - manaSpent) / (count + 1));
					attached.receiveMana(spend);
					manaSpent += spend;

					particlesTowards(spark.entity());
				}
				tile.receiveMana(-manaSpent);
			}
		}

		if (removeTransferants > 0) {
			removeTransferants--;
		}
		filterTransfers();
	}

	private void particlesTowards(Entity e) {
		PacketHandler.sendToNearby(world, this,
				new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.SPARK_MANA_FLOW, getPosX(), getPosY(), getPosZ(),
						getEntityId(), e.getEntityId()));
	}

	public static void particleBeam(PlayerEntity player, Entity e1, Entity e2) {
		if (e1 != null && e2 != null && !e1.world.isRemote) {
			PacketHandler.sendTo((ServerPlayerEntity) player,
					new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.SPARK_NET_INDICATOR, e1.getPosX(), e1.getPosY(), e1.getPosZ(),
							e1.getEntityId(), e2.getEntityId()));
		}
	}

	private void dropAndKill() {
		SparkUpgradeType upgrade = getUpgrade();
		entityDropItem(new ItemStack(ModItems.spark), 0F);
		if (upgrade != SparkUpgradeType.NONE) {
			entityDropItem(ItemSparkUpgrade.getByType(upgrade), 0F);
		}
		remove();
	}

	@Override
	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (isAlive() && !stack.isEmpty()) {
			SparkUpgradeType upgrade = getUpgrade();
			if (stack.getItem() == ModItems.twigWand) {
				if (!world.isRemote) {
					if (player.isSneaking()) {
						if (upgrade != SparkUpgradeType.NONE) {
							entityDropItem(ItemSparkUpgrade.getByType(upgrade), 0F);
							setUpgrade(SparkUpgradeType.NONE);

							transfers.clear();
							removeTransferants = 2;
						} else {
							dropAndKill();
						}
					} else {
						SparkHelper.getSparksAround(world, getPosX(), getPosY() + (getHeight() / 2), getPosZ(), getNetwork())
								.forEach(s -> particleBeam(player, this, s.entity()));
					}
				}

				return ActionResultType.func_233537_a_(world.isRemote);
			} else if (stack.getItem() instanceof ItemSparkUpgrade && upgrade == SparkUpgradeType.NONE) {
				if (!world.isRemote) {
					setUpgrade(((ItemSparkUpgrade) stack.getItem()).type);
					stack.shrink(1);
				}
				return ActionResultType.func_233537_a_(world.isRemote);
			} else if (stack.getItem() == ModItems.phantomInk) {
				if (!world.isRemote) {
					setInvisible(true);
				}
				return ActionResultType.func_233537_a_(world.isRemote);
			} else if (stack.getItem() instanceof DyeItem) {
				DyeColor color = ((DyeItem) stack.getItem()).getDyeColor();
				if (color != getNetwork()) {
					if (!world.isRemote) {
						setNetwork(color);
						stack.shrink(1);
					}
					return ActionResultType.func_233537_a_(world.isRemote);
				}
			}
		}

		return ActionResultType.PASS;
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void readAdditional(@Nonnull CompoundNBT cmp) {
		super.readAdditional(cmp);
		setUpgrade(SparkUpgradeType.values()[cmp.getInt(TAG_UPGRADE)]);
	}

	@Override
	protected void writeAdditional(@Nonnull CompoundNBT cmp) {
		super.writeAdditional(cmp);
		cmp.putInt(TAG_UPGRADE, getUpgrade().ordinal());
	}

	@Override
	public ISparkAttachable getAttachedTile() {
		TileEntity tile = world.getTileEntity(getAttachPos());
		if (tile instanceof ISparkAttachable) {
			return (ISparkAttachable) tile;
		}

		return null;
	}

	private void filterTransfers() {
		Iterator<IManaSpark> iter = transfers.iterator();
		while (iter.hasNext()) {
			IManaSpark spark = iter.next();
			SparkUpgradeType upgr = getUpgrade();
			SparkUpgradeType supgr = spark.getUpgrade();
			ISparkAttachable atile = spark.getAttachedTile();

			if (spark == this
					|| spark.areIncomingTransfersDone()
					|| getNetwork() != spark.getNetwork()
					|| atile == null
					|| atile.isFull()
					|| !(upgr == SparkUpgradeType.NONE && supgr == SparkUpgradeType.DOMINANT
							|| upgr == SparkUpgradeType.RECESSIVE && (supgr == SparkUpgradeType.NONE || supgr == SparkUpgradeType.DISPERSIVE)
							|| !(atile instanceof IManaPool))) {
				iter.remove();
			}
		}
	}

	@Override
	public Collection<IManaSpark> getTransfers() {
		filterTransfers();
		return transfers;
	}

	private boolean hasTransfer(IManaSpark entity) {
		return transfers.contains(entity);
	}

	@Override
	public void registerTransfer(IManaSpark entity) {
		if (hasTransfer(entity)) {
			return;
		}
		transfers.add(entity);
	}

	@Override
	public SparkUpgradeType getUpgrade() {
		return SparkUpgradeType.values()[dataManager.get(UPGRADE)];
	}

	@Override
	public void setUpgrade(SparkUpgradeType upgrade) {
		dataManager.set(UPGRADE, upgrade.ordinal());
	}

	@Override
	public boolean areIncomingTransfersDone() {
		ISparkAttachable tile = getAttachedTile();
		if (tile instanceof IManaPool) {
			return removeTransferants > 0;
		}
		return tile != null && tile.areIncomingTranfersDone();
	}

}
