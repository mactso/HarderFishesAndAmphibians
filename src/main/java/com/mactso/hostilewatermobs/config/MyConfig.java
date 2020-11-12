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
		return aDebugLevel;
	}



	public static void setaDebugLevel(int aDebugLevel) {
		MyConfig.aDebugLevel = aDebugLevel;
	}

	public static int getRiverGuardianSpawnChance() {
		return riverGuardianSpawnChance;
	}

	public static int getRiverGuardianSpawnCap() {
		return riverGuardianSpawnCap;
	}
	
	private static int      aDebugLevel;
	private static int 	    riverGuardianSpawnChance;
	private static int 	    riverGuardianSpawnCap;


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


	
	public static void pushValues() {
		COMMON.debugLevel.set(aDebugLevel);
		COMMON.riverGuardianSpawnChance.set(riverGuardianSpawnChance);
		COMMON.riverGuardianSpawnCap.set(riverGuardianSpawnCap);
	}
	
	// remember need to push each of these values separately once we have commands.
	public static void bakeConfig()
	{

		aDebugLevel = COMMON.debugLevel.get();
		riverGuardianSpawnChance = COMMON.riverGuardianSpawnChance.get();
		riverGuardianSpawnCap = COMMON.riverGuardianSpawnCap.get();
		
		if (aDebugLevel > 0) {
			System.out.println("Harder Farther Debug Level: " + aDebugLevel );
		}
	}
	
	public static class Common {

		public final IntValue debugLevel;
		public final IntValue riverGuardianSpawnChance;
		public final IntValue riverGuardianSpawnCap;
		
		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Hostile Water Mobs");
			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);

			riverGuardianSpawnChance = builder
					.comment("riverGuardianSpawnChance")
					.translation(Main.MODID + ".config." + "riverGuardianSpawnChance")
					.defineInRange("riverGuardianSpawnChance", () -> 6, 0, 100);

			riverGuardianSpawnCap = builder
					.comment("riverGuardianSpawnCap")
					.translation(Main.MODID + ".config." + "riverGuardianSpawnCap")
					.defineInRange("riverGuardianSpawnCap", () -> 41, 0, 100);
			
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

