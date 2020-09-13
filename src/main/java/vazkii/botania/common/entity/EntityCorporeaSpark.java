/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.impl.corporea.DummyCorporeaNode;
import vazkii.botania.common.integration.corporea.CorporeaNodeDetectors;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class EntityCorporeaSpark extends EntitySparkBase implements ICorporeaSpark {
	private static final int SCAN_RANGE = 8;

	private static final String TAG_MASTER = "master";

	private static final DataParameter<Boolean> MASTER = EntityDataManager.createKey(EntityCorporeaSpark.class, DataSerializers.BOOLEAN);

	private ICorporeaSpark master;
	private List<ICorporeaSpark> connections = new ArrayList<>();
	private List<ICorporeaSpark> relatives = new ArrayList<>();
	private boolean firstTick = true;

	public EntityCorporeaSpark(EntityType<EntityCorporeaSpark> type, World world) {
		super(type, world);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(MASTER, false);
	}

	@Nonnull
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return isMaster() ? new ItemStack(ModItems.corporeaSparkMaster) : new ItemStack(ModItems.corporeaSpark);
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isRemote) {
			return;
		}

		ICorporeaNode node = getSparkNode();
		if (node instanceof DummyCorporeaNode && !world.getBlockState(getAttachPos()).isIn(ModTags.Blocks.CORPOREA_SPARK_OVERRIDE)) {
			dropAndKill();
			return;
		}

		if (isMaster()) {
			master = this;
		}

		if (firstTick) {
			if (isMaster()) {
				restartNetwork();
			} else {
				findNetwork();
			}

			firstTick = false;
		}

		if (master != null && (master.entity().removed || master.getNetwork() != getNetwork())) {
			master = null;
		}
	}

	private void dropAndKill() {
		entityDropItem(new ItemStack(isMaster() ? ModItems.corporeaSparkMaster : ModItems.corporeaSpark), 0F);
		remove();
	}

	@Override
	public void remove() {
		super.remove();
		connections.remove(this);
		restartNetwork();
	}

	@Override
	public void registerConnections(ICorporeaSpark master, ICorporeaSpark referrer, List<ICorporeaSpark> connections) {
		relatives.clear();
		for (ICorporeaSpark spark : getNearbySparks()) {
			if (spark == null || connections.contains(spark) || spark.getNetwork() != getNetwork() || spark.isMaster() || spark.entity().removed) {
				continue;
			}

			connections.add(spark);
			relatives.add(spark);
			spark.registerConnections(master, this, connections);
		}

		this.master = master;
		this.connections = connections;
	}

	@SuppressWarnings("unchecked")
	private List<ICorporeaSpark> getNearbySparks() {
		return (List) world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPosX() - SCAN_RANGE, getPosY() - SCAN_RANGE, getPosZ() - SCAN_RANGE, getPosX() + SCAN_RANGE, getPosY() + SCAN_RANGE, getPosZ() + SCAN_RANGE), Predicates.instanceOf(ICorporeaSpark.class));
	}

	private void restartNetwork() {
		connections = new ArrayList<>();
		relatives = new ArrayList<>();

		if (master != null) {
			ICorporeaSpark oldMaster = master;
			master = null;

			oldMaster.registerConnections(oldMaster, this, new ArrayList<>());
		}
	}

	private void findNetwork() {
		for (ICorporeaSpark spark : getNearbySparks()) {
			if (spark.getNetwork() == getNetwork() && !spark.entity().removed) {
				ICorporeaSpark master = spark.getMaster();
				if (master != null) {
					this.master = master;
					restartNetwork();

					break;
				}
			}
		}
	}

	private static void displayRelatives(PlayerEntity player, List<ICorporeaSpark> checked, ICorporeaSpark spark) {
		if (spark == null) {
			return;
		}

		List<ICorporeaSpark> sparks = spark.getRelatives();
		if (sparks.isEmpty()) {
			EntityManaSpark.particleBeam(player, spark.entity(), spark.getMaster().entity());
		} else {
			for (ICorporeaSpark endSpark : sparks) {
				if (!checked.contains(endSpark)) {
					EntityManaSpark.particleBeam(player, spark.entity(), endSpark.entity());
					checked.add(endSpark);
					displayRelatives(player, checked, endSpark);
				}
			}
		}
	}

	@Override
	public ICorporeaNode getSparkNode() {
		return CorporeaNodeDetectors.findNode(world, this);
	}

	@Override
	public List<ICorporeaSpark> getConnections() {
		return connections;
	}

	@Override
	public List<ICorporeaSpark> getRelatives() {
		return relatives;
	}

	@Override
	public void onItemExtracted(ItemStack stack) {
		((ServerWorld) world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, stack), getPosX(), getPosY(), getPosZ(), 10, 0.125, 0.125, 0.125, 0.05);
	}

	@Override
	public void onItemsRequested(List<ItemStack> stacks) {
		List<Item> shownItems = new ArrayList<>();
		for (ItemStack stack : stacks) {
			if (!shownItems.contains(stack.getItem())) {
				shownItems.add(stack.getItem());
				((ServerWorld) world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, stack), getPosX(), getPosY(), getPosZ(), 10, 0.125, 0.125, 0.125, 0.05);
			}
		}
	}

	@Override
	public ICorporeaSpark getMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		dataManager.set(MASTER, master);
	}

	@Override
	public boolean isMaster() {
		return dataManager.get(MASTER);
	}

	@Override
	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!removed && !stack.isEmpty()) {
			if (stack.getItem() == ModItems.twigWand) {
				if (!world.isRemote) {
					if (player.isSneaking()) {
						dropAndKill();
						if (isMaster()) {
							restartNetwork();
						}
					} else {
						displayRelatives(player, new ArrayList<>(), master);
					}
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
			} else if (stack.getItem() == ModItems.phantomInk) {
				if (!world.isRemote) {
					setInvisible(true);
				}
				return ActionResultType.func_233537_a_(world.isRemote);
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public void setNetwork(DyeColor color) {
		if (color == getNetwork()) {
			return;
		}

		super.setNetwork(color);

		// Do not access world during deserialization
		if (!firstTick) {
			if (isMaster()) {
				restartNetwork();
			} else {
				findNetwork();
			}
		}
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void readAdditional(@Nonnull CompoundNBT cmp) {
		super.readAdditional(cmp);
		setMaster(cmp.getBoolean(TAG_MASTER));
	}

	@Override
	protected void writeAdditional(@Nonnull CompoundNBT cmp) {
		super.writeAdditional(cmp);
		cmp.putBoolean(TAG_MASTER, isMaster());
	}

}
