package com.mactso.hostilewatermobs.config;

//16.1 - 0.0.0.0 harderfishesandamphibians

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

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
	
	public static int getDebugLevel() {
		return debugLevel;
	}

	public static int getBlockLightLevel() {
		return blockLightLevel;
	}

	public static boolean getRiverGuardianPreysOnVillagerChildren() {
		return riverGuardianPreysOnVillagerChildren;
	}

	public static void setaDebugLevel(int aDebugLevel) {
		MyConfig.debugLevel = aDebugLevel;
	}

	public static int getRiverGuardianSpawnWeight() {
		return riverGuardianSpawnWeight;
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
	
	public static int getSlipperyBiterSpawnWeight() {
		return slipperyBiterSpawnWeight;
	}


	public static int getSlipperyBiterSpawnCap() {
		return slipperyBiterSpawnCap;
	}

	public static int getGurtySpawnWeight() {
		return gurtySpawnWeight;
	}

	public static int getGurtySpawnCap() {
		return gurtySpawnCap;
	}
	
	public static int getWatersnakeSpawnWeight() {
		return watersnakeSpawnWeight;
	}


	public static int getWatersnakeSpawnCap() {
		return watersnakeSpawnCap;
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
	private static int 		blockLightLevel;

	private static boolean  riverGuardianPreysOnVillagerChildren;
	private static int 	    riverGuardianSpawnWeight;
	private static int 	    riverGuardianSpawnCap;
	private static int 	    riverGuardianSoundRange;

	private static int 	    slipperyBiterSpawnWeight;
	private static int 	    slipperyBiterSpawnCap;

	private static int 	    gurtySpawnWeight;
	private static int 	    gurtySpawnCap;
	private static int      gurtyBaseHitPoints;
	private static int      gurtyBaseDefense;
	private static int      gurtyNestDistance;

	private static int 	    watersnakeSpawnWeight;
	private static int 	    watersnakeSpawnCap;
	
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
	public static void onModConfigEvent(final ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}	

	// bad code.  not running currently.
	
	
	public static void pushValues() {
		
		COMMON.debugLevel.set(debugLevel);

		COMMON.blockLightLevel.set(blockLightLevel);

		
		COMMON.riverGuardianPreysOnVillagerChildren.set(riverGuardianPreysOnVillagerChildren);
		COMMON.riverGuardianSpawnWeight.set(riverGuardianSpawnWeight);
		COMMON.riverGuardianSoundRange.set(riverGuardianSoundRange);
		COMMON.riverGuardianSpawnCap.set(riverGuardianSpawnCap);

		COMMON.slipperyBiterSpawnCap.set(slipperyBiterSpawnCap);
		COMMON.slipperyBiterSpawnWeight.set(slipperyBiterSpawnWeight);

		COMMON.gurtySpawnCap.set(gurtySpawnCap);
		COMMON.gurtySpawnWeight.set(gurtySpawnWeight);
		COMMON.gurtyBaseHitPoints.set(gurtyBaseHitPoints);
		COMMON.gurtyBaseDefense.set(gurtyBaseDefense);
		COMMON.gurtyNestDistance.set(gurtyNestDistance);

		COMMON.watersnakeSpawnCap.set(watersnakeSpawnCap);
		COMMON.watersnakeSpawnWeight.set(watersnakeSpawnWeight);
		
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

		blockLightLevel = COMMON.blockLightLevel.get();
		

		riverGuardianSpawnCap = COMMON.riverGuardianSpawnCap.get();
		riverGuardianSpawnWeight = COMMON.riverGuardianSpawnWeight.get();
		riverGuardianSoundRange = COMMON.riverGuardianSoundRange.get();

		slipperyBiterSpawnWeight = COMMON.slipperyBiterSpawnWeight.get();
		slipperyBiterSpawnCap = COMMON.slipperyBiterSpawnCap.get();

		gurtySpawnWeight = COMMON.gurtySpawnWeight.get();
		gurtySpawnCap = COMMON.gurtySpawnCap.get();
		gurtyBaseHitPoints = COMMON.gurtyBaseHitPoints.get();
		gurtyBaseDefense = COMMON.gurtyBaseDefense.get();
		gurtyNestDistance = COMMON.gurtyNestDistance.get();
		modStructureBoost = COMMON.modStructureBoost.get();

		watersnakeSpawnWeight = COMMON.watersnakeSpawnWeight.get();
		watersnakeSpawnCap = COMMON.watersnakeSpawnCap.get();
		
		codSpawnBoost = COMMON.codSpawnBoost.get();
		salmonSpawnBoost = COMMON.salmonSpawnBoost.get();
		squidSpawnBoost = COMMON.squidSpawnBoost.get();
		dolphinSpawnboost = COMMON.dolphinSpawnBoost.get();
		
		zombifiedPiglinSpawnBoost = COMMON.zombifiedPiglinSpawnBoost.get();
		ghastSpawnBoost = COMMON.ghastSpawnBoost.get();		
		
		Utility.debugMsg(1,"Hostile Water Mobs Debug Level: " + debugLevel );
		
	}
	
	public static class Common {

		public final IntValue debugLevel;
		public final BooleanValue riverGuardianPreysOnVillagerChildren;

		public final IntValue blockLightLevel; 
		
		public final IntValue riverGuardianSpawnWeight;
		public final IntValue riverGuardianSpawnCap;
		public final IntValue riverGuardianSoundRange;

		public final IntValue slipperyBiterSpawnWeight;
		public final IntValue slipperyBiterSpawnCap;	
		
		public final IntValue gurtySpawnWeight;
		public final IntValue gurtySpawnCap;
		public final IntValue gurtyBaseHitPoints;
		public final IntValue gurtyBaseDefense;
		public final IntValue gurtyNestDistance;
		public final IntValue modStructureBoost;
		
		public final IntValue watersnakeSpawnWeight;
		public final IntValue watersnakeSpawnCap;			

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

			blockLightLevel = builder
					.comment("blockLightLevel from light emitting blocks that prevents spawn")
					.translation(Main.MODID + ".config." + "blockLightLevel")
					.defineInRange("blockLightLevel", () -> 7, 1, 15);

			builder.pop();
			builder.push("Hostile Water Mobs - River Guardian");

			riverGuardianPreysOnVillagerChildren = builder
					.comment("riverGuardianPreysOnVillagerChildren = true")
					.translation(Main.MODID + ".config." + "riverGuardianPreysOnVillagerChildren")
					.define("riverGuardianPreysOnVillagerChildren", true);
			
			riverGuardianSpawnWeight = builder
					.comment("riverGuardianSpawnWeight")
					.translation(Main.MODID + ".config." + "riverGuardianSpawnWeight")
					.defineInRange("riverGuardianSpawnWeight", () -> 80, 0, 500);

			riverGuardianSpawnCap = builder
					.comment("riverGuardianSpawnCap")
					.translation(Main.MODID + ".config." + "riverGuardianSpawnCap")
					.defineInRange("riverGuardianSpawnCap", () -> 21, 1, 100);

			riverGuardianSoundRange = builder
					.comment("riverGuardian attack SoundRange in meters.  default is 7 meters. 0 turns off attack sound unless the river guardian is attacking the player.")
					.translation(Main.MODID + ".config." + "riverGuardianSoundRange")
					.defineInRange("riverGuardianSoundRange", () -> 17, 0, 24);

			builder.pop();
			builder.push("Hostile Water Mobs - Slippery Biter");

			slipperyBiterSpawnWeight = builder
					.comment("slipperyBiterSpawnWeight")
					.translation(Main.MODID + ".config." + "slipperyBiterSpawnWeight")
					.defineInRange("slipperyBiterSpawnWeight", () -> 80, 0, 500);

			slipperyBiterSpawnCap = builder
					.comment("slipperyBiterSpawnCap")
					.translation(Main.MODID + ".config." + "slipperyBiterSpawnCap")
					.defineInRange("slipperyBiterSpawnCap", () -> 17, 1, 100);

			builder.pop();
			builder.push("Hostile Water Mobs - Gurty");

			gurtySpawnWeight = builder
					.comment("gurtySpawnWeight")
					.translation(Main.MODID + ".config." + "gurtySpawnWeight")
					.defineInRange("gurtySpawnWeight", () -> 80, 0, 500);

			gurtySpawnCap = builder
					.comment("gurtySpawnCap")
					.translation(Main.MODID + ".config." + "gurtySpawnCap")
					.defineInRange("gurtySpawnCap", () -> 9, 1, 100);

			gurtyBaseHitPoints = builder
					.comment("gurtyBaseHitPoints : note- this doesn't seem to work.")
					.translation(Main.MODID + ".config." + "gurtyBaseHitPoints")
					.defineInRange("gurtyBaseHitPoints", () -> 18, 0, 100);
			
			gurtyBaseDefense = builder
					.comment("gurtyBaseDefense")
					.translation(Main.MODID + ".config." + "gurtyBaseDefense")
					.defineInRange("gurtyBaseDefense", () -> 2, 2, 10);
			
			gurtyNestDistance = builder
					.comment("gurtyNestDistance")
					.translation(Main.MODID + ".config." + "gurtyNestDistance")
					.defineInRange("gurtyNestDistance", () -> 6, 2, 15);
			
			builder.pop();
			
			builder.push("Hostile Water Mobs - Watersnake");

			watersnakeSpawnWeight = builder
					.comment("watersnakeSpawnWeight")
					.translation(Main.MODID + ".config." + "watersnakeSpawnWeight")
					.defineInRange("watersnakeSpawnWeight", () -> 80, 0, 500);

			watersnakeSpawnCap = builder
					.comment("watersnakeSpawnCap")
					.translation(Main.MODID + ".config." + "watersnakeSpawnCap")
					.defineInRange("watersnakeSpawnCap", () -> 17, 1, 100);

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

}

