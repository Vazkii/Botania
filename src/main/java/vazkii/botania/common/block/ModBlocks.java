/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BeehiveDispenseBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.IPetalApothecary;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.AlfPortalState;
import vazkii.botania.api.state.enums.LuminizerVariant;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.corporea.*;
import vazkii.botania.common.block.decor.*;
import vazkii.botania.common.block.decor.stairs.BlockModStairs;
import vazkii.botania.common.block.dispenser.*;
import vazkii.botania.common.block.mana.*;
import vazkii.botania.common.block.string.*;
import vazkii.botania.common.entity.EntityEnderAirBottle;
import vazkii.botania.common.entity.EntityVineBall;
import vazkii.botania.common.impl.DefaultHornHarvestable;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockDreamwood;
import vazkii.botania.common.item.block.ItemBlockElven;
import vazkii.botania.common.item.block.ItemBlockPool;
import vazkii.botania.common.item.block.ItemBlockTinyPotato;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.mixin.AccessorDispenserBlock;

import javax.annotation.Nonnull;

import java.util.Locale;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public final class ModBlocks {
	private static final AbstractBlock.IExtendedPositionPredicate<EntityType<?>> NO_SPAWN = (state, world, pos, et) -> false;
	private static final AbstractBlock.IPositionPredicate NO_SUFFOCATION = (state, world, pos) -> false;

	public static final Block whiteFlower = new BlockModFlower(DyeColor.WHITE, Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT));
	public static final Block orangeFlower = new BlockModFlower(DyeColor.ORANGE, Properties.from(whiteFlower));
	public static final Block magentaFlower = new BlockModFlower(DyeColor.MAGENTA, Properties.from(whiteFlower));
	public static final Block lightBlueFlower = new BlockModFlower(DyeColor.LIGHT_BLUE, Properties.from(whiteFlower));
	public static final Block yellowFlower = new BlockModFlower(DyeColor.YELLOW, Properties.from(whiteFlower));
	public static final Block limeFlower = new BlockModFlower(DyeColor.LIME, Properties.from(whiteFlower));
	public static final Block pinkFlower = new BlockModFlower(DyeColor.PINK, Properties.from(whiteFlower));
	public static final Block grayFlower = new BlockModFlower(DyeColor.GRAY, Properties.from(whiteFlower));
	public static final Block lightGrayFlower = new BlockModFlower(DyeColor.LIGHT_GRAY, Properties.from(whiteFlower));
	public static final Block cyanFlower = new BlockModFlower(DyeColor.CYAN, Properties.from(whiteFlower));
	public static final Block purpleFlower = new BlockModFlower(DyeColor.PURPLE, Properties.from(whiteFlower));
	public static final Block blueFlower = new BlockModFlower(DyeColor.BLUE, Properties.from(whiteFlower));
	public static final Block brownFlower = new BlockModFlower(DyeColor.BROWN, Properties.from(whiteFlower));
	public static final Block greenFlower = new BlockModFlower(DyeColor.GREEN, Properties.from(whiteFlower));
	public static final Block redFlower = new BlockModFlower(DyeColor.RED, Properties.from(whiteFlower));
	public static final Block blackFlower = new BlockModFlower(DyeColor.BLACK, Properties.from(whiteFlower));

	public static final Block whiteShinyFlower = new BlockShinyFlower(DyeColor.WHITE, Properties.from(whiteFlower).setLightLevel(s -> 15));
	public static final Block orangeShinyFlower = new BlockShinyFlower(DyeColor.ORANGE, Properties.from(whiteShinyFlower));
	public static final Block magentaShinyFlower = new BlockShinyFlower(DyeColor.MAGENTA, Properties.from(whiteShinyFlower));
	public static final Block lightBlueShinyFlower = new BlockShinyFlower(DyeColor.LIGHT_BLUE, Properties.from(whiteShinyFlower));
	public static final Block yellowShinyFlower = new BlockShinyFlower(DyeColor.YELLOW, Properties.from(whiteShinyFlower));
	public static final Block limeShinyFlower = new BlockShinyFlower(DyeColor.LIME, Properties.from(whiteShinyFlower));
	public static final Block pinkShinyFlower = new BlockShinyFlower(DyeColor.PINK, Properties.from(whiteShinyFlower));
	public static final Block grayShinyFlower = new BlockShinyFlower(DyeColor.GRAY, Properties.from(whiteShinyFlower));
	public static final Block lightGrayShinyFlower = new BlockShinyFlower(DyeColor.LIGHT_GRAY, Properties.from(whiteShinyFlower));
	public static final Block cyanShinyFlower = new BlockShinyFlower(DyeColor.CYAN, Properties.from(whiteShinyFlower));
	public static final Block purpleShinyFlower = new BlockShinyFlower(DyeColor.PURPLE, Properties.from(whiteShinyFlower));
	public static final Block blueShinyFlower = new BlockShinyFlower(DyeColor.BLUE, Properties.from(whiteShinyFlower));
	public static final Block brownShinyFlower = new BlockShinyFlower(DyeColor.BROWN, Properties.from(whiteShinyFlower));
	public static final Block greenShinyFlower = new BlockShinyFlower(DyeColor.GREEN, Properties.from(whiteShinyFlower));
	public static final Block redShinyFlower = new BlockShinyFlower(DyeColor.RED, Properties.from(whiteShinyFlower));
	public static final Block blackShinyFlower = new BlockShinyFlower(DyeColor.BLACK, Properties.from(whiteShinyFlower));

	public static final Block whiteBuriedPetals = new BlockBuriedPetals(DyeColor.WHITE, Properties.from(whiteFlower).setLightLevel(s -> 4));
	public static final Block orangeBuriedPetals = new BlockBuriedPetals(DyeColor.ORANGE, Properties.from(whiteBuriedPetals));
	public static final Block magentaBuriedPetals = new BlockBuriedPetals(DyeColor.MAGENTA, Properties.from(whiteBuriedPetals));
	public static final Block lightBlueBuriedPetals = new BlockBuriedPetals(DyeColor.LIGHT_BLUE, Properties.from(whiteBuriedPetals));
	public static final Block yellowBuriedPetals = new BlockBuriedPetals(DyeColor.YELLOW, Properties.from(whiteBuriedPetals));
	public static final Block limeBuriedPetals = new BlockBuriedPetals(DyeColor.LIME, Properties.from(whiteBuriedPetals));
	public static final Block pinkBuriedPetals = new BlockBuriedPetals(DyeColor.PINK, Properties.from(whiteBuriedPetals));
	public static final Block grayBuriedPetals = new BlockBuriedPetals(DyeColor.GRAY, Properties.from(whiteBuriedPetals));
	public static final Block lightGrayBuriedPetals = new BlockBuriedPetals(DyeColor.LIGHT_GRAY, Properties.from(whiteBuriedPetals));
	public static final Block cyanBuriedPetals = new BlockBuriedPetals(DyeColor.CYAN, Properties.from(whiteBuriedPetals));
	public static final Block purpleBuriedPetals = new BlockBuriedPetals(DyeColor.PURPLE, Properties.from(whiteBuriedPetals));
	public static final Block blueBuriedPetals = new BlockBuriedPetals(DyeColor.BLUE, Properties.from(whiteBuriedPetals));
	public static final Block brownBuriedPetals = new BlockBuriedPetals(DyeColor.BROWN, Properties.from(whiteBuriedPetals));
	public static final Block greenBuriedPetals = new BlockBuriedPetals(DyeColor.GREEN, Properties.from(whiteBuriedPetals));
	public static final Block redBuriedPetals = new BlockBuriedPetals(DyeColor.RED, Properties.from(whiteBuriedPetals));
	public static final Block blackBuriedPetals = new BlockBuriedPetals(DyeColor.BLACK, Properties.from(whiteBuriedPetals));

	public static final Properties FLOATING_PROPS = Properties.create(Material.EARTH).hardnessAndResistance(0.5F).sound(SoundType.GROUND).setLightLevel(s -> 15).harvestTool(ToolType.SHOVEL);
	public static final Block whiteFloatingFlower = new BlockFloatingFlower(DyeColor.WHITE, FLOATING_PROPS);
	public static final Block orangeFloatingFlower = new BlockFloatingFlower(DyeColor.ORANGE, FLOATING_PROPS);
	public static final Block magentaFloatingFlower = new BlockFloatingFlower(DyeColor.MAGENTA, FLOATING_PROPS);
	public static final Block lightBlueFloatingFlower = new BlockFloatingFlower(DyeColor.LIGHT_BLUE, FLOATING_PROPS);
	public static final Block yellowFloatingFlower = new BlockFloatingFlower(DyeColor.YELLOW, FLOATING_PROPS);
	public static final Block limeFloatingFlower = new BlockFloatingFlower(DyeColor.LIME, FLOATING_PROPS);
	public static final Block pinkFloatingFlower = new BlockFloatingFlower(DyeColor.PINK, FLOATING_PROPS);
	public static final Block grayFloatingFlower = new BlockFloatingFlower(DyeColor.GRAY, FLOATING_PROPS);
	public static final Block lightGrayFloatingFlower = new BlockFloatingFlower(DyeColor.LIGHT_GRAY, FLOATING_PROPS);
	public static final Block cyanFloatingFlower = new BlockFloatingFlower(DyeColor.CYAN, FLOATING_PROPS);
	public static final Block purpleFloatingFlower = new BlockFloatingFlower(DyeColor.PURPLE, FLOATING_PROPS);
	public static final Block blueFloatingFlower = new BlockFloatingFlower(DyeColor.BLUE, FLOATING_PROPS);
	public static final Block brownFloatingFlower = new BlockFloatingFlower(DyeColor.BROWN, FLOATING_PROPS);
	public static final Block greenFloatingFlower = new BlockFloatingFlower(DyeColor.GREEN, FLOATING_PROPS);
	public static final Block redFloatingFlower = new BlockFloatingFlower(DyeColor.RED, FLOATING_PROPS);
	public static final Block blackFloatingFlower = new BlockFloatingFlower(DyeColor.BLACK, FLOATING_PROPS);

	public static final Block petalBlockWhite = new BlockPetalBlock(DyeColor.WHITE, Properties.create(Material.PLANTS).hardnessAndResistance(0.4F).sound(SoundType.PLANT));
	public static final Block petalBlockOrange = new BlockPetalBlock(DyeColor.ORANGE, Properties.from(petalBlockWhite));
	public static final Block petalBlockMagenta = new BlockPetalBlock(DyeColor.MAGENTA, Properties.from(petalBlockWhite));
	public static final Block petalBlockLightBlue = new BlockPetalBlock(DyeColor.LIGHT_BLUE, Properties.from(petalBlockWhite));
	public static final Block petalBlockYellow = new BlockPetalBlock(DyeColor.YELLOW, Properties.from(petalBlockWhite));
	public static final Block petalBlockLime = new BlockPetalBlock(DyeColor.LIME, Properties.from(petalBlockWhite));
	public static final Block petalBlockPink = new BlockPetalBlock(DyeColor.PINK, Properties.from(petalBlockWhite));
	public static final Block petalBlockGray = new BlockPetalBlock(DyeColor.GRAY, Properties.from(petalBlockWhite));
	public static final Block petalBlockSilver = new BlockPetalBlock(DyeColor.LIGHT_GRAY, Properties.from(petalBlockWhite));
	public static final Block petalBlockCyan = new BlockPetalBlock(DyeColor.CYAN, Properties.from(petalBlockWhite));
	public static final Block petalBlockPurple = new BlockPetalBlock(DyeColor.PURPLE, Properties.from(petalBlockWhite));
	public static final Block petalBlockBlue = new BlockPetalBlock(DyeColor.BLUE, Properties.from(petalBlockWhite));
	public static final Block petalBlockBrown = new BlockPetalBlock(DyeColor.BROWN, Properties.from(petalBlockWhite));
	public static final Block petalBlockGreen = new BlockPetalBlock(DyeColor.GREEN, Properties.from(petalBlockWhite));
	public static final Block petalBlockRed = new BlockPetalBlock(DyeColor.RED, Properties.from(petalBlockWhite));
	public static final Block petalBlockBlack = new BlockPetalBlock(DyeColor.BLACK, Properties.from(petalBlockWhite));

	public static final Block whiteMushroom = new BlockModMushroom(DyeColor.WHITE, Properties.from(whiteFlower).setLightLevel(s -> 3));
	public static final Block orangeMushroom = new BlockModMushroom(DyeColor.ORANGE, Properties.from(whiteMushroom));
	public static final Block magentaMushroom = new BlockModMushroom(DyeColor.MAGENTA, Properties.from(whiteMushroom));
	public static final Block lightBlueMushroom = new BlockModMushroom(DyeColor.LIGHT_BLUE, Properties.from(whiteMushroom));
	public static final Block yellowMushroom = new BlockModMushroom(DyeColor.YELLOW, Properties.from(whiteMushroom));
	public static final Block limeMushroom = new BlockModMushroom(DyeColor.LIME, Properties.from(whiteMushroom));
	public static final Block pinkMushroom = new BlockModMushroom(DyeColor.PINK, Properties.from(whiteMushroom));
	public static final Block grayMushroom = new BlockModMushroom(DyeColor.GRAY, Properties.from(whiteMushroom));
	public static final Block lightGrayMushroom = new BlockModMushroom(DyeColor.LIGHT_GRAY, Properties.from(whiteMushroom));
	public static final Block cyanMushroom = new BlockModMushroom(DyeColor.CYAN, Properties.from(whiteMushroom));
	public static final Block purpleMushroom = new BlockModMushroom(DyeColor.PURPLE, Properties.from(whiteMushroom));
	public static final Block blueMushroom = new BlockModMushroom(DyeColor.BLUE, Properties.from(whiteMushroom));
	public static final Block brownMushroom = new BlockModMushroom(DyeColor.BROWN, Properties.from(whiteMushroom));
	public static final Block greenMushroom = new BlockModMushroom(DyeColor.GREEN, Properties.from(whiteMushroom));
	public static final Block redMushroom = new BlockModMushroom(DyeColor.RED, Properties.from(whiteMushroom));
	public static final Block blackMushroom = new BlockModMushroom(DyeColor.BLACK, Properties.from(whiteMushroom));

	public static final Block doubleFlowerWhite = new BlockModDoubleFlower(DyeColor.WHITE, Properties.from(whiteFlower));
	public static final Block doubleFlowerOrange = new BlockModDoubleFlower(DyeColor.ORANGE, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerMagenta = new BlockModDoubleFlower(DyeColor.MAGENTA, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerLightBlue = new BlockModDoubleFlower(DyeColor.LIGHT_BLUE, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerYellow = new BlockModDoubleFlower(DyeColor.YELLOW, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerLime = new BlockModDoubleFlower(DyeColor.LIME, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerPink = new BlockModDoubleFlower(DyeColor.PINK, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerGray = new BlockModDoubleFlower(DyeColor.GRAY, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerLightGray = new BlockModDoubleFlower(DyeColor.LIGHT_GRAY, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerCyan = new BlockModDoubleFlower(DyeColor.CYAN, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerPurple = new BlockModDoubleFlower(DyeColor.PURPLE, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerBlue = new BlockModDoubleFlower(DyeColor.BLUE, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerBrown = new BlockModDoubleFlower(DyeColor.BROWN, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerGreen = new BlockModDoubleFlower(DyeColor.GREEN, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerRed = new BlockModDoubleFlower(DyeColor.RED, Properties.from(doubleFlowerWhite));
	public static final Block doubleFlowerBlack = new BlockModDoubleFlower(DyeColor.BLACK, Properties.from(doubleFlowerWhite));

	public static final Block defaultAltar = new BlockAltar(BlockAltar.Variant.DEFAULT, Properties.create(Material.ROCK).hardnessAndResistance(3.5F).sound(SoundType.STONE)
			.setLightLevel(s -> s.get(BlockAltar.FLUID) == IPetalApothecary.State.LAVA ? 15 : 0));
	public static final Block forestAltar = new BlockAltar(BlockAltar.Variant.FOREST, Properties.from(defaultAltar));
	public static final Block plainsAltar = new BlockAltar(BlockAltar.Variant.PLAINS, Properties.from(defaultAltar));
	public static final Block mountainAltar = new BlockAltar(BlockAltar.Variant.MOUNTAIN, Properties.from(defaultAltar));
	public static final Block fungalAltar = new BlockAltar(BlockAltar.Variant.FUNGAL, Properties.from(defaultAltar));
	public static final Block swampAltar = new BlockAltar(BlockAltar.Variant.SWAMP, Properties.from(defaultAltar));
	public static final Block desertAltar = new BlockAltar(BlockAltar.Variant.DESERT, Properties.from(defaultAltar));
	public static final Block taigaAltar = new BlockAltar(BlockAltar.Variant.TAIGA, Properties.from(defaultAltar));
	public static final Block mesaAltar = new BlockAltar(BlockAltar.Variant.MESA, Properties.from(defaultAltar));
	public static final Block mossyAltar = new BlockAltar(BlockAltar.Variant.MOSSY, Properties.from(defaultAltar));

	public static final Block livingrock = new BlockMod(Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
	public static final Block livingrockBrick = new BlockMod(Properties.from(livingrock));
	public static final Block livingrockBrickChiseled = new BlockMod(Properties.from(livingrock));
	public static final Block livingrockBrickCracked = new BlockMod(Properties.from(livingrock));
	public static final Block livingrockBrickMossy = new BlockMod(Properties.from(livingrock));

	// TODO 1.16+ livingwood/dreamwood should support leaves?
	public static final Block livingwood = new BlockMod(Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD));
	public static final Block livingwoodPlanks = new BlockMod(Properties.from(livingwood));
	public static final Block livingwoodPlanksMossy = new BlockMod(Properties.from(livingwood));
	public static final Block livingwoodFramed = new BlockMod(Properties.from(livingwood));
	public static final Block livingwoodPatternFramed = new BlockMod(Properties.from(livingwood));
	public static final Block livingwoodGlimmering = new BlockMod(Properties.from(livingwood).setLightLevel(s -> 12));

	public static final Block dreamwood = new BlockMod(Properties.from(livingwood));
	public static final Block dreamwoodPlanks = new BlockMod(Properties.from(livingwood));
	public static final Block dreamwoodPlanksMossy = new BlockMod(Properties.from(livingwood));
	public static final Block dreamwoodFramed = new BlockMod(Properties.from(livingwood));
	public static final Block dreamwoodPatternFramed = new BlockMod(Properties.from(livingwood));
	public static final Block dreamwoodGlimmering = new BlockMod(Properties.from(livingwoodGlimmering));

	public static final Block manaSpreader = new BlockSpreader(BlockSpreader.Variant.MANA, Properties.from(livingwood).setAllowsSpawn(NO_SPAWN));
	public static final Block redstoneSpreader = new BlockSpreader(BlockSpreader.Variant.REDSTONE, Properties.from(livingwood).setAllowsSpawn(NO_SPAWN));
	public static final Block elvenSpreader = new BlockSpreader(BlockSpreader.Variant.ELVEN, Properties.from(livingwood).setAllowsSpawn(NO_SPAWN));
	public static final Block gaiaSpreader = new BlockSpreader(BlockSpreader.Variant.GAIA, Properties.from(livingwood).setAllowsSpawn(NO_SPAWN));

	public static final Block manaPool = new BlockPool(BlockPool.Variant.DEFAULT, Properties.from(livingrock));
	public static final Block creativePool = new BlockPool(BlockPool.Variant.CREATIVE, Properties.from(livingrock));
	public static final Block dilutedPool = new BlockPool(BlockPool.Variant.DILUTED, Properties.from(livingrock));
	public static final Block fabulousPool = new BlockPool(BlockPool.Variant.FABULOUS, Properties.from(livingrock));
	public static final Block alchemyCatalyst = new BlockAlchemyCatalyst(Properties.from(livingrock));
	public static final Block conjurationCatalyst = new BlockConjurationCatalyst(Properties.from(livingrock));

	public static final Block manasteelBlock = new BlockMod(Properties.create(Material.IRON).hardnessAndResistance(3, 10).sound(SoundType.METAL));
	public static final Block terrasteelBlock = new BlockMod(Properties.from(manasteelBlock));
	public static final Block elementiumBlock = new BlockMod(Properties.from(manasteelBlock));
	public static final Block manaDiamondBlock = new BlockMod(Properties.from(manasteelBlock));
	public static final Block dragonstoneBlock = new BlockMod(Properties.from(manasteelBlock));

	public static final Block manaGlass = new BlockModGlass(Properties.from(Blocks.GLASS).setLightLevel(s -> 15).setBlocksVision(NO_SUFFOCATION).setSuffocates(NO_SUFFOCATION).setAllowsSpawn(NO_SPAWN));
	public static final Block elfGlass = new BlockModGlass(Properties.from(manaGlass).setBlocksVision(NO_SUFFOCATION).setSuffocates(NO_SUFFOCATION).setAllowsSpawn(NO_SPAWN));
	public static final Block bifrost = new BlockBifrost(Properties.create(Material.GLASS).hardnessAndResistance(-1, 0.3F)
			.setLightLevel(s -> 15).sound(SoundType.GLASS).notSolid().setBlocksVision(NO_SUFFOCATION).setSuffocates(NO_SUFFOCATION).setAllowsSpawn(NO_SPAWN));
	public static final Block bifrostPerm = new BlockBifrostPerm(Properties.create(Material.GLASS).hardnessAndResistance(0.3F)
			.setLightLevel(s -> 15).sound(SoundType.GLASS).notSolid().setBlocksVision(NO_SUFFOCATION).setSuffocates(NO_SUFFOCATION).setAllowsSpawn(NO_SPAWN));

	public static final Block runeAltar = new BlockRuneAltar(Properties.from(livingrock));
	public static final Block enchanter = new BlockEnchanter(Properties.create(Material.ROCK).hardnessAndResistance(3, 5).setLightLevel(s -> 15).sound(SoundType.STONE));
	public static final Block brewery = new BlockBrewery(Properties.from(livingrock));
	public static final Block terraPlate = new BlockTerraPlate(Properties.create(Material.IRON).hardnessAndResistance(3, 10).sound(SoundType.METAL));
	public static final Block alfPortal = new BlockAlfPortal(Properties.create(Material.WOOD).hardnessAndResistance(10).sound(SoundType.WOOD)
			.setLightLevel(s -> s.get(BotaniaStateProps.ALFPORTAL_STATE) != AlfPortalState.OFF ? 15 : 0));

	public static final Block manaPylon = new BlockPylon(BlockPylon.Variant.MANA, Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL).setLightLevel(s -> 7));
	public static final Block naturaPylon = new BlockPylon(BlockPylon.Variant.NATURA, Properties.from(manaPylon));
	public static final Block gaiaPylon = new BlockPylon(BlockPylon.Variant.GAIA, Properties.from(manaPylon));

	public static final Block distributor = new BlockDistributor(Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
	public static final Block manaVoid = new BlockManaVoid(Properties.create(Material.ROCK).hardnessAndResistance(2, 2000).sound(SoundType.STONE));
	public static final Block manaDetector = new BlockManaDetector(Properties.from(livingrock));
	public static final Block pistonRelay = new BlockPistonRelay(Properties.create(Material.GOURD).hardnessAndResistance(2, 10).sound(SoundType.METAL).setAllowsSpawn(NO_SPAWN));
	public static final Block turntable = new BlockTurntable(Properties.from(livingwood));
	public static final Block tinyPlanet = new BlockTinyPlanet(Properties.create(Material.ROCK).hardnessAndResistance(20, 100).sound(SoundType.STONE));
	public static final Block wildDrum = new BlockForestDrum(BlockForestDrum.Variant.WILD, Properties.from(livingwood));
	public static final Block gatheringDrum = new BlockForestDrum(BlockForestDrum.Variant.GATHERING, Properties.from(livingwood));
	public static final Block canopyDrum = new BlockForestDrum(BlockForestDrum.Variant.CANOPY, Properties.from(livingwood));
	public static final Block spawnerClaw = new BlockSpawnerClaw(Properties.create(Material.IRON).hardnessAndResistance(3));
	public static final Block rfGenerator = new BlockRFGenerator(Properties.from(livingrock));
	public static final Block prism = new BlockPrism(Properties.from(elfGlass).doesNotBlockMovement());
	public static final Block pump = new BlockPump(Properties.from(livingrock));
	public static final Block sparkChanger = new BlockSparkChanger(Properties.from(livingrock));
	public static final Block manaBomb = new BlockManaBomb(Properties.create(Material.WOOD).hardnessAndResistance(12).sound(SoundType.WOOD));
	public static final Block bellows = new BlockBellows(Properties.from(livingwood));

	public static final Block openCrate = new BlockOpenCrate(Properties.from(livingwood));
	public static final Block craftCrate = new BlockCraftyCrate(Properties.from(livingwood));
	public static final Block forestEye = new BlockForestEye(Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL));
	public static final Block solidVines = new BlockSolidVines(Properties.create(Material.TALL_PLANTS).hardnessAndResistance(0.2F).sound(SoundType.PLANT).notSolid());
	public static final Block abstrusePlatform = new BlockPlatform(BlockPlatform.Variant.ABSTRUSE, Properties.create(Material.WOOD).hardnessAndResistance(2, 5).sound(SoundType.WOOD).notSolid().variableOpacity());
	public static final Block spectralPlatform = new BlockPlatform(BlockPlatform.Variant.SPECTRAL, Properties.from(abstrusePlatform));
	public static final Block infrangiblePlatform = new BlockPlatform(BlockPlatform.Variant.INFRANGIBLE, Properties.create(Material.WOOD).hardnessAndResistance(-1, Float.MAX_VALUE).sound(SoundType.WOOD).notSolid().variableOpacity());
	public static final Block tinyPotato = new BlockTinyPotato(Properties.create(Material.WOOL).hardnessAndResistance(0.25F));
	public static final Block enderEye = new BlockEnderEye(Properties.from(manasteelBlock));
	public static final Block redStringContainer = new BlockRedStringContainer(Properties.from(livingrock));
	public static final Block redStringDispenser = new BlockRedStringDispenser(Properties.from(livingrock));
	public static final Block redStringFertilizer = new BlockRedStringFertilizer(Properties.from(livingrock));
	public static final Block redStringComparator = new BlockRedStringComparator(Properties.from(livingrock));
	public static final Block redStringRelay = new BlockRedStringRelay(Properties.from(livingrock));
	public static final Block redStringInterceptor = new BlockRedStringInterceptor(Properties.from(livingrock));

	public static final Block corporeaIndex = new BlockCorporeaIndex(Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL).notSolid());
	public static final Block corporeaFunnel = new BlockCorporeaFunnel(Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL));
	public static final Block corporeaInterceptor = new BlockCorporeaInterceptor(Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL));
	public static final Block corporeaCrystalCube = new BlockCorporeaCrystalCube(Properties.from(corporeaInterceptor));
	public static final Block corporeaRetainer = new BlockCorporeaRetainer(Properties.from(corporeaInterceptor));

	public static final Block corporeaBlock = new BlockMod(Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL));
	public static final SlabBlock corporeaSlab = new SlabBlock(Properties.from(corporeaBlock));
	public static final StairsBlock corporeaStairs = new BlockModStairs(corporeaBlock.getDefaultState(), Properties.from(corporeaBlock));
	public static final Block corporeaBrick = new BlockMod(Properties.from(corporeaBlock));
	public static final SlabBlock corporeaBrickSlab = new SlabBlock(Properties.from(corporeaBrick));
	public static final StairsBlock corporeaBrickStairs = new BlockModStairs(corporeaBrick.getDefaultState(), Properties.from(corporeaBrick));
	public static final Block corporeaBrickWall = new WallBlock(Properties.from(corporeaBrick));

	public static final Block incensePlate = new BlockIncensePlate(Properties.from(livingwood));
	public static final Block hourglass = new BlockHourglass(Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL));
	public static final Block ghostRail = new BlockGhostRail(Properties.from(Blocks.RAIL));
	public static final Block lightRelayDefault = new BlockLightRelay(LuminizerVariant.DEFAULT, Properties.create(Material.GLASS).doesNotBlockMovement());
	public static final Block lightRelayDetector = new BlockLightRelay(LuminizerVariant.DETECTOR, Properties.from(lightRelayDefault));
	public static final Block lightRelayFork = new BlockLightRelay(LuminizerVariant.FORK, Properties.from(lightRelayDefault));
	public static final Block lightRelayToggle = new BlockLightRelay(LuminizerVariant.TOGGLE, Properties.from(lightRelayDefault));
	public static final Block lightLauncher = new BlockLightLauncher(Properties.from(livingwood));
	public static final Block cacophonium = new BlockCacophonium(Properties.create(Material.WOOD).hardnessAndResistance(0.8F));
	public static final Block cellBlock = new BlockCell(Properties.create(Material.GOURD).sound(SoundType.CLOTH));
	public static final Block teruTeruBozu = new BlockTeruTeruBozu(Properties.create(Material.WOOL));
	public static final Block avatar = new BlockAvatar(Properties.from(livingwood));
	public static final Block fakeAir = new BlockFakeAir(Properties.create(Material.STRUCTURE_VOID).setAir().tickRandomly());
	public static final Block root = new BlockRoot(Properties.create(Material.PLANTS).hardnessAndResistance(1.2F).sound(SoundType.WOOD));
	public static final Block felPumpkin = new BlockFelPumpkin(Properties.from(Blocks.CARVED_PUMPKIN));
	public static final Block cocoon = new BlockCocoon(Properties.create(Material.WOOL).hardnessAndResistance(3, 60).sound(SoundType.CLOTH));
	public static final Block enchantedSoil = new BlockEnchantedSoil(Properties.create(Material.ORGANIC).hardnessAndResistance(0.6F).sound(SoundType.PLANT));
	public static final Block animatedTorch = new BlockAnimatedTorch(Properties.create(Material.MISCELLANEOUS).setLightLevel(s -> 7).notSolid());
	public static final Block starfield = new BlockStarfield(Properties.create(Material.IRON).hardnessAndResistance(5, 2000).sound(SoundType.METAL));

	public static final Block azulejo0 = new BlockMod(Properties.create(Material.ROCK).hardnessAndResistance(2, 5).sound(SoundType.STONE));
	public static final Block azulejo1 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo2 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo3 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo4 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo5 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo6 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo7 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo8 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo9 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo10 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo11 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo12 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo13 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo14 = new BlockMod(Properties.from(azulejo0));
	public static final Block azulejo15 = new BlockMod(Properties.from(azulejo0));
	public static final Block manaFlame = new BlockManaFlame(Properties.create(Material.MISCELLANEOUS).sound(SoundType.CLOTH).setLightLevel(s -> 15).doesNotBlockMovement());
	public static final Block blazeBlock = new BlockMod(Properties.from(manasteelBlock).setLightLevel(s -> 15));
	public static final Block gaiaHeadWall = new BlockGaiaHeadWall(Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1));
	public static final Block gaiaHead = new BlockGaiaHead(Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1));
	public static final Block shimmerrock = new BlockMod(Properties.from(livingrock));
	public static final Block shimmerwoodPlanks = new BlockMod(Properties.from(livingwood));
	public static final Block dryGrass = new BlockAltGrass(BlockAltGrass.Variant.DRY, Properties.create(Material.ORGANIC).hardnessAndResistance(0.6F).tickRandomly().sound(SoundType.PLANT).harvestTool(ToolType.SHOVEL));
	public static final Block goldenGrass = new BlockAltGrass(BlockAltGrass.Variant.GOLDEN, Properties.from(dryGrass));
	public static final Block vividGrass = new BlockAltGrass(BlockAltGrass.Variant.VIVID, Properties.from(dryGrass));
	public static final Block scorchedGrass = new BlockAltGrass(BlockAltGrass.Variant.SCORCHED, Properties.from(dryGrass));
	public static final Block infusedGrass = new BlockAltGrass(BlockAltGrass.Variant.INFUSED, Properties.from(dryGrass));
	public static final Block mutatedGrass = new BlockAltGrass(BlockAltGrass.Variant.MUTATED, Properties.from(dryGrass));

	public static final Block motifDaybloom = new BlockMotifFlower(Effects.BLINDNESS, 15, Properties.from(Blocks.POPPY), true);
	public static final Block motifNightshade = new BlockMotifFlower(Effects.POISON, 20, Properties.from(Blocks.POPPY), true);
	public static final Block motifHydroangeas = new BlockMotifFlower(Effects.UNLUCK, 10, Properties.from(Blocks.POPPY), false);

	public static void registerBlocks(RegistryEvent.Register<Block> evt) {
		IForgeRegistry<Block> r = evt.getRegistry();
		register(r, "white" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, whiteFlower);
		register(r, "orange" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, orangeFlower);
		register(r, "magenta" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, magentaFlower);
		register(r, "light_blue" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, lightBlueFlower);
		register(r, "yellow" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, yellowFlower);
		register(r, "lime" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, limeFlower);
		register(r, "pink" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, pinkFlower);
		register(r, "gray" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, grayFlower);
		register(r, "light_gray" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, lightGrayFlower);
		register(r, "cyan" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, cyanFlower);
		register(r, "purple" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, purpleFlower);
		register(r, "blue" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, blueFlower);
		register(r, "brown" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, brownFlower);
		register(r, "green" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, greenFlower);
		register(r, "red" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, redFlower);
		register(r, "black" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, blackFlower);
		register(r, "white" + LibBlockNames.SHINY_FLOWER_SUFFIX, whiteShinyFlower);
		register(r, "orange" + LibBlockNames.SHINY_FLOWER_SUFFIX, orangeShinyFlower);
		register(r, "magenta" + LibBlockNames.SHINY_FLOWER_SUFFIX, magentaShinyFlower);
		register(r, "light_blue" + LibBlockNames.SHINY_FLOWER_SUFFIX, lightBlueShinyFlower);
		register(r, "yellow" + LibBlockNames.SHINY_FLOWER_SUFFIX, yellowShinyFlower);
		register(r, "lime" + LibBlockNames.SHINY_FLOWER_SUFFIX, limeShinyFlower);
		register(r, "pink" + LibBlockNames.SHINY_FLOWER_SUFFIX, pinkShinyFlower);
		register(r, "gray" + LibBlockNames.SHINY_FLOWER_SUFFIX, grayShinyFlower);
		register(r, "light_gray" + LibBlockNames.SHINY_FLOWER_SUFFIX, lightGrayShinyFlower);
		register(r, "cyan" + LibBlockNames.SHINY_FLOWER_SUFFIX, cyanShinyFlower);
		register(r, "purple" + LibBlockNames.SHINY_FLOWER_SUFFIX, purpleShinyFlower);
		register(r, "blue" + LibBlockNames.SHINY_FLOWER_SUFFIX, blueShinyFlower);
		register(r, "brown" + LibBlockNames.SHINY_FLOWER_SUFFIX, brownShinyFlower);
		register(r, "green" + LibBlockNames.SHINY_FLOWER_SUFFIX, greenShinyFlower);
		register(r, "red" + LibBlockNames.SHINY_FLOWER_SUFFIX, redShinyFlower);
		register(r, "black" + LibBlockNames.SHINY_FLOWER_SUFFIX, blackShinyFlower);
		register(r, "white" + LibBlockNames.BURIED_PETALS_SUFFIX, whiteBuriedPetals);
		register(r, "orange" + LibBlockNames.BURIED_PETALS_SUFFIX, orangeBuriedPetals);
		register(r, "magenta" + LibBlockNames.BURIED_PETALS_SUFFIX, magentaBuriedPetals);
		register(r, "light_blue" + LibBlockNames.BURIED_PETALS_SUFFIX, lightBlueBuriedPetals);
		register(r, "yellow" + LibBlockNames.BURIED_PETALS_SUFFIX, yellowBuriedPetals);
		register(r, "lime" + LibBlockNames.BURIED_PETALS_SUFFIX, limeBuriedPetals);
		register(r, "pink" + LibBlockNames.BURIED_PETALS_SUFFIX, pinkBuriedPetals);
		register(r, "gray" + LibBlockNames.BURIED_PETALS_SUFFIX, grayBuriedPetals);
		register(r, "light_gray" + LibBlockNames.BURIED_PETALS_SUFFIX, lightGrayBuriedPetals);
		register(r, "cyan" + LibBlockNames.BURIED_PETALS_SUFFIX, cyanBuriedPetals);
		register(r, "purple" + LibBlockNames.BURIED_PETALS_SUFFIX, purpleBuriedPetals);
		register(r, "blue" + LibBlockNames.BURIED_PETALS_SUFFIX, blueBuriedPetals);
		register(r, "brown" + LibBlockNames.BURIED_PETALS_SUFFIX, brownBuriedPetals);
		register(r, "green" + LibBlockNames.BURIED_PETALS_SUFFIX, greenBuriedPetals);
		register(r, "red" + LibBlockNames.BURIED_PETALS_SUFFIX, redBuriedPetals);
		register(r, "black" + LibBlockNames.BURIED_PETALS_SUFFIX, blackBuriedPetals);
		register(r, "white" + LibBlockNames.FLOATING_FLOWER_SUFFIX, whiteFloatingFlower);
		register(r, "orange" + LibBlockNames.FLOATING_FLOWER_SUFFIX, orangeFloatingFlower);
		register(r, "magenta" + LibBlockNames.FLOATING_FLOWER_SUFFIX, magentaFloatingFlower);
		register(r, "light_blue" + LibBlockNames.FLOATING_FLOWER_SUFFIX, lightBlueFloatingFlower);
		register(r, "yellow" + LibBlockNames.FLOATING_FLOWER_SUFFIX, yellowFloatingFlower);
		register(r, "lime" + LibBlockNames.FLOATING_FLOWER_SUFFIX, limeFloatingFlower);
		register(r, "pink" + LibBlockNames.FLOATING_FLOWER_SUFFIX, pinkFloatingFlower);
		register(r, "gray" + LibBlockNames.FLOATING_FLOWER_SUFFIX, grayFloatingFlower);
		register(r, "light_gray" + LibBlockNames.FLOATING_FLOWER_SUFFIX, lightGrayFloatingFlower);
		register(r, "cyan" + LibBlockNames.FLOATING_FLOWER_SUFFIX, cyanFloatingFlower);
		register(r, "purple" + LibBlockNames.FLOATING_FLOWER_SUFFIX, purpleFloatingFlower);
		register(r, "blue" + LibBlockNames.FLOATING_FLOWER_SUFFIX, blueFloatingFlower);
		register(r, "brown" + LibBlockNames.FLOATING_FLOWER_SUFFIX, brownFloatingFlower);
		register(r, "green" + LibBlockNames.FLOATING_FLOWER_SUFFIX, greenFloatingFlower);
		register(r, "red" + LibBlockNames.FLOATING_FLOWER_SUFFIX, redFloatingFlower);
		register(r, "black" + LibBlockNames.FLOATING_FLOWER_SUFFIX, blackFloatingFlower);
		register(r, "white" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockWhite);
		register(r, "orange" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockOrange);
		register(r, "magenta" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockMagenta);
		register(r, "light_blue" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockLightBlue);
		register(r, "yellow" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockYellow);
		register(r, "lime" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockLime);
		register(r, "pink" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockPink);
		register(r, "gray" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockGray);
		register(r, "light_gray" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockSilver);
		register(r, "cyan" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockCyan);
		register(r, "purple" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockPurple);
		register(r, "blue" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockBlue);
		register(r, "brown" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockBrown);
		register(r, "green" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockGreen);
		register(r, "red" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockRed);
		register(r, "black" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockBlack);
		register(r, "white" + LibBlockNames.MUSHROOM_SUFFIX, whiteMushroom);
		register(r, "orange" + LibBlockNames.MUSHROOM_SUFFIX, orangeMushroom);
		register(r, "magenta" + LibBlockNames.MUSHROOM_SUFFIX, magentaMushroom);
		register(r, "light_blue" + LibBlockNames.MUSHROOM_SUFFIX, lightBlueMushroom);
		register(r, "yellow" + LibBlockNames.MUSHROOM_SUFFIX, yellowMushroom);
		register(r, "lime" + LibBlockNames.MUSHROOM_SUFFIX, limeMushroom);
		register(r, "pink" + LibBlockNames.MUSHROOM_SUFFIX, pinkMushroom);
		register(r, "gray" + LibBlockNames.MUSHROOM_SUFFIX, grayMushroom);
		register(r, "light_gray" + LibBlockNames.MUSHROOM_SUFFIX, lightGrayMushroom);
		register(r, "cyan" + LibBlockNames.MUSHROOM_SUFFIX, cyanMushroom);
		register(r, "purple" + LibBlockNames.MUSHROOM_SUFFIX, purpleMushroom);
		register(r, "blue" + LibBlockNames.MUSHROOM_SUFFIX, blueMushroom);
		register(r, "brown" + LibBlockNames.MUSHROOM_SUFFIX, brownMushroom);
		register(r, "green" + LibBlockNames.MUSHROOM_SUFFIX, greenMushroom);
		register(r, "red" + LibBlockNames.MUSHROOM_SUFFIX, redMushroom);
		register(r, "black" + LibBlockNames.MUSHROOM_SUFFIX, blackMushroom);
		register(r, "white" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerWhite);
		register(r, "orange" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerOrange);
		register(r, "magenta" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerMagenta);
		register(r, "light_blue" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerLightBlue);
		register(r, "yellow" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerYellow);
		register(r, "lime" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerLime);
		register(r, "pink" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerPink);
		register(r, "gray" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerGray);
		register(r, "light_gray" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerLightGray);
		register(r, "cyan" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerCyan);
		register(r, "purple" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerPurple);
		register(r, "blue" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerBlue);
		register(r, "brown" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerBrown);
		register(r, "green" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerGreen);
		register(r, "red" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerRed);
		register(r, "black" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerBlack);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.DEFAULT.name().toLowerCase(Locale.ROOT), defaultAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.FOREST.name().toLowerCase(Locale.ROOT), forestAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.PLAINS.name().toLowerCase(Locale.ROOT), plainsAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.MOUNTAIN.name().toLowerCase(Locale.ROOT), mountainAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.FUNGAL.name().toLowerCase(Locale.ROOT), fungalAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.SWAMP.name().toLowerCase(Locale.ROOT), swampAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.DESERT.name().toLowerCase(Locale.ROOT), desertAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.TAIGA.name().toLowerCase(Locale.ROOT), taigaAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.MESA.name().toLowerCase(Locale.ROOT), mesaAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.MOSSY.name().toLowerCase(Locale.ROOT), mossyAltar);
		register(r, LibBlockNames.LIVING_ROCK, livingrock);
		register(r, LibBlockNames.LIVING_ROCK_BRICK, livingrockBrick);
		register(r, LibBlockNames.LIVING_ROCK_BRICK_CHISELED, livingrockBrickChiseled);
		register(r, LibBlockNames.LIVING_ROCK_BRICK_CRACKED, livingrockBrickCracked);
		register(r, LibBlockNames.LIVING_ROCK_BRICK_MOSSY, livingrockBrickMossy);
		register(r, LibBlockNames.LIVING_WOOD, livingwood);
		register(r, LibBlockNames.LIVING_WOOD_PLANKS, livingwoodPlanks);
		register(r, LibBlockNames.LIVING_WOOD_PLANKS_MOSSY, livingwoodPlanksMossy);
		register(r, LibBlockNames.LIVING_WOOD_FRAMED, livingwoodFramed);
		register(r, LibBlockNames.LIVING_WOOD_PATTERN_FRAMED, livingwoodPatternFramed);
		register(r, LibBlockNames.LIVING_WOOD_GLIMMERING, livingwoodGlimmering);
		register(r, LibBlockNames.DREAM_WOOD, dreamwood);
		register(r, LibBlockNames.DREAM_WOOD_PLANKS, dreamwoodPlanks);
		register(r, LibBlockNames.DREAM_WOOD_PLANKS_MOSSY, dreamwoodPlanksMossy);
		register(r, LibBlockNames.DREAM_WOOD_FRAMED, dreamwoodFramed);
		register(r, LibBlockNames.DREAM_WOOD_PATTERN_FRAMED, dreamwoodPatternFramed);
		register(r, LibBlockNames.DREAM_WOOD_GLIMMERING, dreamwoodGlimmering);
		register(r, LibBlockNames.SPREADER, manaSpreader);
		register(r, LibBlockNames.SPREADER_REDSTONE, redstoneSpreader);
		register(r, LibBlockNames.SPREADER_ELVEN, elvenSpreader);
		register(r, LibBlockNames.SPREADER_GAIA, gaiaSpreader);
		register(r, LibBlockNames.POOL, manaPool);
		register(r, LibBlockNames.POOL_CREATIVE, creativePool);
		register(r, LibBlockNames.POOL_DILUTED, dilutedPool);
		register(r, LibBlockNames.POOL_FABULOUS, fabulousPool);
		register(r, LibBlockNames.ALCHEMY_CATALYST, alchemyCatalyst);
		register(r, LibBlockNames.CONJURATION_CATALYST, conjurationCatalyst);
		register(r, LibBlockNames.MANASTEEL_BLOCK, manasteelBlock);
		register(r, LibBlockNames.TERRASTEEL_BLOCK, terrasteelBlock);
		register(r, LibBlockNames.ELEMENTIUM_BLOCK, elementiumBlock);
		register(r, LibBlockNames.MANA_DIAMOND_BLOCK, manaDiamondBlock);
		register(r, LibBlockNames.DRAGONSTONE_BLOCK, dragonstoneBlock);
		register(r, LibBlockNames.MANA_GLASS, manaGlass);
		register(r, LibBlockNames.ELF_GLASS, elfGlass);
		register(r, LibBlockNames.BIFROST, bifrost);
		register(r, LibBlockNames.BIFROST_PERM, bifrostPerm);
		register(r, LibBlockNames.RUNE_ALTAR, runeAltar);
		register(r, LibBlockNames.ENCHANTER, enchanter);
		register(r, LibBlockNames.BREWERY, brewery);
		register(r, LibBlockNames.TERRA_PLATE, terraPlate);
		register(r, LibBlockNames.ALF_PORTAL, alfPortal);
		register(r, LibBlockNames.PYLON, manaPylon);
		register(r, LibBlockNames.PYLON_NATURA, naturaPylon);
		register(r, LibBlockNames.PYLON_GAIA, gaiaPylon);
		register(r, LibBlockNames.DISTRIBUTOR, distributor);
		register(r, LibBlockNames.MANA_VOID, manaVoid);
		register(r, LibBlockNames.MANA_DETECTOR, manaDetector);
		register(r, LibBlockNames.PISTON_RELAY, pistonRelay);
		register(r, LibBlockNames.TURNTABLE, turntable);
		register(r, LibBlockNames.TINY_PLANET, tinyPlanet);
		register(r, LibBlockNames.DRUM_WILD, wildDrum);
		register(r, LibBlockNames.DRUM_GATHERING, gatheringDrum);
		register(r, LibBlockNames.DRUM_CANOPY, canopyDrum);
		register(r, LibBlockNames.SPAWNER_CLAW, spawnerClaw);
		register(r, LibBlockNames.FLUXFIELD, rfGenerator);
		register(r, LibBlockNames.PRISM, prism);
		register(r, LibBlockNames.PUMP, pump);
		register(r, LibBlockNames.SPARK_CHANGER, sparkChanger);
		register(r, LibBlockNames.MANA_BOMB, manaBomb);
		register(r, LibBlockNames.BELLOWS, bellows);
		register(r, LibBlockNames.OPEN_CRATE, openCrate);
		register(r, LibBlockNames.CRAFT_CRATE, craftCrate);
		register(r, LibBlockNames.FOREST_EYE, forestEye);
		register(r, LibBlockNames.SOLID_VINE, solidVines);
		register(r, LibBlockNames.PLATFORM_ABSTRUSE, abstrusePlatform);
		register(r, LibBlockNames.PLATFORM_SPECTRAL, spectralPlatform);
		register(r, LibBlockNames.PLATFORM_INFRANGIBLE, infrangiblePlatform);
		register(r, LibBlockNames.TINY_POTATO, tinyPotato);
		register(r, LibBlockNames.ENDER_EYE_BLOCK, enderEye);
		register(r, LibBlockNames.RED_STRING_CONTAINER, redStringContainer);
		register(r, LibBlockNames.RED_STRING_DISPENSER, redStringDispenser);
		register(r, LibBlockNames.RED_STRING_FERTILIZER, redStringFertilizer);
		register(r, LibBlockNames.RED_STRING_COMPARATOR, redStringComparator);
		register(r, LibBlockNames.RED_STRING_RELAY, redStringRelay);
		register(r, LibBlockNames.RED_STRING_INTERCEPTOR, redStringInterceptor);
		register(r, LibBlockNames.CORPOREA_INDEX, corporeaIndex);
		register(r, LibBlockNames.CORPOREA_FUNNEL, corporeaFunnel);
		register(r, LibBlockNames.CORPOREA_INTERCEPTOR, corporeaInterceptor);
		register(r, LibBlockNames.CORPOREA_CRYSTAL_CUBE, corporeaCrystalCube);
		register(r, LibBlockNames.CORPOREA_RETAINER, corporeaRetainer);
		register(r, LibBlockNames.CORPOREA_BLOCK, corporeaBlock);
		register(r, LibBlockNames.CORPOREA_SLAB, corporeaSlab);
		register(r, LibBlockNames.CORPOREA_STAIRS, corporeaStairs);
		register(r, LibBlockNames.CORPOREA_BRICK, corporeaBrick);
		register(r, LibBlockNames.CORPOREA_BRICK + LibBlockNames.SLAB_SUFFIX, corporeaBrickSlab);
		register(r, LibBlockNames.CORPOREA_BRICK + LibBlockNames.STAIR_SUFFIX, corporeaBrickStairs);
		register(r, LibBlockNames.CORPOREA_BRICK + LibBlockNames.WALL_SUFFIX, corporeaBrickWall);
		register(r, LibBlockNames.INCENSE_PLATE, incensePlate);
		register(r, LibBlockNames.HOURGLASS, hourglass);
		register(r, LibBlockNames.GHOST_RAIL, ghostRail);
		register(r, LibBlockNames.LIGHT_RELAY, lightRelayDefault);
		register(r, "detector" + LibBlockNames.LIGHT_RELAY_SUFFIX, lightRelayDetector);
		register(r, "fork" + LibBlockNames.LIGHT_RELAY_SUFFIX, lightRelayFork);
		register(r, "toggle" + LibBlockNames.LIGHT_RELAY_SUFFIX, lightRelayToggle);
		register(r, LibBlockNames.LIGHT_LAUNCHER, lightLauncher);
		register(r, LibBlockNames.CACOPHONIUM, cacophonium);
		register(r, LibBlockNames.CELL_BLOCK, cellBlock);
		register(r, LibBlockNames.TERU_TERU_BOZU, teruTeruBozu);
		register(r, LibBlockNames.AVATAR, avatar);
		register(r, LibBlockNames.FAKE_AIR, fakeAir);
		register(r, LibBlockNames.ROOT, root);
		register(r, LibBlockNames.FEL_PUMPKIN, felPumpkin);
		register(r, LibBlockNames.COCOON, cocoon);
		register(r, LibBlockNames.ENCHANTED_SOIL, enchantedSoil);
		register(r, LibBlockNames.ANIMATED_TORCH, animatedTorch);
		register(r, LibBlockNames.STARFIELD, starfield);
		register(r, LibBlockNames.AZULEJO_PREFIX + 0, azulejo0);
		register(r, LibBlockNames.AZULEJO_PREFIX + 1, azulejo1);
		register(r, LibBlockNames.AZULEJO_PREFIX + 2, azulejo2);
		register(r, LibBlockNames.AZULEJO_PREFIX + 3, azulejo3);
		register(r, LibBlockNames.AZULEJO_PREFIX + 4, azulejo4);
		register(r, LibBlockNames.AZULEJO_PREFIX + 5, azulejo5);
		register(r, LibBlockNames.AZULEJO_PREFIX + 6, azulejo6);
		register(r, LibBlockNames.AZULEJO_PREFIX + 7, azulejo7);
		register(r, LibBlockNames.AZULEJO_PREFIX + 8, azulejo8);
		register(r, LibBlockNames.AZULEJO_PREFIX + 9, azulejo9);
		register(r, LibBlockNames.AZULEJO_PREFIX + 10, azulejo10);
		register(r, LibBlockNames.AZULEJO_PREFIX + 11, azulejo11);
		register(r, LibBlockNames.AZULEJO_PREFIX + 12, azulejo12);
		register(r, LibBlockNames.AZULEJO_PREFIX + 13, azulejo13);
		register(r, LibBlockNames.AZULEJO_PREFIX + 14, azulejo14);
		register(r, LibBlockNames.AZULEJO_PREFIX + 15, azulejo15);
		register(r, LibBlockNames.MANA_FLAME, manaFlame);
		register(r, LibBlockNames.BLAZE_BLOCK, blazeBlock);
		register(r, LibBlockNames.GAIA_WALL_HEAD, gaiaHeadWall);
		register(r, LibBlockNames.GAIA_HEAD, gaiaHead);
		register(r, LibBlockNames.SHIMMERROCK, shimmerrock);
		register(r, LibBlockNames.SHIMMERWOOD_PLANKS, shimmerwoodPlanks);
		register(r, BlockAltGrass.Variant.DRY.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, dryGrass);
		register(r, BlockAltGrass.Variant.GOLDEN.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, goldenGrass);
		register(r, BlockAltGrass.Variant.VIVID.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, vividGrass);
		register(r, BlockAltGrass.Variant.SCORCHED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, scorchedGrass);
		register(r, BlockAltGrass.Variant.INFUSED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, infusedGrass);
		register(r, BlockAltGrass.Variant.MUTATED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, mutatedGrass);
		register(r, LibBlockNames.MOTIF_DAYBLOOM, motifDaybloom);
		register(r, LibBlockNames.MOTIF_NIGHTSHADE, motifNightshade);
		register(r, LibBlockNames.MOTIF_HYDROANGEAS, motifHydroangeas);

		BotaniaAPI.instance().registerHornHarvestableBlock(whiteShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(orangeShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(magentaShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(lightBlueShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(yellowShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(limeShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(pinkShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(grayShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(lightGrayShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(cyanShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(purpleShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(blueShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(brownShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(greenShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(redShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(blackShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);

		BotaniaAPI.instance().registerHornHarvestableBlock(whiteMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(orangeMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(magentaMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(lightBlueMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(yellowMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(limeMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(pinkMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(grayMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(lightGrayMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(cyanMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(purpleMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(blueMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(brownMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(greenMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(redMushroom.getRegistryName(), DefaultHornHarvestable.INSTANCE);
		BotaniaAPI.instance().registerHornHarvestableBlock(blackShinyFlower.getRegistryName(), DefaultHornHarvestable.INSTANCE);
	}

	public static void registerItemBlocks(RegistryEvent.Register<Item> evt) {
		IForgeRegistry<Item> r = evt.getRegistry();
		Item.Properties props = ModItems.defaultBuilder();
		register(r, Registry.BLOCK.getKey(whiteFlower), new BlockItem(whiteFlower, props));
		register(r, Registry.BLOCK.getKey(orangeFlower), new BlockItem(orangeFlower, props));
		register(r, Registry.BLOCK.getKey(magentaFlower), new BlockItem(magentaFlower, props));
		register(r, Registry.BLOCK.getKey(lightBlueFlower), new BlockItem(lightBlueFlower, props));
		register(r, Registry.BLOCK.getKey(yellowFlower), new BlockItem(yellowFlower, props));
		register(r, Registry.BLOCK.getKey(limeFlower), new BlockItem(limeFlower, props));
		register(r, Registry.BLOCK.getKey(pinkFlower), new BlockItem(pinkFlower, props));
		register(r, Registry.BLOCK.getKey(grayFlower), new BlockItem(grayFlower, props));
		register(r, Registry.BLOCK.getKey(lightGrayFlower), new BlockItem(lightGrayFlower, props));
		register(r, Registry.BLOCK.getKey(cyanFlower), new BlockItem(cyanFlower, props));
		register(r, Registry.BLOCK.getKey(purpleFlower), new BlockItem(purpleFlower, props));
		register(r, Registry.BLOCK.getKey(blueFlower), new BlockItem(blueFlower, props));
		register(r, Registry.BLOCK.getKey(brownFlower), new BlockItem(brownFlower, props));
		register(r, Registry.BLOCK.getKey(greenFlower), new BlockItem(greenFlower, props));
		register(r, Registry.BLOCK.getKey(redFlower), new BlockItem(redFlower, props));
		register(r, Registry.BLOCK.getKey(blackFlower), new BlockItem(blackFlower, props));
		register(r, Registry.BLOCK.getKey(whiteShinyFlower), new BlockItem(whiteShinyFlower, props));
		register(r, Registry.BLOCK.getKey(orangeShinyFlower), new BlockItem(orangeShinyFlower, props));
		register(r, Registry.BLOCK.getKey(magentaShinyFlower), new BlockItem(magentaShinyFlower, props));
		register(r, Registry.BLOCK.getKey(lightBlueShinyFlower), new BlockItem(lightBlueShinyFlower, props));
		register(r, Registry.BLOCK.getKey(yellowShinyFlower), new BlockItem(yellowShinyFlower, props));
		register(r, Registry.BLOCK.getKey(limeShinyFlower), new BlockItem(limeShinyFlower, props));
		register(r, Registry.BLOCK.getKey(pinkShinyFlower), new BlockItem(pinkShinyFlower, props));
		register(r, Registry.BLOCK.getKey(grayShinyFlower), new BlockItem(grayShinyFlower, props));
		register(r, Registry.BLOCK.getKey(lightGrayShinyFlower), new BlockItem(lightGrayShinyFlower, props));
		register(r, Registry.BLOCK.getKey(cyanShinyFlower), new BlockItem(cyanShinyFlower, props));
		register(r, Registry.BLOCK.getKey(purpleShinyFlower), new BlockItem(purpleShinyFlower, props));
		register(r, Registry.BLOCK.getKey(blueShinyFlower), new BlockItem(blueShinyFlower, props));
		register(r, Registry.BLOCK.getKey(brownShinyFlower), new BlockItem(brownShinyFlower, props));
		register(r, Registry.BLOCK.getKey(greenShinyFlower), new BlockItem(greenShinyFlower, props));
		register(r, Registry.BLOCK.getKey(redShinyFlower), new BlockItem(redShinyFlower, props));
		register(r, Registry.BLOCK.getKey(blackShinyFlower), new BlockItem(blackShinyFlower, props));
		register(r, Registry.BLOCK.getKey(whiteFloatingFlower), new BlockItem(whiteFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(orangeFloatingFlower), new BlockItem(orangeFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(magentaFloatingFlower), new BlockItem(magentaFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(lightBlueFloatingFlower), new BlockItem(lightBlueFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(yellowFloatingFlower), new BlockItem(yellowFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(limeFloatingFlower), new BlockItem(limeFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(pinkFloatingFlower), new BlockItem(pinkFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(grayFloatingFlower), new BlockItem(grayFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(lightGrayFloatingFlower), new BlockItem(lightGrayFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(cyanFloatingFlower), new BlockItem(cyanFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(purpleFloatingFlower), new BlockItem(purpleFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(blueFloatingFlower), new BlockItem(blueFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(brownFloatingFlower), new BlockItem(brownFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(greenFloatingFlower), new BlockItem(greenFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(redFloatingFlower), new BlockItem(redFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(blackFloatingFlower), new BlockItem(blackFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(petalBlockWhite), new BlockItem(petalBlockWhite, props));
		register(r, Registry.BLOCK.getKey(petalBlockOrange), new BlockItem(petalBlockOrange, props));
		register(r, Registry.BLOCK.getKey(petalBlockMagenta), new BlockItem(petalBlockMagenta, props));
		register(r, Registry.BLOCK.getKey(petalBlockLightBlue), new BlockItem(petalBlockLightBlue, props));
		register(r, Registry.BLOCK.getKey(petalBlockYellow), new BlockItem(petalBlockYellow, props));
		register(r, Registry.BLOCK.getKey(petalBlockLime), new BlockItem(petalBlockLime, props));
		register(r, Registry.BLOCK.getKey(petalBlockPink), new BlockItem(petalBlockPink, props));
		register(r, Registry.BLOCK.getKey(petalBlockGray), new BlockItem(petalBlockGray, props));
		register(r, Registry.BLOCK.getKey(petalBlockSilver), new BlockItem(petalBlockSilver, props));
		register(r, Registry.BLOCK.getKey(petalBlockCyan), new BlockItem(petalBlockCyan, props));
		register(r, Registry.BLOCK.getKey(petalBlockPurple), new BlockItem(petalBlockPurple, props));
		register(r, Registry.BLOCK.getKey(petalBlockBlue), new BlockItem(petalBlockBlue, props));
		register(r, Registry.BLOCK.getKey(petalBlockBrown), new BlockItem(petalBlockBrown, props));
		register(r, Registry.BLOCK.getKey(petalBlockGreen), new BlockItem(petalBlockGreen, props));
		register(r, Registry.BLOCK.getKey(petalBlockRed), new BlockItem(petalBlockRed, props));
		register(r, Registry.BLOCK.getKey(petalBlockBlack), new BlockItem(petalBlockBlack, props));
		register(r, Registry.BLOCK.getKey(whiteMushroom), new BlockItem(whiteMushroom, props));
		register(r, Registry.BLOCK.getKey(orangeMushroom), new BlockItem(orangeMushroom, props));
		register(r, Registry.BLOCK.getKey(magentaMushroom), new BlockItem(magentaMushroom, props));
		register(r, Registry.BLOCK.getKey(lightBlueMushroom), new BlockItem(lightBlueMushroom, props));
		register(r, Registry.BLOCK.getKey(yellowMushroom), new BlockItem(yellowMushroom, props));
		register(r, Registry.BLOCK.getKey(limeMushroom), new BlockItem(limeMushroom, props));
		register(r, Registry.BLOCK.getKey(pinkMushroom), new BlockItem(pinkMushroom, props));
		register(r, Registry.BLOCK.getKey(grayMushroom), new BlockItem(grayMushroom, props));
		register(r, Registry.BLOCK.getKey(lightGrayMushroom), new BlockItem(lightGrayMushroom, props));
		register(r, Registry.BLOCK.getKey(cyanMushroom), new BlockItem(cyanMushroom, props));
		register(r, Registry.BLOCK.getKey(purpleMushroom), new BlockItem(purpleMushroom, props));
		register(r, Registry.BLOCK.getKey(blueMushroom), new BlockItem(blueMushroom, props));
		register(r, Registry.BLOCK.getKey(brownMushroom), new BlockItem(brownMushroom, props));
		register(r, Registry.BLOCK.getKey(greenMushroom), new BlockItem(greenMushroom, props));
		register(r, Registry.BLOCK.getKey(redMushroom), new BlockItem(redMushroom, props));
		register(r, Registry.BLOCK.getKey(blackMushroom), new BlockItem(blackMushroom, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerWhite), new BlockItem(doubleFlowerWhite, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerOrange), new BlockItem(doubleFlowerOrange, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerMagenta), new BlockItem(doubleFlowerMagenta, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerLightBlue), new BlockItem(doubleFlowerLightBlue, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerYellow), new BlockItem(doubleFlowerYellow, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerLime), new BlockItem(doubleFlowerLime, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerPink), new BlockItem(doubleFlowerPink, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerGray), new BlockItem(doubleFlowerGray, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerLightGray), new BlockItem(doubleFlowerLightGray, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerCyan), new BlockItem(doubleFlowerCyan, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerPurple), new BlockItem(doubleFlowerPurple, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerBlue), new BlockItem(doubleFlowerBlue, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerBrown), new BlockItem(doubleFlowerBrown, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerGreen), new BlockItem(doubleFlowerGreen, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerRed), new BlockItem(doubleFlowerRed, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerBlack), new BlockItem(doubleFlowerBlack, props));
		register(r, Registry.BLOCK.getKey(defaultAltar), new BlockItem(defaultAltar, props));
		register(r, Registry.BLOCK.getKey(forestAltar), new BlockItem(forestAltar, props));
		register(r, Registry.BLOCK.getKey(plainsAltar), new BlockItem(plainsAltar, props));
		register(r, Registry.BLOCK.getKey(mountainAltar), new BlockItem(mountainAltar, props));
		register(r, Registry.BLOCK.getKey(fungalAltar), new BlockItem(fungalAltar, props));
		register(r, Registry.BLOCK.getKey(swampAltar), new BlockItem(swampAltar, props));
		register(r, Registry.BLOCK.getKey(desertAltar), new BlockItem(desertAltar, props));
		register(r, Registry.BLOCK.getKey(taigaAltar), new BlockItem(taigaAltar, props));
		register(r, Registry.BLOCK.getKey(mesaAltar), new BlockItem(mesaAltar, props));
		register(r, Registry.BLOCK.getKey(mossyAltar), new BlockItem(mossyAltar, props));
		register(r, Registry.BLOCK.getKey(livingrock), new BlockItem(livingrock, props));
		register(r, Registry.BLOCK.getKey(livingrockBrick), new BlockItem(livingrockBrick, props));
		register(r, Registry.BLOCK.getKey(livingrockBrickChiseled), new BlockItem(livingrockBrickChiseled, props));
		register(r, Registry.BLOCK.getKey(livingrockBrickCracked), new BlockItem(livingrockBrickCracked, props));
		register(r, Registry.BLOCK.getKey(livingrockBrickMossy), new BlockItem(livingrockBrickMossy, props));
		register(r, Registry.BLOCK.getKey(livingwood), new BlockItem(livingwood, props));
		register(r, Registry.BLOCK.getKey(livingwoodPlanks), new BlockItem(livingwoodPlanks, props));
		register(r, Registry.BLOCK.getKey(livingwoodPlanksMossy), new BlockItem(livingwoodPlanksMossy, props));
		register(r, Registry.BLOCK.getKey(livingwoodFramed), new BlockItem(livingwoodFramed, props));
		register(r, Registry.BLOCK.getKey(livingwoodPatternFramed), new BlockItem(livingwoodPatternFramed, props));
		register(r, Registry.BLOCK.getKey(livingwoodGlimmering), new BlockItem(livingwoodGlimmering, props));
		register(r, Registry.BLOCK.getKey(dreamwood), new ItemBlockDreamwood(dreamwood, props));
		register(r, Registry.BLOCK.getKey(dreamwoodPlanks), new ItemBlockDreamwood(dreamwoodPlanks, props));
		register(r, Registry.BLOCK.getKey(dreamwoodPlanksMossy), new ItemBlockDreamwood(dreamwoodPlanksMossy, props));
		register(r, Registry.BLOCK.getKey(dreamwoodFramed), new ItemBlockDreamwood(dreamwoodFramed, props));
		register(r, Registry.BLOCK.getKey(dreamwoodPatternFramed), new ItemBlockDreamwood(dreamwoodPatternFramed, props));
		register(r, Registry.BLOCK.getKey(dreamwoodGlimmering), new ItemBlockDreamwood(dreamwoodGlimmering, props));
		register(r, Registry.BLOCK.getKey(manaSpreader), new BlockItem(manaSpreader, props));
		register(r, Registry.BLOCK.getKey(redstoneSpreader), new BlockItem(redstoneSpreader, props));
		register(r, Registry.BLOCK.getKey(elvenSpreader), new BlockItem(elvenSpreader, props));
		register(r, Registry.BLOCK.getKey(gaiaSpreader), new BlockItem(gaiaSpreader, props));
		register(r, Registry.BLOCK.getKey(manaPool), new ItemBlockPool(manaPool, props));
		register(r, Registry.BLOCK.getKey(creativePool), new ItemBlockPool(creativePool, ModItems.defaultBuilder().rarity(Rarity.EPIC)));
		register(r, Registry.BLOCK.getKey(dilutedPool), new ItemBlockPool(dilutedPool, props));
		register(r, Registry.BLOCK.getKey(fabulousPool), new ItemBlockPool(fabulousPool, props));
		register(r, Registry.BLOCK.getKey(alchemyCatalyst), new BlockItem(alchemyCatalyst, props));
		register(r, Registry.BLOCK.getKey(conjurationCatalyst), new BlockItem(conjurationCatalyst, props));
		register(r, Registry.BLOCK.getKey(manasteelBlock), new BlockItem(manasteelBlock, props));
		register(r, Registry.BLOCK.getKey(terrasteelBlock), new BlockItem(terrasteelBlock, ModItems.defaultBuilder().rarity(Rarity.UNCOMMON)));
		register(r, Registry.BLOCK.getKey(elementiumBlock), new ItemBlockElven(elementiumBlock, props));
		register(r, Registry.BLOCK.getKey(manaDiamondBlock), new BlockItem(manaDiamondBlock, props));
		register(r, Registry.BLOCK.getKey(dragonstoneBlock), new BlockItem(dragonstoneBlock, props));
		register(r, Registry.BLOCK.getKey(manaGlass), new BlockItem(manaGlass, props));
		register(r, Registry.BLOCK.getKey(elfGlass), new ItemBlockElven(elfGlass, props));
		register(r, Registry.BLOCK.getKey(bifrostPerm), new BlockItem(bifrostPerm, props));
		register(r, Registry.BLOCK.getKey(runeAltar), new BlockItem(runeAltar, props));
		register(r, Registry.BLOCK.getKey(enchanter), new BlockItem(enchanter, props));
		register(r, Registry.BLOCK.getKey(brewery), new BlockItem(brewery, Botania.proxy.propertiesWithRenderer(ModItems.defaultBuilder(), brewery)));
		register(r, Registry.BLOCK.getKey(terraPlate), new BlockItem(terraPlate, props));
		register(r, Registry.BLOCK.getKey(alfPortal), new BlockItem(alfPortal, ModItems.defaultBuilder().rarity(Rarity.UNCOMMON)));

		Item.Properties pylonProps = Botania.proxy.propertiesWithRenderer(ModItems.defaultBuilder(), manaPylon);
		register(r, Registry.BLOCK.getKey(manaPylon), new BlockItem(manaPylon, pylonProps));
		register(r, Registry.BLOCK.getKey(naturaPylon), new BlockItem(naturaPylon, pylonProps));
		register(r, Registry.BLOCK.getKey(gaiaPylon), new BlockItem(gaiaPylon, pylonProps));
		register(r, Registry.BLOCK.getKey(distributor), new BlockItem(distributor, props));
		register(r, Registry.BLOCK.getKey(manaVoid), new BlockItem(manaVoid, props));
		register(r, Registry.BLOCK.getKey(manaDetector), new BlockItem(manaDetector, props));
		register(r, Registry.BLOCK.getKey(pistonRelay), new BlockItem(pistonRelay, props));
		register(r, Registry.BLOCK.getKey(turntable), new BlockItem(turntable, props));
		register(r, Registry.BLOCK.getKey(tinyPlanet), new BlockItem(tinyPlanet, props));
		register(r, Registry.BLOCK.getKey(wildDrum), new BlockItem(wildDrum, props));
		register(r, Registry.BLOCK.getKey(gatheringDrum), new BlockItem(gatheringDrum, props));
		register(r, Registry.BLOCK.getKey(canopyDrum), new BlockItem(canopyDrum, props));
		register(r, Registry.BLOCK.getKey(spawnerClaw), new BlockItem(spawnerClaw, props));
		register(r, Registry.BLOCK.getKey(rfGenerator), new BlockItem(rfGenerator, props));
		register(r, Registry.BLOCK.getKey(prism), new BlockItem(prism, props));
		register(r, Registry.BLOCK.getKey(pump), new BlockItem(pump, props));
		register(r, Registry.BLOCK.getKey(sparkChanger), new BlockItem(sparkChanger, props));
		register(r, Registry.BLOCK.getKey(manaBomb), new BlockItem(manaBomb, props));
		register(r, Registry.BLOCK.getKey(bellows), new BlockItem(bellows, Botania.proxy.propertiesWithRenderer(ModItems.defaultBuilder(), bellows)));
		register(r, Registry.BLOCK.getKey(openCrate), new BlockItem(openCrate, props));
		register(r, Registry.BLOCK.getKey(craftCrate), new BlockItem(craftCrate, props));
		register(r, Registry.BLOCK.getKey(forestEye), new BlockItem(forestEye, props));
		register(r, Registry.BLOCK.getKey(abstrusePlatform), new BlockItem(abstrusePlatform, props));
		register(r, Registry.BLOCK.getKey(spectralPlatform), new BlockItem(spectralPlatform, props));
		register(r, Registry.BLOCK.getKey(infrangiblePlatform), new BlockItem(infrangiblePlatform, ModItems.defaultBuilder().rarity(Rarity.EPIC)));
		register(r, Registry.BLOCK.getKey(tinyPotato), new ItemBlockTinyPotato(tinyPotato, ModItems.defaultBuilder().rarity(Rarity.UNCOMMON)));
		register(r, Registry.BLOCK.getKey(enderEye), new BlockItem(enderEye, props));
		register(r, Registry.BLOCK.getKey(redStringContainer), new BlockItem(redStringContainer, props));
		register(r, Registry.BLOCK.getKey(redStringDispenser), new BlockItem(redStringDispenser, props));
		register(r, Registry.BLOCK.getKey(redStringFertilizer), new BlockItem(redStringFertilizer, props));
		register(r, Registry.BLOCK.getKey(redStringComparator), new BlockItem(redStringComparator, props));
		register(r, Registry.BLOCK.getKey(redStringRelay), new BlockItem(redStringRelay, props));
		register(r, Registry.BLOCK.getKey(redStringInterceptor), new BlockItem(redStringInterceptor, props));
		register(r, Registry.BLOCK.getKey(corporeaIndex), new BlockItem(corporeaIndex, Botania.proxy.propertiesWithRenderer(ModItems.defaultBuilder(), corporeaIndex)));
		register(r, Registry.BLOCK.getKey(corporeaFunnel), new BlockItem(corporeaFunnel, props));
		register(r, Registry.BLOCK.getKey(corporeaInterceptor), new BlockItem(corporeaInterceptor, props));
		register(r, Registry.BLOCK.getKey(corporeaCrystalCube), new BlockItem(corporeaCrystalCube, props));
		register(r, Registry.BLOCK.getKey(corporeaRetainer), new BlockItem(corporeaRetainer, props));
		register(r, Registry.BLOCK.getKey(corporeaBlock), new BlockItem(corporeaBlock, props));
		register(r, Registry.BLOCK.getKey(corporeaSlab), new BlockItem(corporeaSlab, props));
		register(r, Registry.BLOCK.getKey(corporeaStairs), new BlockItem(corporeaStairs, props));
		register(r, Registry.BLOCK.getKey(corporeaBrick), new BlockItem(corporeaBrick, props));
		register(r, Registry.BLOCK.getKey(corporeaBrickSlab), new BlockItem(corporeaBrickSlab, props));
		register(r, Registry.BLOCK.getKey(corporeaBrickStairs), new BlockItem(corporeaBrickStairs, props));
		register(r, Registry.BLOCK.getKey(corporeaBrickWall), new BlockItem(corporeaBrickWall, props));
		register(r, Registry.BLOCK.getKey(incensePlate), new BlockItem(incensePlate, props));
		register(r, Registry.BLOCK.getKey(hourglass), new BlockItem(hourglass, Botania.proxy.propertiesWithRenderer(ModItems.defaultBuilder(), hourglass)));
		register(r, Registry.BLOCK.getKey(ghostRail), new BlockItem(ghostRail, props));
		register(r, Registry.BLOCK.getKey(lightRelayDefault), new BlockItem(lightRelayDefault, props));
		register(r, Registry.BLOCK.getKey(lightRelayDetector), new BlockItem(lightRelayDetector, props));
		register(r, Registry.BLOCK.getKey(lightRelayFork), new BlockItem(lightRelayFork, props));
		register(r, Registry.BLOCK.getKey(lightRelayToggle), new BlockItem(lightRelayToggle, props));
		register(r, Registry.BLOCK.getKey(lightLauncher), new BlockItem(lightLauncher, props));
		register(r, Registry.BLOCK.getKey(cacophonium), new BlockItem(cacophonium, props));
		register(r, Registry.BLOCK.getKey(cellBlock), new BlockItem(cellBlock, props));
		register(r, Registry.BLOCK.getKey(teruTeruBozu), new BlockItem(teruTeruBozu, Botania.proxy.propertiesWithRenderer(ModItems.defaultBuilder(), teruTeruBozu)));
		register(r, Registry.BLOCK.getKey(avatar), new BlockItem(avatar, Botania.proxy.propertiesWithRenderer(ModItems.defaultBuilder(), avatar)));
		register(r, Registry.BLOCK.getKey(root), new BlockItem(root, props));
		register(r, Registry.BLOCK.getKey(felPumpkin), new BlockItem(felPumpkin, props));
		register(r, Registry.BLOCK.getKey(cocoon), new BlockItem(cocoon, props));
		register(r, Registry.BLOCK.getKey(enchantedSoil), new BlockItem(enchantedSoil, ModItems.defaultBuilder().rarity(Rarity.RARE)));
		register(r, Registry.BLOCK.getKey(animatedTorch), new BlockItem(animatedTorch, props));
		register(r, Registry.BLOCK.getKey(starfield), new BlockItem(starfield, props));
		register(r, Registry.BLOCK.getKey(azulejo0), new BlockItem(azulejo0, props));
		register(r, Registry.BLOCK.getKey(azulejo1), new BlockItem(azulejo1, props));
		register(r, Registry.BLOCK.getKey(azulejo2), new BlockItem(azulejo2, props));
		register(r, Registry.BLOCK.getKey(azulejo3), new BlockItem(azulejo3, props));
		register(r, Registry.BLOCK.getKey(azulejo4), new BlockItem(azulejo4, props));
		register(r, Registry.BLOCK.getKey(azulejo5), new BlockItem(azulejo5, props));
		register(r, Registry.BLOCK.getKey(azulejo6), new BlockItem(azulejo6, props));
		register(r, Registry.BLOCK.getKey(azulejo7), new BlockItem(azulejo7, props));
		register(r, Registry.BLOCK.getKey(azulejo8), new BlockItem(azulejo8, props));
		register(r, Registry.BLOCK.getKey(azulejo9), new BlockItem(azulejo9, props));
		register(r, Registry.BLOCK.getKey(azulejo10), new BlockItem(azulejo10, props));
		register(r, Registry.BLOCK.getKey(azulejo11), new BlockItem(azulejo11, props));
		register(r, Registry.BLOCK.getKey(azulejo12), new BlockItem(azulejo12, props));
		register(r, Registry.BLOCK.getKey(azulejo13), new BlockItem(azulejo13, props));
		register(r, Registry.BLOCK.getKey(azulejo14), new BlockItem(azulejo14, props));
		register(r, Registry.BLOCK.getKey(azulejo15), new BlockItem(azulejo15, props));
		register(r, Registry.BLOCK.getKey(blazeBlock), new ItemBlockBlaze(blazeBlock, props));
		register(r, Registry.BLOCK.getKey(gaiaHead), new WallOrFloorItem(gaiaHead, gaiaHeadWall, ModItems.defaultBuilder().rarity(Rarity.UNCOMMON)));
		register(r, Registry.BLOCK.getKey(shimmerrock), new BlockItem(shimmerrock, props));
		register(r, Registry.BLOCK.getKey(shimmerwoodPlanks), new BlockItem(shimmerwoodPlanks, props));
		register(r, Registry.BLOCK.getKey(dryGrass), new BlockItem(dryGrass, props));
		register(r, Registry.BLOCK.getKey(goldenGrass), new BlockItem(goldenGrass, props));
		register(r, Registry.BLOCK.getKey(vividGrass), new BlockItem(vividGrass, props));
		register(r, Registry.BLOCK.getKey(scorchedGrass), new BlockItem(scorchedGrass, props));
		register(r, Registry.BLOCK.getKey(infusedGrass), new BlockItem(infusedGrass, props));
		register(r, Registry.BLOCK.getKey(mutatedGrass), new BlockItem(mutatedGrass, props));
		register(r, Registry.BLOCK.getKey(motifDaybloom), new BlockItem(motifDaybloom, props));
		register(r, Registry.BLOCK.getKey(motifNightshade), new BlockItem(motifNightshade, props));
		register(r, Registry.BLOCK.getKey(motifHydroangeas), new BlockItem(motifHydroangeas, props));
	}

	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, ResourceLocation name, IForgeRegistryEntry<V> thing) {
		reg.register(thing.setRegistryName(name));
	}

	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, String name, IForgeRegistryEntry<V> thing) {
		register(reg, prefix(name), thing);
	}

	public static void addDispenserBehaviours() {
		DispenserBlock.registerDispenseBehavior(ModItems.twigWand, new BehaviourWand());
		DispenserBlock.registerDispenseBehavior(ModItems.obedienceStick, new BehaviourStick());
		DispenserBlock.registerDispenseBehavior(ModItems.poolMinecart, new BehaviourPoolMinecart());
		DispenserBlock.registerDispenseBehavior(ModBlocks.felPumpkin, new BehaviourFelPumpkin());
		DispenserBlock.registerDispenseBehavior(ModItems.spark, new BehaviourManaSpark());
		DispenserBlock.registerDispenseBehavior(ModBlocks.gaiaHead, new OptionalDispenseBehavior() {
			@Nonnull
			@Override
			protected ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
				setSuccessful(ArmorItem.func_226626_a_(source, stack));
				return stack;
			}
		});

		IDispenseItemBehavior behavior = new BehaviourCorporeaSpark();
		DispenserBlock.registerDispenseBehavior(ModItems.corporeaSpark, behavior);
		DispenserBlock.registerDispenseBehavior(ModItems.corporeaSparkMaster, behavior);
		DispenserBlock.registerDispenseBehavior(ModItems.enderAirBottle, new ProjectileDispenseBehavior() {
			@Nonnull
			@Override
			protected ProjectileEntity getProjectileEntity(@Nonnull World world, @Nonnull IPosition pos, @Nonnull ItemStack stack) {
				return new EntityEnderAirBottle(pos.getX(), pos.getY(), pos.getZ(), world);
			}
		});

		behavior = AccessorDispenserBlock.getDispenseBehaviorRegistry().get(Items.GLASS_BOTTLE);
		DispenserBlock.registerDispenseBehavior(Items.GLASS_BOTTLE, new BehaviourEnderAirBottling(behavior));

		DispenserBlock.registerDispenseBehavior(ModItems.manasteelShears, new BeehiveDispenseBehavior());
		DispenserBlock.registerDispenseBehavior(ModItems.elementiumShears, new BeehiveDispenseBehavior());
		DispenserBlock.registerDispenseBehavior(ModItems.vineBall, new ProjectileDispenseBehavior() {
			@Nonnull
			@Override
			protected ProjectileEntity getProjectileEntity(@Nonnull World world, @Nonnull IPosition pos, @Nonnull ItemStack stack) {
				return new EntityVineBall(pos.getX(), pos.getY(), pos.getZ(), world);
			}
		});

		SeedBehaviours.init();
	}

	public static Block getFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteFlower;
		case ORANGE:
			return orangeFlower;
		case MAGENTA:
			return magentaFlower;
		case LIGHT_BLUE:
			return lightBlueFlower;
		case YELLOW:
			return yellowFlower;
		case LIME:
			return limeFlower;
		case PINK:
			return pinkFlower;
		case GRAY:
			return grayFlower;
		case LIGHT_GRAY:
			return lightGrayFlower;
		case CYAN:
			return cyanFlower;
		case PURPLE:
			return purpleFlower;
		case BLUE:
			return blueFlower;
		case BROWN:
			return brownFlower;
		case GREEN:
			return greenFlower;
		case RED:
			return redFlower;
		case BLACK:
			return blackFlower;
		}
	}

	public static Block getMushroom(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteMushroom;
		case ORANGE:
			return orangeMushroom;
		case MAGENTA:
			return magentaMushroom;
		case LIGHT_BLUE:
			return lightBlueMushroom;
		case YELLOW:
			return yellowMushroom;
		case LIME:
			return limeMushroom;
		case PINK:
			return pinkMushroom;
		case GRAY:
			return grayMushroom;
		case LIGHT_GRAY:
			return lightGrayMushroom;
		case CYAN:
			return cyanMushroom;
		case PURPLE:
			return purpleMushroom;
		case BLUE:
			return blueMushroom;
		case BROWN:
			return brownMushroom;
		case GREEN:
			return greenMushroom;
		case RED:
			return redMushroom;
		case BLACK:
			return blackMushroom;
		}
	}

	public static Block getBuriedPetal(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteBuriedPetals;
		case ORANGE:
			return orangeBuriedPetals;
		case MAGENTA:
			return magentaBuriedPetals;
		case LIGHT_BLUE:
			return lightBlueBuriedPetals;
		case YELLOW:
			return yellowBuriedPetals;
		case LIME:
			return limeBuriedPetals;
		case PINK:
			return pinkBuriedPetals;
		case GRAY:
			return grayBuriedPetals;
		case LIGHT_GRAY:
			return lightGrayBuriedPetals;
		case CYAN:
			return cyanBuriedPetals;
		case PURPLE:
			return purpleBuriedPetals;
		case BLUE:
			return blueBuriedPetals;
		case BROWN:
			return brownBuriedPetals;
		case GREEN:
			return greenBuriedPetals;
		case RED:
			return redBuriedPetals;
		case BLACK:
			return blackBuriedPetals;
		}
	}

	public static Block getShinyFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteShinyFlower;
		case ORANGE:
			return orangeShinyFlower;
		case MAGENTA:
			return magentaShinyFlower;
		case LIGHT_BLUE:
			return lightBlueShinyFlower;
		case YELLOW:
			return yellowShinyFlower;
		case LIME:
			return limeShinyFlower;
		case PINK:
			return pinkShinyFlower;
		case GRAY:
			return grayShinyFlower;
		case LIGHT_GRAY:
			return lightGrayShinyFlower;
		case CYAN:
			return cyanShinyFlower;
		case PURPLE:
			return purpleShinyFlower;
		case BLUE:
			return blueShinyFlower;
		case BROWN:
			return brownShinyFlower;
		case GREEN:
			return greenShinyFlower;
		case RED:
			return redShinyFlower;
		case BLACK:
			return blackShinyFlower;
		}
	}

	public static Block getFloatingFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteFloatingFlower;
		case ORANGE:
			return orangeFloatingFlower;
		case MAGENTA:
			return magentaFloatingFlower;
		case LIGHT_BLUE:
			return lightBlueFloatingFlower;
		case YELLOW:
			return yellowFloatingFlower;
		case LIME:
			return limeFloatingFlower;
		case PINK:
			return pinkFloatingFlower;
		case GRAY:
			return grayFloatingFlower;
		case LIGHT_GRAY:
			return lightGrayFloatingFlower;
		case CYAN:
			return cyanFloatingFlower;
		case PURPLE:
			return purpleFloatingFlower;
		case BLUE:
			return blueFloatingFlower;
		case BROWN:
			return brownFloatingFlower;
		case GREEN:
			return greenFloatingFlower;
		case RED:
			return redFloatingFlower;
		case BLACK:
			return blackFloatingFlower;
		}
	}

	public static Block getDoubleFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return doubleFlowerWhite;
		case ORANGE:
			return doubleFlowerOrange;
		case MAGENTA:
			return doubleFlowerMagenta;
		case LIGHT_BLUE:
			return doubleFlowerLightBlue;
		case YELLOW:
			return doubleFlowerYellow;
		case LIME:
			return doubleFlowerLime;
		case PINK:
			return doubleFlowerPink;
		case GRAY:
			return doubleFlowerGray;
		case LIGHT_GRAY:
			return doubleFlowerLightGray;
		case CYAN:
			return doubleFlowerCyan;
		case PURPLE:
			return doubleFlowerPurple;
		case BLUE:
			return doubleFlowerBlue;
		case BROWN:
			return doubleFlowerBrown;
		case GREEN:
			return doubleFlowerGreen;
		case RED:
			return doubleFlowerRed;
		case BLACK:
			return doubleFlowerBlack;
		}
	}

	public static Block getPetalBlock(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return petalBlockWhite;
		case ORANGE:
			return petalBlockOrange;
		case MAGENTA:
			return petalBlockMagenta;
		case LIGHT_BLUE:
			return petalBlockLightBlue;
		case YELLOW:
			return petalBlockYellow;
		case LIME:
			return petalBlockLime;
		case PINK:
			return petalBlockPink;
		case GRAY:
			return petalBlockGray;
		case LIGHT_GRAY:
			return petalBlockSilver;
		case CYAN:
			return petalBlockCyan;
		case PURPLE:
			return petalBlockPurple;
		case BLUE:
			return petalBlockBlue;
		case BROWN:
			return petalBlockBrown;
		case GREEN:
			return petalBlockGreen;
		case RED:
			return petalBlockRed;
		case BLACK:
			return petalBlockBlack;
		}
	}
}
