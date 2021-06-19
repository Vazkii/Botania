/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.IForgeRegistry;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.IAncientWillContainer;
import vazkii.botania.api.item.IFloatingFlower;
import vazkii.botania.api.mana.spark.SparkUpgradeType;
import vazkii.botania.api.state.enums.CratePattern;
import vazkii.botania.client.gui.bag.ContainerFlowerBag;
import vazkii.botania.client.gui.bag.GuiFlowerBag;
import vazkii.botania.client.gui.box.ContainerBaubleBox;
import vazkii.botania.client.gui.box.GuiBaubleBox;
import vazkii.botania.common.block.ModBanners;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.BotaniaCreativeTab;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.crafting.FluxfieldCondition;
import vazkii.botania.common.crafting.FuzzyNBTIngredient;
import vazkii.botania.common.crafting.recipe.*;
import vazkii.botania.common.item.brew.ItemBrewBase;
import vazkii.botania.common.item.brew.ItemIncenseStick;
import vazkii.botania.common.item.brew.ItemVial;
import vazkii.botania.common.item.equipment.armor.elementium.ItemElementiumBoots;
import vazkii.botania.common.item.equipment.armor.elementium.ItemElementiumChest;
import vazkii.botania.common.item.equipment.armor.elementium.ItemElementiumHelm;
import vazkii.botania.common.item.equipment.armor.elementium.ItemElementiumLegs;
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor;
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelHelm;
import vazkii.botania.common.item.equipment.armor.manaweave.ItemManaweaveArmor;
import vazkii.botania.common.item.equipment.armor.manaweave.ItemManaweaveHelm;
import vazkii.botania.common.item.equipment.armor.terrasteel.ItemTerrasteelArmor;
import vazkii.botania.common.item.equipment.armor.terrasteel.ItemTerrasteelHelm;
import vazkii.botania.common.item.equipment.bauble.*;
import vazkii.botania.common.item.equipment.tool.ItemEnderDagger;
import vazkii.botania.common.item.equipment.tool.ItemGlassPick;
import vazkii.botania.common.item.equipment.tool.ItemStarSword;
import vazkii.botania.common.item.equipment.tool.ItemThunderSword;
import vazkii.botania.common.item.equipment.tool.bow.ItemCrystalBow;
import vazkii.botania.common.item.equipment.tool.bow.ItemLivingwoodBow;
import vazkii.botania.common.item.equipment.tool.elementium.*;
import vazkii.botania.common.item.equipment.tool.manasteel.*;
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraAxe;
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraPick;
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraSword;
import vazkii.botania.common.item.lens.*;
import vazkii.botania.common.item.material.*;
import vazkii.botania.common.item.record.ItemModRecord;
import vazkii.botania.common.item.relic.*;
import vazkii.botania.common.item.rod.*;
import vazkii.botania.common.lib.LibItemNames;

import java.util.Locale;

import static vazkii.botania.common.block.ModBlocks.register;
import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public final class ModItems {
	public static final ItemLexicon lexicon = new ItemLexicon(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item twigWand = new ItemTwigWand(unstackable().rarity(Rarity.RARE));
	public static final Item obedienceStick = new ItemObedienceStick(unstackable());
	public static final Item pestleAndMortar = new ItemSelfReturning(unstackable());
	public static final Item fertilizer = new ItemFertilizer(defaultBuilder());

	public static final Item whitePetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.WHITE), DyeColor.WHITE, defaultBuilder());
	public static final Item orangePetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.ORANGE), DyeColor.ORANGE, defaultBuilder());
	public static final Item magentaPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.MAGENTA), DyeColor.MAGENTA, defaultBuilder());
	public static final Item lightBluePetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.LIGHT_BLUE), DyeColor.LIGHT_BLUE, defaultBuilder());
	public static final Item yellowPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.YELLOW), DyeColor.YELLOW, defaultBuilder());
	public static final Item limePetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.LIME), DyeColor.LIME, defaultBuilder());
	public static final Item pinkPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.PINK), DyeColor.PINK, defaultBuilder());
	public static final Item grayPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.GRAY), DyeColor.GRAY, defaultBuilder());
	public static final Item lightGrayPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.LIGHT_GRAY), DyeColor.LIGHT_GRAY, defaultBuilder());
	public static final Item cyanPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.CYAN), DyeColor.CYAN, defaultBuilder());
	public static final Item purplePetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.PURPLE), DyeColor.PURPLE, defaultBuilder());
	public static final Item bluePetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.BLUE), DyeColor.BLUE, defaultBuilder());
	public static final Item brownPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.BROWN), DyeColor.BROWN, defaultBuilder());
	public static final Item greenPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.GREEN), DyeColor.GREEN, defaultBuilder());
	public static final Item redPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.RED), DyeColor.RED, defaultBuilder());
	public static final Item blackPetal = new ItemPetal(ModBlocks.getBuriedPetal(DyeColor.BLACK), DyeColor.BLACK, defaultBuilder());

	public static final Item manaSteel = new Item(defaultBuilder());
	public static final Item manaPearl = new Item(defaultBuilder());
	public static final Item manaDiamond = new Item(defaultBuilder());
	public static final Item livingwoodTwig = new Item(defaultBuilder());
	public static final Item terrasteel = new ItemManaResource(defaultBuilder().rarity(Rarity.UNCOMMON));
	public static final Item lifeEssence = new Item(defaultBuilder().rarity(Rarity.UNCOMMON));
	public static final Item redstoneRoot = new Item(defaultBuilder());
	public static final Item elementium = new ItemElven(defaultBuilder());
	public static final Item pixieDust = new ItemElven(defaultBuilder());
	public static final Item dragonstone = new ItemElven(defaultBuilder());
	public static final Item redString = new Item(defaultBuilder());
	public static final Item dreamwoodTwig = new Item(defaultBuilder());
	public static final Item gaiaIngot = new ItemManaResource(defaultBuilder().rarity(Rarity.RARE));
	public static final Item enderAirBottle = new ItemEnderAir(defaultBuilder());
	public static final Item manaString = new Item(defaultBuilder());
	public static final Item manasteelNugget = new Item(defaultBuilder());
	public static final Item terrasteelNugget = new Item(defaultBuilder().rarity(Rarity.UNCOMMON));
	public static final Item elementiumNugget = new Item(defaultBuilder());
	public static final Item livingroot = new ItemManaResource(defaultBuilder());
	public static final Item pebble = new Item(defaultBuilder());
	public static final Item manaweaveCloth = new Item(defaultBuilder());
	public static final Item manaPowder = new Item(defaultBuilder());

	public static final Item darkQuartz = new Item(defaultBuilder());
	public static final Item manaQuartz = new Item(defaultBuilder());
	public static final Item blazeQuartz = new Item(defaultBuilder());
	public static final Item lavenderQuartz = new Item(defaultBuilder());
	public static final Item redQuartz = new Item(defaultBuilder());
	public static final Item elfQuartz = new Item(defaultBuilder());
	public static final Item sunnyQuartz = new Item(defaultBuilder());

	public static final Item lensNormal = new ItemLens(unstackable(), new Lens(), ItemLens.PROP_NONE);
	public static final Item lensSpeed = new ItemLens(unstackable(), new LensSpeed(), ItemLens.PROP_NONE);
	public static final Item lensPower = new ItemLens(unstackable(), new LensPower(), ItemLens.PROP_POWER);
	public static final Item lensTime = new ItemLens(unstackable(), new LensTime(), ItemLens.PROP_NONE);
	public static final Item lensEfficiency = new ItemLens(unstackable(), new LensEfficiency(), ItemLens.PROP_NONE);
	public static final Item lensBounce = new ItemLens(unstackable(), new LensBounce(), ItemLens.PROP_TOUCH);
	public static final Item lensGravity = new ItemLens(unstackable(), new LensGravity(), ItemLens.PROP_ORIENTATION);
	public static final Item lensMine = new ItemLens(unstackable(), new LensMine(), ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensDamage = new ItemLens(unstackable(), new LensDamage(), ItemLens.PROP_DAMAGE);
	public static final Item lensPhantom = new ItemLens(unstackable(), new LensPhantom(), ItemLens.PROP_TOUCH);
	public static final Item lensMagnet = new ItemLens(unstackable(), new LensMagnet(), ItemLens.PROP_ORIENTATION);
	public static final Item lensExplosive = new ItemLens(unstackable(), new LensExplosive(), ItemLens.PROP_DAMAGE | ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensInfluence = new ItemLens(unstackable(), new LensInfluence(), ItemLens.PROP_NONE);
	public static final Item lensWeight = new ItemLens(unstackable(), new LensWeight(), ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensPaint = new ItemLens(unstackable(), new LensPaint(), ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensFire = new ItemLens(unstackable(), new LensFire(), ItemLens.PROP_DAMAGE | ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensPiston = new ItemLens(unstackable(), new LensPiston(), ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensLight = new ItemLens(unstackable(), new LensLight(), ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensWarp = new ItemLens(unstackable(), new LensWarp(), ItemLens.PROP_NONE);
	public static final Item lensRedirect = new ItemLens(unstackable(), new LensRedirect(), ItemLens.PROP_TOUCH | ItemLens.PROP_INTERACTION);
	public static final Item lensFirework = new ItemLens(unstackable(), new LensFirework(), ItemLens.PROP_TOUCH);
	public static final Item lensFlare = new ItemLens(unstackable(), new LensFlare(), ItemLens.PROP_CONTROL);
	public static final Item lensMessenger = new ItemLens(unstackable(), new LensMessenger(), ItemLens.PROP_POWER);
	public static final Item lensTripwire = new ItemLens(unstackable(), new LensTripwire(), ItemLens.PROP_CONTROL);
	public static final Item lensStorm = new ItemLens(unstackable().rarity(Rarity.EPIC), new LensStorm(), ItemLens.PROP_NONE);

	public static final Item runeWater = new ItemRune(defaultBuilder());
	public static final Item runeFire = new ItemRune(defaultBuilder());
	public static final Item runeEarth = new ItemRune(defaultBuilder());
	public static final Item runeAir = new ItemRune(defaultBuilder());
	public static final Item runeSpring = new ItemRune(defaultBuilder());
	public static final Item runeSummer = new ItemRune(defaultBuilder());
	public static final Item runeAutumn = new ItemRune(defaultBuilder());
	public static final Item runeWinter = new ItemRune(defaultBuilder());
	public static final Item runeMana = new ItemRune(defaultBuilder());
	public static final Item runeLust = new ItemRune(defaultBuilder());
	public static final Item runeGluttony = new ItemRune(defaultBuilder());
	public static final Item runeGreed = new ItemRune(defaultBuilder());
	public static final Item runeSloth = new ItemRune(defaultBuilder());
	public static final Item runeWrath = new ItemRune(defaultBuilder());
	public static final Item runeEnvy = new ItemRune(defaultBuilder());
	public static final Item runePride = new ItemRune(defaultBuilder());

	public static final Item grassSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.GRASS, defaultBuilder());
	public static final Item podzolSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.PODZOL, defaultBuilder());
	public static final Item mycelSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.MYCEL, defaultBuilder());
	public static final Item drySeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.DRY, defaultBuilder());
	public static final Item goldenSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.GOLDEN, defaultBuilder());
	public static final Item vividSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.VIVID, defaultBuilder());
	public static final Item scorchedSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.SCORCHED, defaultBuilder());
	public static final Item infusedSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.INFUSED, defaultBuilder());
	public static final Item mutatedSeeds = new ItemGrassSeeds(IFloatingFlower.IslandType.MUTATED, defaultBuilder());

	public static final Item dirtRod = new ItemDirtRod(unstackable());
	public static final Item skyDirtRod = new ItemSkyDirtRod(unstackable());
	public static final Item terraformRod = new ItemTerraformRod(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item cobbleRod = new ItemCobbleRod(unstackable());
	public static final Item waterRod = new ItemWaterRod(unstackable());
	public static final Item tornadoRod = new ItemTornadoRod(unstackable());
	public static final Item fireRod = new ItemFireRod(unstackable());
	public static final Item diviningRod = new ItemDiviningRod(unstackable());
	public static final Item smeltRod = new ItemSmeltRod(unstackable());
	public static final Item exchangeRod = new ItemExchangeRod(unstackable());
	public static final Item rainbowRod = new ItemRainbowRod(unstackable());
	public static final Item gravityRod = new ItemGravityRod(unstackable());
	public static final Item missileRod = new ItemMissileRod(unstackable().rarity(Rarity.UNCOMMON));

	// Equipment
	public static final Item manasteelHelm = new ItemManasteelHelm(unstackable());
	public static final Item manasteelChest = new ItemManasteelArmor(EquipmentSlotType.CHEST, unstackable());
	public static final Item manasteelLegs = new ItemManasteelArmor(EquipmentSlotType.LEGS, unstackable());
	public static final Item manasteelBoots = new ItemManasteelArmor(EquipmentSlotType.FEET, unstackable());
	public static final Item manasteelPick = new ItemManasteelPick(unstackable());
	public static final Item manasteelShovel = new ItemManasteelShovel(unstackable());
	public static final Item manasteelAxe = new ItemManasteelAxe(unstackable());
	public static final Item manasteelHoe = new ItemManasteelHoe(unstackable());
	public static final Item manasteelSword = new ItemManasteelSword(unstackable());
	public static final Item manasteelShears = new ItemManasteelShears(unstackable().defaultMaxDamage(238));
	public static final Item elementiumHelm = new ItemElementiumHelm(unstackable());
	public static final Item elementiumChest = new ItemElementiumChest(unstackable());
	public static final Item elementiumLegs = new ItemElementiumLegs(unstackable());
	public static final Item elementiumBoots = new ItemElementiumBoots(unstackable());
	public static final Item elementiumPick = new ItemElementiumPick(unstackable());
	public static final Item elementiumShovel = new ItemElementiumShovel(unstackable());
	public static final Item elementiumAxe = new ItemElementiumAxe(unstackable());
	public static final Item elementiumHoe = new ItemElementiumHoe(unstackable());
	public static final Item elementiumSword = new ItemElementiumSword(unstackable());
	public static final Item elementiumShears = new ItemElementiumShears(unstackable().defaultMaxDamage(238));
	public static final Item terrasteelHelm = new ItemTerrasteelHelm(unstackable().isImmuneToFire().rarity(Rarity.UNCOMMON));
	public static final Item terrasteelChest = new ItemTerrasteelArmor(EquipmentSlotType.CHEST, unstackable().isImmuneToFire().rarity(Rarity.UNCOMMON));
	public static final Item terrasteelLegs = new ItemTerrasteelArmor(EquipmentSlotType.LEGS, unstackable().isImmuneToFire().rarity(Rarity.UNCOMMON));
	public static final Item terrasteelBoots = new ItemTerrasteelArmor(EquipmentSlotType.FEET, unstackable().isImmuneToFire().rarity(Rarity.UNCOMMON));
	public static final Item terraPick = new ItemTerraPick(unstackable().isImmuneToFire().rarity(Rarity.UNCOMMON));
	public static final Item terraAxe = new ItemTerraAxe(unstackable().isImmuneToFire().rarity(Rarity.UNCOMMON));
	public static final Item terraSword = new ItemTerraSword(unstackable().isImmuneToFire().rarity(Rarity.UNCOMMON));
	public static final Item starSword = new ItemStarSword(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item thunderSword = new ItemThunderSword(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item manaweaveHelm = new ItemManaweaveHelm(unstackable());
	public static final Item manaweaveChest = new ItemManaweaveArmor(EquipmentSlotType.CHEST, unstackable());
	public static final Item manaweaveLegs = new ItemManaweaveArmor(EquipmentSlotType.LEGS, unstackable());
	public static final Item manaweaveBoots = new ItemManaweaveArmor(EquipmentSlotType.FEET, unstackable());
	public static final Item enderDagger = new ItemEnderDagger(unstackable().defaultMaxDamage(69).setNoRepair()); // What you looking at?
	public static final Item glassPick = new ItemGlassPick(unstackable());
	public static final Item livingwoodBow = new ItemLivingwoodBow(defaultBuilder().defaultMaxDamage(500));
	public static final Item crystalBow = new ItemCrystalBow(defaultBuilder().defaultMaxDamage(500));
	public static final Item thornChakram = new ItemThornChakram(defaultBuilder().maxStackSize(6));
	public static final Item flareChakram = new ItemThornChakram(defaultBuilder().maxStackSize(6));

	// Misc tools
	public static final Item manaTablet = new ItemManaTablet(unstackable());
	public static final Item manaMirror = new ItemManaMirror(unstackable());
	public static final Item manaGun = new ItemManaGun(unstackable());
	public static final Item clip = new Item(unstackable());
	public static final Item grassHorn = new ItemHorn(unstackable());
	public static final Item leavesHorn = new ItemHorn(unstackable());
	public static final Item snowHorn = new ItemHorn(unstackable());
	public static final Item vineBall = new ItemVineBall(defaultBuilder());
	public static final Item slingshot = new ItemSlingshot(unstackable());
	public static final Item openBucket = new ItemOpenBucket(unstackable());
	public static final Item spawnerMover = new ItemSpawnerMover(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item enderHand = new ItemEnderHand(unstackable());
	public static final Item craftingHalo = new ItemCraftingHalo(unstackable());
	public static final Item autocraftingHalo = new ItemAutocraftingHalo(unstackable());
	public static final Item spellCloth = new ItemSpellCloth(unstackable().defaultMaxDamage(35).setNoRepair());
	public static final Item flowerBag = new ItemFlowerBag(unstackable());
	public static final Item blackHoleTalisman = new ItemBlackHoleTalisman(unstackable());
	public static final Item temperanceStone = new ItemTemperanceStone(unstackable());
	public static final Item waterBowl = new ItemWaterBowl(unstackable());
	public static final Item cacophonium = new ItemCacophonium(unstackable());
	public static final Item slimeBottle = new ItemSlimeBottle(unstackable());
	public static final Item sextant = new ItemSextant(unstackable());
	public static final Item astrolabe = new ItemAstrolabe(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item baubleBox = new ItemBaubleBox(unstackable());

	// Baubles / trinkets / curios / etc.
	public static final Item manaRing = new ItemManaRing(unstackable());
	public static final Item manaRingGreater = new ItemGreaterManaRing(unstackable());
	public static final Item auraRing = new ItemAuraRing(unstackable(), 10);
	public static final Item auraRingGreater = new ItemAuraRing(unstackable(), 2);
	public static final Item magnetRing = new ItemMagnetRing(unstackable());
	public static final Item magnetRingGreater = new ItemMagnetRing(unstackable(), 16);
	public static final Item waterRing = new ItemWaterRing(unstackable().rarity(Rarity.RARE));
	public static final Item swapRing = new ItemSwapRing(unstackable());
	public static final Item dodgeRing = new ItemDodgeRing(unstackable());
	public static final Item miningRing = new ItemMiningRing(unstackable());
	public static final Item pixieRing = new ItemPixieRing(unstackable());
	public static final Item reachRing = new ItemReachRing(unstackable());
	public static final Item travelBelt = new ItemTravelBelt(unstackable());
	public static final Item superTravelBelt = new ItemSuperTravelBelt(unstackable());
	public static final Item speedUpBelt = new ItemSpeedUpBelt(unstackable());
	public static final Item knockbackBelt = new ItemKnockbackBelt(unstackable());
	public static final Item icePendant = new ItemIcePendant(unstackable());
	public static final Item lavaPendant = new ItemLavaPendant(unstackable());
	public static final Item superLavaPendant = new ItemSuperLavaPendant(unstackable());
	public static final Item cloudPendant = new ItemCloudPendant(unstackable());
	public static final Item superCloudPendant = new ItemSuperCloudPendant(unstackable());
	public static final Item holyCloak = new ItemHolyCloak(unstackable());
	public static final Item unholyCloak = new ItemUnholyCloak(unstackable());
	public static final Item balanceCloak = new ItemBalanceCloak(unstackable());
	public static final Item invisibilityCloak = new ItemInvisibilityCloak(unstackable());
	public static final Item thirdEye = new ItemThirdEye(unstackable());
	public static final Item monocle = new ItemMonocle(unstackable());
	public static final Item tinyPlanet = new ItemTinyPlanet(unstackable());
	public static final Item goddessCharm = new ItemGoddessCharm(unstackable());
	public static final Item divaCharm = new ItemDivaCharm(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item itemFinder = new ItemItemFinder(unstackable());
	public static final Item flightTiara = new ItemFlightTiara(unstackable().rarity(Rarity.UNCOMMON));

	// Misc
	public static final Item manaCookie = new Item(defaultBuilder().food(new Food.Builder().hunger(0).saturation(0.1F).effect(new EffectInstance(Effects.SATURATION, 20, 0), 1).build()));
	public static final Item manaBottle = new ItemBottledMana(unstackable());
	public static final Item laputaShard = new ItemLaputaShard(unstackable().rarity(Rarity.UNCOMMON));
	public static final Item necroVirus = new ItemVirus(defaultBuilder());
	public static final Item nullVirus = new ItemVirus(defaultBuilder());
	public static final Item spark = new ItemManaSpark(defaultBuilder());
	public static final Item sparkUpgradeDispersive = new ItemSparkUpgrade(defaultBuilder(), SparkUpgradeType.DISPERSIVE);
	public static final Item sparkUpgradeDominant = new ItemSparkUpgrade(defaultBuilder(), SparkUpgradeType.DOMINANT);
	public static final Item sparkUpgradeRecessive = new ItemSparkUpgrade(defaultBuilder(), SparkUpgradeType.RECESSIVE);
	public static final Item sparkUpgradeIsolated = new ItemSparkUpgrade(defaultBuilder(), SparkUpgradeType.ISOLATED);
	public static final Item corporeaSpark = new ItemCorporeaSpark(defaultBuilder());
	public static final Item corporeaSparkMaster = new ItemCorporeaSpark(defaultBuilder());
	public static final Item blackLotus = new ItemBlackLotus(defaultBuilder().rarity(Rarity.RARE));
	public static final Item blackerLotus = new ItemBlackLotus(defaultBuilder().rarity(Rarity.EPIC));
	public static final Item worldSeed = new ItemWorldSeed(defaultBuilder());
	public static final Item overgrowthSeed = new ItemOvergrowthSeed(defaultBuilder().rarity(Rarity.RARE));
	public static final Item phantomInk = new Item(defaultBuilder());
	public static final Item poolMinecart = new ItemPoolMinecart(unstackable());
	public static final Item keepIvy = new ItemKeepIvy(defaultBuilder());
	public static final Item placeholder = new ItemSelfReturning(defaultBuilder());
	public static final Item craftPattern1_1 = new ItemCraftPattern(CratePattern.CRAFTY_1_1, unstackable());
	public static final Item craftPattern2_2 = new ItemCraftPattern(CratePattern.CRAFTY_2_2, unstackable());
	public static final Item craftPattern1_2 = new ItemCraftPattern(CratePattern.CRAFTY_1_2, unstackable());
	public static final Item craftPattern2_1 = new ItemCraftPattern(CratePattern.CRAFTY_2_1, unstackable());
	public static final Item craftPattern1_3 = new ItemCraftPattern(CratePattern.CRAFTY_1_3, unstackable());
	public static final Item craftPattern3_1 = new ItemCraftPattern(CratePattern.CRAFTY_3_1, unstackable());
	public static final Item craftPattern2_3 = new ItemCraftPattern(CratePattern.CRAFTY_2_3, unstackable());
	public static final Item craftPattern3_2 = new ItemCraftPattern(CratePattern.CRAFTY_3_2, unstackable());
	public static final Item craftPatternDonut = new ItemCraftPattern(CratePattern.CRAFTY_DONUT, unstackable());

	// Guardian of Gaia drops
	public static final Item dice = new ItemDice(unstackable().isImmuneToFire().rarity(BotaniaAPI.instance().getRelicRarity()));
	public static final Item infiniteFruit = new ItemInfiniteFruit(unstackable().isImmuneToFire().rarity(BotaniaAPI.instance().getRelicRarity()));
	public static final Item kingKey = new ItemKingKey(unstackable().isImmuneToFire().rarity(BotaniaAPI.instance().getRelicRarity()));
	public static final Item flugelEye = new ItemFlugelEye(unstackable().isImmuneToFire().rarity(BotaniaAPI.instance().getRelicRarity()));
	public static final Item thorRing = new ItemThorRing(unstackable().isImmuneToFire().rarity(BotaniaAPI.instance().getRelicRarity()));
	public static final Item odinRing = new ItemOdinRing(unstackable().isImmuneToFire().rarity(BotaniaAPI.instance().getRelicRarity()));
	public static final Item lokiRing = new ItemLokiRing(unstackable().isImmuneToFire().rarity(BotaniaAPI.instance().getRelicRarity()));
	public static final Item recordGaia1 = new ItemModRecord(1, ModSounds.gaiaMusic1, unstackable().rarity(Rarity.RARE));
	public static final Item recordGaia2 = new ItemModRecord(1, ModSounds.gaiaMusic2, unstackable().rarity(Rarity.RARE));
	public static final Item ancientWillAhrim = new ItemAncientWill(IAncientWillContainer.AncientWillType.AHRIM, unstackable().rarity(Rarity.UNCOMMON));
	public static final Item ancientWillDharok = new ItemAncientWill(IAncientWillContainer.AncientWillType.DHAROK, unstackable().rarity(Rarity.UNCOMMON));
	public static final Item ancientWillGuthan = new ItemAncientWill(IAncientWillContainer.AncientWillType.GUTHAN, unstackable().rarity(Rarity.UNCOMMON));
	public static final Item ancientWillTorag = new ItemAncientWill(IAncientWillContainer.AncientWillType.TORAG, unstackable().rarity(Rarity.UNCOMMON));
	public static final Item ancientWillVerac = new ItemAncientWill(IAncientWillContainer.AncientWillType.VERAC, unstackable().rarity(Rarity.UNCOMMON));
	public static final Item ancientWillKaril = new ItemAncientWill(IAncientWillContainer.AncientWillType.KARIL, unstackable().rarity(Rarity.UNCOMMON));
	public static final Item pinkinator = new ItemPinkinator(unstackable().rarity(Rarity.UNCOMMON));

	// Brewing
	public static final Item vial = new ItemVial(defaultBuilder());
	public static final Item flask = new ItemVial(defaultBuilder());
	public static final Item brewVial = new ItemBrewBase(unstackable(), 4, 32, () -> vial);
	public static final Item brewFlask = new ItemBrewBase(unstackable(), 6, 24, () -> flask);
	public static final Item bloodPendant = new ItemBloodPendant(unstackable());
	public static final Item incenseStick = new ItemIncenseStick(unstackable());

	// Cosmetics
	public static final Item blackBowtie = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.BLACK_BOWTIE, unstackable());
	public static final Item blackTie = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.BLACK_TIE, unstackable());
	public static final Item redGlasses = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.RED_GLASSES, unstackable());
	public static final Item puffyScarf = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.PUFFY_SCARF, unstackable());
	public static final Item engineerGoggles = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.ENGINEER_GOGGLES, unstackable());
	public static final Item eyepatch = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.EYEPATCH, unstackable());
	public static final Item wickedEyepatch = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.WICKED_EYEPATCH, unstackable());
	public static final Item redRibbons = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.RED_RIBBONS, unstackable());
	public static final Item pinkFlowerBud = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.PINK_FLOWER_BUD, unstackable());
	public static final Item polkaDottedBows = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.POLKA_DOTTED_BOWS, unstackable());
	public static final Item blueButterfly = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.BLUE_BUTTERFLY, unstackable());
	public static final Item catEars = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.CAT_EARS, unstackable());
	public static final Item witchPin = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.WITCH_PIN, unstackable());
	public static final Item devilTail = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.DEVIL_TAIL, unstackable());
	public static final Item kamuiEye = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.KAMUI_EYE, unstackable());
	public static final Item googlyEyes = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.GOOGLY_EYES, unstackable());
	public static final Item fourLeafClover = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.FOUR_LEAF_CLOVER, unstackable());
	public static final Item clockEye = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.CLOCK_EYE, unstackable());
	public static final Item unicornHorn = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.UNICORN_HORN, unstackable());
	public static final Item devilHorns = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.DEVIL_HORNS, unstackable());
	public static final Item hyperPlus = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.HYPER_PLUS, unstackable());
	public static final Item botanistEmblem = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.BOTANIST_EMBLEM, unstackable());
	public static final Item ancientMask = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.ANCIENT_MASK, unstackable());
	public static final Item eerieMask = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.EERIE_MASK, unstackable());
	public static final Item alienAntenna = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.ALIEN_ANTENNA, unstackable());
	public static final Item anaglyphGlasses = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.ANAGLYPH_GLASSES, unstackable());
	public static final Item orangeShades = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.ORANGE_SHADES, unstackable());
	public static final Item grouchoGlasses = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.GROUCHO_GLASSES, unstackable());
	public static final Item thickEyebrows = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.THICK_EYEBROWS, unstackable());
	public static final Item lusitanicShield = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.LUSITANIC_SHIELD, unstackable());
	public static final Item tinyPotatoMask = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.TINY_POTATO_MASK, unstackable());
	public static final Item questgiverMark = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.QUESTGIVER_MARK, unstackable());
	public static final Item thinkingHand = new ItemBaubleCosmetic(ItemBaubleCosmetic.Variant.THINKING_HAND, unstackable());

	public static final ContainerType<ContainerFlowerBag> FLOWER_BAG_CONTAINER = IForgeContainerType.create(ContainerFlowerBag::fromNetwork);
	public static final ContainerType<ContainerBaubleBox> BAUBLE_BOX_CONTAINER = IForgeContainerType.create(ContainerBaubleBox::fromNetwork);

	public static Item.Properties defaultBuilder() {
		return new Item.Properties().group(BotaniaCreativeTab.INSTANCE);
	}

	private static Item.Properties unstackable() {
		return defaultBuilder().maxStackSize(1);
	}

	public static void registerItems(RegistryEvent.Register<Item> evt) {
		IForgeRegistry<Item> r = evt.getRegistry();
		register(r, LibItemNames.LEXICON, lexicon);
		register(r, LibItemNames.TWIG_WAND, twigWand);
		register(r, LibItemNames.OBEDIENCE_STICK, obedienceStick);
		register(r, LibItemNames.PESTLE_AND_MORTAR, pestleAndMortar);
		register(r, LibItemNames.FERTILIZER, fertilizer);
		register(r, "white" + LibItemNames.PETAL_SUFFIX, whitePetal);
		register(r, "orange" + LibItemNames.PETAL_SUFFIX, orangePetal);
		register(r, "magenta" + LibItemNames.PETAL_SUFFIX, magentaPetal);
		register(r, "light_blue" + LibItemNames.PETAL_SUFFIX, lightBluePetal);
		register(r, "yellow" + LibItemNames.PETAL_SUFFIX, yellowPetal);
		register(r, "lime" + LibItemNames.PETAL_SUFFIX, limePetal);
		register(r, "pink" + LibItemNames.PETAL_SUFFIX, pinkPetal);
		register(r, "gray" + LibItemNames.PETAL_SUFFIX, grayPetal);
		register(r, "light_gray" + LibItemNames.PETAL_SUFFIX, lightGrayPetal);
		register(r, "cyan" + LibItemNames.PETAL_SUFFIX, cyanPetal);
		register(r, "purple" + LibItemNames.PETAL_SUFFIX, purplePetal);
		register(r, "blue" + LibItemNames.PETAL_SUFFIX, bluePetal);
		register(r, "brown" + LibItemNames.PETAL_SUFFIX, brownPetal);
		register(r, "green" + LibItemNames.PETAL_SUFFIX, greenPetal);
		register(r, "red" + LibItemNames.PETAL_SUFFIX, redPetal);
		register(r, "black" + LibItemNames.PETAL_SUFFIX, blackPetal);
		register(r, LibItemNames.MANASTEEL_INGOT, manaSteel);
		register(r, LibItemNames.MANA_PEARL, manaPearl);
		register(r, LibItemNames.MANA_DIAMOND, manaDiamond);
		register(r, LibItemNames.LIVINGWOOD_TWIG, livingwoodTwig);
		register(r, LibItemNames.TERRASTEEL_INGOT, terrasteel);
		register(r, LibItemNames.LIFE_ESSENCE, lifeEssence);
		register(r, LibItemNames.REDSTONE_ROOT, redstoneRoot);
		register(r, LibItemNames.ELEMENTIUM_INGOT, elementium);
		register(r, LibItemNames.PIXIE_DUST, pixieDust);
		register(r, LibItemNames.DRAGONSTONE, dragonstone);
		register(r, LibItemNames.RED_STRING, redString);
		register(r, LibItemNames.DREAMWOOD_TWIG, dreamwoodTwig);
		register(r, LibItemNames.GAIA_INGOT, gaiaIngot);
		register(r, LibItemNames.ENDER_AIR_BOTTLE, enderAirBottle);
		register(r, LibItemNames.MANA_STRING, manaString);
		register(r, LibItemNames.MANASTEEL_NUGGET, manasteelNugget);
		register(r, LibItemNames.TERRASTEEL_NUGGET, terrasteelNugget);
		register(r, LibItemNames.ELEMENTIUM_NUGGET, elementiumNugget);
		register(r, LibItemNames.LIVING_ROOT, livingroot);
		register(r, LibItemNames.PEBBLE, pebble);
		register(r, LibItemNames.MANAWEAVE_CLOTH, manaweaveCloth);
		register(r, LibItemNames.MANA_POWDER, manaPowder);
		register(r, LibItemNames.QUARTZ_DARK, darkQuartz);
		register(r, LibItemNames.QUARTZ_MANA, manaQuartz);
		register(r, LibItemNames.QUARTZ_BLAZE, blazeQuartz);
		register(r, LibItemNames.QUARTZ_LAVENDER, lavenderQuartz);
		register(r, LibItemNames.QUARTZ_RED, redQuartz);
		register(r, LibItemNames.QUARTZ_ELVEN, elfQuartz);
		register(r, LibItemNames.QUARTZ_SUNNY, sunnyQuartz);
		register(r, LibItemNames.LENS_NORMAL, lensNormal);
		register(r, LibItemNames.LENS_SPEED, lensSpeed);
		register(r, LibItemNames.LENS_POWER, lensPower);
		register(r, LibItemNames.LENS_TIME, lensTime);
		register(r, LibItemNames.LENS_EFFICIENCY, lensEfficiency);
		register(r, LibItemNames.LENS_BOUNCE, lensBounce);
		register(r, LibItemNames.LENS_GRAVITY, lensGravity);
		register(r, LibItemNames.LENS_MINE, lensMine);
		register(r, LibItemNames.LENS_DAMAGE, lensDamage);
		register(r, LibItemNames.LENS_PHANTOM, lensPhantom);
		register(r, LibItemNames.LENS_MAGNET, lensMagnet);
		register(r, LibItemNames.LENS_EXPLOSIVE, lensExplosive);
		register(r, LibItemNames.LENS_INFLUENCE, lensInfluence);
		register(r, LibItemNames.LENS_WEIGHT, lensWeight);
		register(r, LibItemNames.LENS_PAINT, lensPaint);
		register(r, LibItemNames.LENS_FIRE, lensFire);
		register(r, LibItemNames.LENS_PISTON, lensPiston);
		register(r, LibItemNames.LENS_LIGHT, lensLight);
		register(r, LibItemNames.LENS_WARP, lensWarp);
		register(r, LibItemNames.LENS_REDIRECT, lensRedirect);
		register(r, LibItemNames.LENS_FIREWORK, lensFirework);
		register(r, LibItemNames.LENS_FLARE, lensFlare);
		register(r, LibItemNames.LENS_MESSENGER, lensMessenger);
		register(r, LibItemNames.LENS_TRIPWIRE, lensTripwire);
		register(r, LibItemNames.LENS_STORM, lensStorm);
		register(r, LibItemNames.RUNE_WATER, runeWater);
		register(r, LibItemNames.RUNE_FIRE, runeFire);
		register(r, LibItemNames.RUNE_EARTH, runeEarth);
		register(r, LibItemNames.RUNE_AIR, runeAir);
		register(r, LibItemNames.RUNE_SPRING, runeSpring);
		register(r, LibItemNames.RUNE_SUMMER, runeSummer);
		register(r, LibItemNames.RUNE_AUTUMN, runeAutumn);
		register(r, LibItemNames.RUNE_WINTER, runeWinter);
		register(r, LibItemNames.RUNE_MANA, runeMana);
		register(r, LibItemNames.RUNE_LUST, runeLust);
		register(r, LibItemNames.RUNE_GLUTTONY, runeGluttony);
		register(r, LibItemNames.RUNE_GREED, runeGreed);
		register(r, LibItemNames.RUNE_SLOTH, runeSloth);
		register(r, LibItemNames.RUNE_WRATH, runeWrath);
		register(r, LibItemNames.RUNE_ENVY, runeEnvy);
		register(r, LibItemNames.RUNE_PRIDE, runePride);
		register(r, LibItemNames.GRASS_SEEDS, grassSeeds);
		register(r, LibItemNames.PODZOL_SEEDS, podzolSeeds);
		register(r, LibItemNames.MYCEL_SEEDS, mycelSeeds);
		register(r, LibItemNames.DRY_SEEDS, drySeeds);
		register(r, LibItemNames.GOLDEN_SEEDS, goldenSeeds);
		register(r, LibItemNames.VIVID_SEEDS, vividSeeds);
		register(r, LibItemNames.SCORCHED_SEEDS, scorchedSeeds);
		register(r, LibItemNames.INFUSED_SEEDS, infusedSeeds);
		register(r, LibItemNames.MUTATED_SEEDS, mutatedSeeds);
		register(r, LibItemNames.DIRT_ROD, dirtRod);
		register(r, LibItemNames.SKY_DIRT_ROD, skyDirtRod);
		register(r, LibItemNames.TERRAFORM_ROD, terraformRod);
		register(r, LibItemNames.COBBLE_ROD, cobbleRod);
		register(r, LibItemNames.WATER_ROD, waterRod);
		register(r, LibItemNames.TORNADO_ROD, tornadoRod);
		register(r, LibItemNames.FIRE_ROD, fireRod);
		register(r, LibItemNames.DIVINING_ROD, diviningRod);
		register(r, LibItemNames.SMELT_ROD, smeltRod);
		register(r, LibItemNames.EXCHANGE_ROD, exchangeRod);
		register(r, LibItemNames.RAINBOW_ROD, rainbowRod);
		register(r, LibItemNames.GRAVITY_ROD, gravityRod);
		register(r, LibItemNames.MISSILE_ROD, missileRod);
		register(r, LibItemNames.MANASTEEL_HELM, manasteelHelm);
		register(r, LibItemNames.MANASTEEL_CHEST, manasteelChest);
		register(r, LibItemNames.MANASTEEL_LEGS, manasteelLegs);
		register(r, LibItemNames.MANASTEEL_BOOTS, manasteelBoots);
		register(r, LibItemNames.MANASTEEL_PICK, manasteelPick);
		register(r, LibItemNames.MANASTEEL_SHOVEL, manasteelShovel);
		register(r, LibItemNames.MANASTEEL_HOE, manasteelHoe);
		register(r, LibItemNames.MANASTEEL_AXE, manasteelAxe);
		register(r, LibItemNames.MANASTEEL_SWORD, manasteelSword);
		register(r, LibItemNames.MANASTEEL_SHEARS, manasteelShears);
		register(r, LibItemNames.ELEMENTIUM_HELM, elementiumHelm);
		register(r, LibItemNames.ELEMENTIUM_CHEST, elementiumChest);
		register(r, LibItemNames.ELEMENTIUM_LEGS, elementiumLegs);
		register(r, LibItemNames.ELEMENTIUM_BOOTS, elementiumBoots);
		register(r, LibItemNames.ELEMENTIUM_PICK, elementiumPick);
		register(r, LibItemNames.ELEMENTIUM_SHOVEL, elementiumShovel);
		register(r, LibItemNames.ELEMENTIUM_HOE, elementiumHoe);
		register(r, LibItemNames.ELEMENTIUM_AXE, elementiumAxe);
		register(r, LibItemNames.ELEMENTIUM_SWORD, elementiumSword);
		register(r, LibItemNames.ELEMENTIUM_SHEARS, elementiumShears);
		register(r, LibItemNames.TERRASTEEL_HELM, terrasteelHelm);
		register(r, LibItemNames.TERRASTEEL_CHEST, terrasteelChest);
		register(r, LibItemNames.TERRASTEEL_LEGS, terrasteelLegs);
		register(r, LibItemNames.TERRASTEEL_BOOTS, terrasteelBoots);
		register(r, LibItemNames.TERRA_PICK, terraPick);
		register(r, LibItemNames.TERRA_AXE, terraAxe);
		register(r, LibItemNames.TERRA_SWORD, terraSword);
		register(r, LibItemNames.STAR_SWORD, starSword);
		register(r, LibItemNames.THUNDER_SWORD, thunderSword);
		register(r, LibItemNames.MANAWEAVE_HELM, manaweaveHelm);
		register(r, LibItemNames.MANAWEAVE_CHEST, manaweaveChest);
		register(r, LibItemNames.MANAWEAVE_LEGS, manaweaveLegs);
		register(r, LibItemNames.MANAWEAVE_BOOTS, manaweaveBoots);
		register(r, LibItemNames.ENDER_DAGGER, enderDagger);
		register(r, LibItemNames.GLASS_PICK, glassPick);
		register(r, LibItemNames.LIVINGWOOD_BOW, livingwoodBow);
		register(r, LibItemNames.CRYSTAL_BOW, crystalBow);
		register(r, LibItemNames.THORN_CHAKRAM, thornChakram);
		register(r, LibItemNames.FLARE_CHAKRAM, flareChakram);
		register(r, LibItemNames.MANA_TABLET, manaTablet);
		register(r, LibItemNames.MANA_MIRROR, manaMirror);
		register(r, LibItemNames.MANA_GUN, manaGun);
		register(r, LibItemNames.CLIP, clip);
		register(r, LibItemNames.GRASS_HORN, grassHorn);
		register(r, LibItemNames.LEAVES_HORN, leavesHorn);
		register(r, LibItemNames.SNOW_HORN, snowHorn);
		register(r, LibItemNames.VINE_BALL, vineBall);
		register(r, LibItemNames.SLINGSHOT, slingshot);
		register(r, LibItemNames.OPEN_BUCKET, openBucket);
		register(r, LibItemNames.SPAWNER_MOVER, spawnerMover);
		register(r, LibItemNames.ENDER_HAND, enderHand);
		register(r, LibItemNames.CRAFTING_HALO, craftingHalo);
		register(r, LibItemNames.AUTOCRAFTING_HALO, autocraftingHalo);
		register(r, LibItemNames.SPELL_CLOTH, spellCloth);
		register(r, LibItemNames.FLOWER_BAG, flowerBag);
		register(r, LibItemNames.BLACK_HOLE_TALISMAN, blackHoleTalisman);
		register(r, LibItemNames.TEMPERANCE_STONE, temperanceStone);
		register(r, LibItemNames.WATER_BOWL, waterBowl);
		register(r, LibItemNames.CACOPHONIUM, cacophonium);
		register(r, LibItemNames.SLIME_BOTTLE, slimeBottle);
		register(r, LibItemNames.SEXTANT, sextant);
		register(r, LibItemNames.ASTROLABE, astrolabe);
		register(r, LibItemNames.BAUBLE_BOX, baubleBox);
		register(r, LibItemNames.MANA_RING, manaRing);
		register(r, LibItemNames.MANA_RING_GREATER, manaRingGreater);
		register(r, LibItemNames.AURA_RING, auraRing);
		register(r, LibItemNames.AURA_RING_GREATER, auraRingGreater);
		register(r, LibItemNames.MAGNET_RING, magnetRing);
		register(r, LibItemNames.MAGNET_RING_GREATER, magnetRingGreater);
		register(r, LibItemNames.WATER_RING, waterRing);
		register(r, LibItemNames.SWAP_RING, swapRing);
		register(r, LibItemNames.DODGE_RING, dodgeRing);
		register(r, LibItemNames.MINING_RING, miningRing);
		register(r, LibItemNames.PIXIE_RING, pixieRing);
		register(r, LibItemNames.REACH_RING, reachRing);
		register(r, LibItemNames.TRAVEL_BELT, travelBelt);
		register(r, LibItemNames.SUPER_TRAVEL_BELT, superTravelBelt);
		register(r, LibItemNames.SPEED_UP_BELT, speedUpBelt);
		register(r, LibItemNames.KNOCKBACK_BELT, knockbackBelt);
		register(r, LibItemNames.ICE_PENDANT, icePendant);
		register(r, LibItemNames.LAVA_PENDANT, lavaPendant);
		register(r, LibItemNames.SUPER_LAVA_PENDANT, superLavaPendant);
		register(r, LibItemNames.CLOUD_PENDANT, cloudPendant);
		register(r, LibItemNames.SUPER_CLOUD_PENDANT, superCloudPendant);
		register(r, LibItemNames.HOLY_CLOAK, holyCloak);
		register(r, LibItemNames.UNHOLY_CLOAK, unholyCloak);
		register(r, LibItemNames.BALANCE_CLOAK, balanceCloak);
		register(r, LibItemNames.INVISIBILITY_CLOAK, invisibilityCloak);
		register(r, LibItemNames.THIRD_EYE, thirdEye);
		register(r, LibItemNames.MONOCLE, monocle);
		register(r, LibItemNames.TINY_PLANET, tinyPlanet);
		register(r, LibItemNames.GODDESS_CHARM, goddessCharm);
		register(r, LibItemNames.DIVA_CHARM, divaCharm);
		register(r, LibItemNames.ITEM_FINDER, itemFinder);
		register(r, LibItemNames.FLIGHT_TIARA, flightTiara);
		register(r, LibItemNames.MANA_COOKIE, manaCookie);
		register(r, LibItemNames.MANA_BOTTLE, manaBottle);
		register(r, LibItemNames.LAPUTA_SHARD, laputaShard);
		register(r, LibItemNames.NECRO_VIRUS, necroVirus);
		register(r, LibItemNames.NULL_VIRUS, nullVirus);
		register(r, LibItemNames.SPARK, spark);
		register(r, LibItemNames.SPARK_UPGRADE + "_" + SparkUpgradeType.DISPERSIVE.name().toLowerCase(Locale.ROOT), sparkUpgradeDispersive);
		register(r, LibItemNames.SPARK_UPGRADE + "_" + SparkUpgradeType.DOMINANT.name().toLowerCase(Locale.ROOT), sparkUpgradeDominant);
		register(r, LibItemNames.SPARK_UPGRADE + "_" + SparkUpgradeType.RECESSIVE.name().toLowerCase(Locale.ROOT), sparkUpgradeRecessive);
		register(r, LibItemNames.SPARK_UPGRADE + "_" + SparkUpgradeType.ISOLATED.name().toLowerCase(Locale.ROOT), sparkUpgradeIsolated);
		register(r, LibItemNames.CORPOREA_SPARK, corporeaSpark);
		register(r, LibItemNames.CORPOREA_SPARK_MASTER, corporeaSparkMaster);
		register(r, LibItemNames.BLACK_LOTUS, blackLotus);
		register(r, LibItemNames.BLACKER_LOTUS, blackerLotus);
		register(r, LibItemNames.WORLD_SEED, worldSeed);
		register(r, LibItemNames.OVERGROWTH_SEED, overgrowthSeed);
		register(r, LibItemNames.PHANTOM_INK, phantomInk);
		register(r, LibItemNames.POOL_MINECART, poolMinecart);
		register(r, LibItemNames.KEEP_IVY, keepIvy);
		register(r, LibItemNames.PLACEHOLDER, placeholder);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "1_1", craftPattern1_1);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "2_2", craftPattern2_2);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "1_2", craftPattern1_2);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "2_1", craftPattern2_1);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "1_3", craftPattern1_3);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "3_1", craftPattern3_1);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "2_3", craftPattern2_3);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "3_2", craftPattern3_2);
		register(r, LibItemNames.CRAFT_PATTERN_PREFIX + "donut", craftPatternDonut);
		register(r, LibItemNames.DICE, dice);
		register(r, LibItemNames.INFINITE_FRUIT, infiniteFruit);
		register(r, LibItemNames.KING_KEY, kingKey);
		register(r, LibItemNames.FLUGEL_EYE, flugelEye);
		register(r, LibItemNames.THOR_RING, thorRing);
		register(r, LibItemNames.ODIN_RING, odinRing);
		register(r, LibItemNames.LOKI_RING, lokiRing);
		register(r, LibItemNames.RECORD_GAIA1, recordGaia1);
		register(r, LibItemNames.RECORD_GAIA2, recordGaia2);
		register(r, LibItemNames.ANCIENT_WILL_PREFIX + "ahrim", ancientWillAhrim);
		register(r, LibItemNames.ANCIENT_WILL_PREFIX + "dharok", ancientWillDharok);
		register(r, LibItemNames.ANCIENT_WILL_PREFIX + "guthan", ancientWillGuthan);
		register(r, LibItemNames.ANCIENT_WILL_PREFIX + "torag", ancientWillTorag);
		register(r, LibItemNames.ANCIENT_WILL_PREFIX + "verac", ancientWillVerac);
		register(r, LibItemNames.ANCIENT_WILL_PREFIX + "karil", ancientWillKaril);
		register(r, LibItemNames.PINKINATOR, pinkinator);
		register(r, LibItemNames.VIAL, vial);
		register(r, LibItemNames.FLASK, flask);
		register(r, LibItemNames.BREW_VIAL, brewVial);
		register(r, LibItemNames.BREW_FLASK, brewFlask);
		register(r, LibItemNames.BLOOD_PENDANT, bloodPendant);
		register(r, LibItemNames.INCENSE_STICK, incenseStick);
		register(r, LibItemNames.COSMETIC_PREFIX + "black_bowtie", blackBowtie);
		register(r, LibItemNames.COSMETIC_PREFIX + "black_tie", blackTie);
		register(r, LibItemNames.COSMETIC_PREFIX + "red_glasses", redGlasses);
		register(r, LibItemNames.COSMETIC_PREFIX + "puffy_scarf", puffyScarf);
		register(r, LibItemNames.COSMETIC_PREFIX + "engineer_goggles", engineerGoggles);
		register(r, LibItemNames.COSMETIC_PREFIX + "eyepatch", eyepatch);
		register(r, LibItemNames.COSMETIC_PREFIX + "wicked_eyepatch", wickedEyepatch);
		register(r, LibItemNames.COSMETIC_PREFIX + "red_ribbons", redRibbons);
		register(r, LibItemNames.COSMETIC_PREFIX + "pink_flower_bud", pinkFlowerBud);
		register(r, LibItemNames.COSMETIC_PREFIX + "polka_dotted_bows", polkaDottedBows);
		register(r, LibItemNames.COSMETIC_PREFIX + "blue_butterfly", blueButterfly);
		register(r, LibItemNames.COSMETIC_PREFIX + "cat_ears", catEars);
		register(r, LibItemNames.COSMETIC_PREFIX + "witch_pin", witchPin);
		register(r, LibItemNames.COSMETIC_PREFIX + "devil_tail", devilTail);
		register(r, LibItemNames.COSMETIC_PREFIX + "kamui_eye", kamuiEye);
		register(r, LibItemNames.COSMETIC_PREFIX + "googly_eyes", googlyEyes);
		register(r, LibItemNames.COSMETIC_PREFIX + "four_leaf_clover", fourLeafClover);
		register(r, LibItemNames.COSMETIC_PREFIX + "clock_eye", clockEye);
		register(r, LibItemNames.COSMETIC_PREFIX + "unicorn_horn", unicornHorn);
		register(r, LibItemNames.COSMETIC_PREFIX + "devil_horns", devilHorns);
		register(r, LibItemNames.COSMETIC_PREFIX + "hyper_plus", hyperPlus);
		register(r, LibItemNames.COSMETIC_PREFIX + "botanist_emblem", botanistEmblem);
		register(r, LibItemNames.COSMETIC_PREFIX + "ancient_mask", ancientMask);
		register(r, LibItemNames.COSMETIC_PREFIX + "eerie_mask", eerieMask);
		register(r, LibItemNames.COSMETIC_PREFIX + "alien_antenna", alienAntenna);
		register(r, LibItemNames.COSMETIC_PREFIX + "anaglyph_glasses", anaglyphGlasses);
		register(r, LibItemNames.COSMETIC_PREFIX + "orange_shades", orangeShades);
		register(r, LibItemNames.COSMETIC_PREFIX + "groucho_glasses", grouchoGlasses);
		register(r, LibItemNames.COSMETIC_PREFIX + "thick_eyebrows", thickEyebrows);
		register(r, LibItemNames.COSMETIC_PREFIX + "lusitanic_shield", lusitanicShield);
		register(r, LibItemNames.COSMETIC_PREFIX + "tiny_potato_mask", tinyPotatoMask);
		register(r, LibItemNames.COSMETIC_PREFIX + "questgiver_mark", questgiverMark);
		register(r, LibItemNames.COSMETIC_PREFIX + "thinking_hand", thinkingHand);
	}

	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> evt) {
		IForgeRegistry<IRecipeSerializer<?>> r = evt.getRegistry();
		register(r, "ancient_will_attach", AncientWillRecipe.SERIALIZER);
		register(r, "armor_upgrade", ArmorUpgradeRecipe.SERIALIZER);
		register(r, "banner_pattern_apply", BannerRecipe.SERIALIZER);
		register(r, "black_hole_talisman_extract", BlackHoleTalismanExtractRecipe.SERIALIZER);
		register(r, "composite_lens", CompositeLensRecipe.SERIALIZER);
		register(r, "cosmetic_attach", CosmeticAttachRecipe.SERIALIZER);
		register(r, "cosmetic_remove", CosmeticRemoveRecipe.SERIALIZER);
		register(r, "keep_ivy", KeepIvyRecipe.SERIALIZER);
		register(r, "laputa_shard_upgrade", LaputaShardUpgradeRecipe.SERIALIZER);
		register(r, "lens_dye", LensDyeingRecipe.SERIALIZER);
		register(r, "mana_gun_add_clip", ManaGunClipRecipe.SERIALIZER);
		register(r, "mana_gun_add_lens", ManaGunLensRecipe.SERIALIZER);
		register(r, "mana_gun_remove_lens", ManaGunRemoveLensRecipe.SERIALIZER);
		register(r, "mana_upgrade", ManaUpgradeRecipe.SERIALIZER);
		register(r, "mana_upgrade_shapeless", ShapelessManaUpgradeRecipe.SERIALIZER);
		register(r, "merge_vial", MergeVialRecipe.SERIALIZER);
		register(r, "phantom_ink_apply", PhantomInkRecipe.SERIALIZER);
		register(r, "spell_cloth_apply", SpellClothRecipe.SERIALIZER);
		register(r, "split_lens", SplitLensRecipe.SERIALIZER);
		register(r, "terra_pick_tipping", TerraPickTippingRecipe.SERIALIZER);
		register(r, "twig_wand", TwigWandRecipe.SERIALIZER);
		register(r, "gog_alternation", GogAlternationRecipe.SERIALIZER);

		CraftingHelper.register(FluxfieldCondition.SERIALIZER);
		CraftingHelper.register(prefix("fuzzy_nbt"), FuzzyNBTIngredient.SERIALIZER);

		ModBanners.init();
	}

	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> evt) {
		IForgeRegistry<ContainerType<?>> r = evt.getRegistry();

		register(r, Registry.ITEM.getKey(flowerBag), FLOWER_BAG_CONTAINER);
		register(r, Registry.ITEM.getKey(baubleBox), BAUBLE_BOX_CONTAINER);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			ScreenManager.registerFactory(FLOWER_BAG_CONTAINER, GuiFlowerBag::new);
			ScreenManager.registerFactory(BAUBLE_BOX_CONTAINER, GuiBaubleBox::new);
		});
	}

	public static Item getPetal(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whitePetal;
		case ORANGE:
			return orangePetal;
		case MAGENTA:
			return magentaPetal;
		case LIGHT_BLUE:
			return lightBluePetal;
		case YELLOW:
			return yellowPetal;
		case LIME:
			return limePetal;
		case PINK:
			return pinkPetal;
		case GRAY:
			return grayPetal;
		case LIGHT_GRAY:
			return lightGrayPetal;
		case CYAN:
			return cyanPetal;
		case PURPLE:
			return purplePetal;
		case BLUE:
			return bluePetal;
		case BROWN:
			return brownPetal;
		case GREEN:
			return greenPetal;
		case RED:
			return redPetal;
		case BLACK:
			return blackPetal;
		}
	}

}
