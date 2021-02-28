package com.mactso.hostilewatermobs.config;

//16.1 - 0.0.0.0 harderfishesandamphibians

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hostilewatermobs.Main;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	static
	{
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
	
	public static int getaDebugLevel() {
		return debugLevel;
	}

	public static boolean getRiverGuardianPreysOnVillagerChildren() {
		return riverGuardianPreysOnVillagerChildren;
	}

	public static void setaDebugLevel(int aDebugLevel) {
		MyConfig.debugLevel = aDebugLevel;
	}

	public static int getRiverGuardianSpawnChance() {
		return riverGuardianSpawnChance;
	}

	public static int getRiverGuardianSoundRange() {
		// old/bad configuration protection
		if (riverGuardianSoundRange < 1) {
			return 1;
		}
		return riverGuardianSoundRange;
	}
	
	public static int getRiverGuardianSpawnCap() {
		return riverGuardianSpawnCap;
	}
	
	public static int getSlipperyBiterSpawnChance() {
		return slipperyBiterSpawnChance;
	}


	public static int getSlipperyBiterSpawnCap() {
		return slipperyBiterSpawnCap;
	}

	public static int getGurtySpawnChance() {
		return gurtySpawnChance;
	}

	public static int getGurtySpawnCap() {
		return gurtySpawnCap;
	}

	public static int getGurtyBaseHitPoints() {
		return gurtyBaseHitPoints;
	}

	public static int getGurtyBaseDefense() {
		return gurtyBaseDefense;
	}

	public static int getGurtyNestDistance() {
		return gurtyNestDistance;
	}

	public static int getModStructureBoost() {
		return modStructureBoost;
	}
	
	public static int getCodSpawnBoost() {
		return codSpawnBoost;
	}
	
	public static int getSalmonSpawnBoost() {
		return salmonSpawnBoost;
	}
	
	public static int getSquidSpawnBoost() {
		return squidSpawnBoost;
	}

	// Experimental.   Possible fix for underpopulated Nether.
	public static int getZombifiedPiglinSpawnBoost() {
		return zombifiedPiglinSpawnBoost;
	}

	public static int getGhastSpawnBoost() {
		return ghastSpawnBoost;
	}

	
	public static int getDolphinSpawnboost() {
		return dolphinSpawnboost;
	}
	
	private static int      debugLevel;

	private static boolean  riverGuardianPreysOnVillagerChildren;
	private static int 	    riverGuardianSpawnChance;
	private static int 	    riverGuardianSpawnCap;
	private static int 	    riverGuardianSoundRange;

	private static int 	    slipperyBiterSpawnChance;
	private static int 	    slipperyBiterSpawnCap;

	private static int 	    gurtySpawnChance;
	private static int 	    gurtySpawnCap;
	private static int      gurtyBaseHitPoints;
	private static int      gurtyBaseDefense;
	private static int      gurtyNestDistance;

	private static int      modStructureBoost;
	
	private static int 	    codSpawnBoost;
	private static int 	    salmonSpawnBoost;
	private static int 	    squidSpawnBoost;
	private static int 	    dolphinSpawnboost;
	


	private static int      zombifiedPiglinSpawnBoost;
	private static int      ghastSpawnBoost;


	public static final int KILLER_ANY   = 0;
	public static final int KILLER_MOB_OR_PLAYER = 1;
	public static final int KILLER_PLAYER = 2;

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}	

	// bad code.  not running currently.
	
	
	public static void pushValues() {
		
		COMMON.debugLevel.set(debugLevel);

		COMMON.riverGuardianPreysOnVillagerChildren.set(riverGuardianPreysOnVillagerChildren);
		COMMON.riverGuardianSpawnChance.set(riverGuardianSpawnChance);
		COMMON.riverGuardianSoundRange.set(riverGuardianSoundRange);
		COMMON.riverGuardianSpawnCap.set(riverGuardianSpawnCap);

		COMMON.slipperyBiterSpawnCap.set(slipperyBiterSpawnCap);
		COMMON.slipperyBiterSpawnChance.set(slipperyBiterSpawnChance);

		COMMON.gurtySpawnCap.set(gurtySpawnCap);
		COMMON.gurtySpawnChance.set(gurtySpawnChance);
		COMMON.gurtyBaseHitPoints.set(gurtyBaseHitPoints);
		COMMON.gurtyBaseDefense.set(gurtyBaseDefense);
		COMMON.gurtyNestDistance.set(gurtyNestDistance);

		COMMON.codSpawnBoost.set(codSpawnBoost);
		COMMON.salmonSpawnBoost.set(salmonSpawnBoost);
		COMMON.squidSpawnBoost.set(squidSpawnBoost);
		COMMON.dolphinSpawnBoost.set(dolphinSpawnboost);
		COMMON.zombifiedPiglinSpawnBoost.set(zombifiedPiglinSpawnBoost);
		COMMON.ghastSpawnBoost.set(ghastSpawnBoost);
	}
	
	// remember need to push each of these values separately once we have commands.
	public static void bakeConfig()
	{

		debugLevel = COMMON.debugLevel.get();

		riverGuardianSpawnChance = COMMON.riverGuardianSpawnChance.get();
		riverGuardianSpawnCap = COMMON.riverGuardianSpawnCap.get();
		riverGuardianSoundRange = COMMON.riverGuardianSoundRange.get();

		slipperyBiterSpawnChance = COMMON.slipperyBiterSpawnChance.get();
		slipperyBiterSpawnCap = COMMON.slipperyBiterSpawnCap.get();

		gurtySpawnChance = COMMON.gurtySpawnChance.get();
		gurtySpawnCap = COMMON.gurtySpawnCap.get();
		gurtyBaseHitPoints = COMMON.gurtyBaseHitPoints.get();
		gurtyBaseDefense = COMMON.gurtyBaseDefense.get();
		gurtyNestDistance = COMMON.gurtyNestDistance.get();
		modStructureBoost = COMMON.modStructureBoost.get();

		codSpawnBoost = COMMON.codSpawnBoost.get();
		salmonSpawnBoost = COMMON.salmonSpawnBoost.get();
		squidSpawnBoost = COMMON.squidSpawnBoost.get();
		dolphinSpawnboost = COMMON.dolphinSpawnBoost.get();
		
		zombifiedPiglinSpawnBoost = COMMON.zombifiedPiglinSpawnBoost.get();
		ghastSpawnBoost = COMMON.ghastSpawnBoost.get();		
		
		if (debugLevel > 0) {
			System.out.println("Harder Farther Debug Level: " + debugLevel );
		}
	}
	
	public static class Common {

		public final IntValue debugLevel;
		public final BooleanValue riverGuardianPreysOnVillagerChildren;

		public final IntValue riverGuardianSpawnChance;
		public final IntValue riverGuardianSpawnCap;
		public final IntValue riverGuardianSoundRange;

		public final IntValue slipperyBiterSpawnChance;
		public final IntValue slipperyBiterSpawnCap;	
		
		public final IntValue gurtySpawnChance;
		public final IntValue gurtySpawnCap;
		public final IntValue gurtyBaseHitPoints;
		public final IntValue gurtyBaseDefense;
		public final IntValue gurtyNestDistance;
		public final IntValue modStructureBoost;
		
		public final IntValue codSpawnBoost;
		public final IntValue salmonSpawnBoost;
		public final IntValue squidSpawnBoost;
		public final IntValue dolphinSpawnBoost;
		
		public final IntValue zombifiedPiglinSpawnBoost;
		public final IntValue ghastSpawnBoost;
		
		public Common(ForgeConfigSpec.Builder builder) {

			builder.push("Hostile Water Mobs");
			
			builder.push("Hostile Water Mobs - General");
			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);

			builder.pop();
			builder.push("Hostile Water Mobs - River Guardian");

			riverGuardianPreysOnVillagerChildren = builder
					.comment("riverGuardianPreysOnVillagerChildren = true")
					.translation(Main.MODID + ".config." + "riverGuardianPreysOnVillagerChildren")
					.define("riverGuardianPreysOnVillagerChildren", true);
			
			riverGuardianSpawnChance = builder
					.comment("riverGuardianSpawnChance Weight")
					.translation(Main.MODID + ".config." + "riverGuardianSpawnChance")
					.defineInRange("riverGuardianSpawnChance", () -> 30, 0, 500);

			riverGuardianSpawnCap = builder
					.comment("riverGuardianSpawnCap")
					.translation(Main.MODID + ".config." + "riverGuardianSpawnCap")
					.defineInRange("riverGuardianSpawnCap", () -> 37, 1, 100);

			riverGuardianSoundRange = builder
					.comment("riverGuardianSoundRange")
					.translation(Main.MODID + ".config." + "riverGuardianSoundRange")
					.defineInRange("riverGuardianSoundRange", () -> 15, 1, 24);

			builder.pop();
			builder.push("Hostile Water Mobs - Slippery Biter");

			slipperyBiterSpawnChance = builder
					.comment("slipperyBiterSpawnChance Weight")
					.translation(Main.MODID + ".config." + "slipperyBiterSpawnChance")
					.defineInRange("slipperyBiterSpawnChance", () -> 30, 0, 500);

			slipperyBiterSpawnCap = builder
					.comment("slipperyBiterSpawnCap")
					.translation(Main.MODID + ".config." + "slipperyBiterSpawnCap")
					.defineInRange("slipperyBiterSpawnCap", () -> 21, 1, 100);

			builder.pop();
			builder.push("Hostile Water Mobs - Gurty");

			gurtySpawnChance = builder
					.comment("gurtySpawnChance Weight")
					.translation(Main.MODID + ".config." + "gurtySpawnChance")
					.defineInRange("gurtySpawnChance", () -> 30, 0, 500);

			gurtySpawnCap = builder
					.comment("gurtySpawnCap")
					.translation(Main.MODID + ".config." + "gurtySpawnCap")
					.defineInRange("gurtySpawnCap", () -> 21, 1, 100);

			gurtyBaseHitPoints = builder
					.comment("gurtyBaseHitPoints")
					.translation(Main.MODID + ".config." + "gurtyBaseHitPoints")
					.defineInRange("gurtyBaseHitPoints", () -> 10, 0, 100);
			
			gurtyBaseDefense = builder
					.comment("gurtyBaseDefense")
					.translation(Main.MODID + ".config." + "gurtyBaseDefense")
					.defineInRange("gurtyBaseDefense", () -> 2, 2, 10);
			
			gurtyNestDistance = builder
					.comment("gurtyNestDistance")
					.translation(Main.MODID + ".config." + "gurtyNestDistance")
					.defineInRange("gurtyNestDistance", () -> 6, 2, 15);
			
			builder.pop();
			builder.push("Mod Structure Boost - Add Zo,Sk,W*0.16 to modded structures");

			modStructureBoost = builder
					.comment("modStructureBoost")
					.translation(Main.MODID + ".config." + "modStructureBoost")
					.defineInRange("modStructureBoost", () -> 10, 0, 100);

			builder.pop();
			builder.push("Hostile Water Mobs - Fish Spawn Boost");

			codSpawnBoost = builder
					.comment("codSpawnBoost")
					.translation(Main.MODID + ".config." + "codSpawnBoost")
					.defineInRange("codSpawnBoost", () -> 15, 1, 100);
			salmonSpawnBoost = builder
					.comment("salmonSpawnBoost")
					.translation(Main.MODID + ".config." + "salmonSpawnBoost")
					.defineInRange("salmonSpawnBoost", () -> 15, 1, 100);
			squidSpawnBoost = builder
					.comment("squidSpawnBoost")
					.translation(Main.MODID + ".config." + "squidSpawnBoost")
					.defineInRange("squidSpawnBoost", () -> 10, 1, 100);
			dolphinSpawnBoost = builder
					.comment("dolphinSpawnBoost")
					.translation(Main.MODID + ".config." + "dolphinSpawnBoost")
					.defineInRange("dolphinSpawnBoost", () -> 10, 1, 100);

			builder.pop();
			builder.push("Hostile Water Mobs - Experimental Nether Spawn Fix");
			zombifiedPiglinSpawnBoost = builder
					.comment("zombiePiglinSpawnBoost ")
					.translation(Main.MODID + ".config." + "zombifiedPiglinSpawnBoost ")
					.defineInRange("zombifiedPiglinSpawnBoost ", () -> 25, 0, 100);
			
			ghastSpawnBoost = builder
					.comment("ghastSpawnBoost ")
					.translation(Main.MODID + ".config." + "ghastSpawnBoost ")
					.defineInRange("ghastSpawnBoost ", () -> 30, 0, 100);
			builder.pop();
			
			builder.pop();
			
		}
	}
	
	private static Item getItemFromString (String name)
	{
		Item ret = Items.PAPER;
		try {
			ResourceLocation key = new ResourceLocation(name);
			if (ForgeRegistries.ITEMS.containsKey(key))
			{
				ret = ForgeRegistries.ITEMS.getValue(key);
			}
			else
				LOGGER.warn("Unknown item: " + name);
		}
		catch (Exception e)
		{
			LOGGER.warn("Bad item: " + name);
		}
		return ret;
	}
	
	// support for any color chattext
	public static void sendChat(PlayerEntity p, String chatMessage, Color color) {
		StringTextComponent component = new StringTextComponent (chatMessage);
		component.getStyle().setColor(color);
		p.sendMessage(component, p.getUniqueID());
	}
	
	// support for any color, optionally bold text.
	public static void sendBoldChat(PlayerEntity p, String chatMessage, Color color) {
		StringTextComponent component = new StringTextComponent (chatMessage);

		component.getStyle().setBold(true);
		component.getStyle().setColor(color);
		
		p.sendMessage(component, p.getUniqueID());
	}
	
}

