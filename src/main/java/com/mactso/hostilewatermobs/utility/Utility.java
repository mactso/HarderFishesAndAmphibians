package com.mactso.hostilewatermobs.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hostilewatermobs.config.MyConfig;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraftforge.common.BiomeDictionary;

public class Utility {
	final static int TWO_SECONDS = 40;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static String NONE = Category.NONE.getName();
	public static String BEACH = Category.BEACH.getName();
	public static String BADLANDS = Category.MESA.getName();
	public static String DESERT = Category.DESERT.getName();
	public static String EXTREME_HILLS = Category.EXTREME_HILLS.getName();
	public static String ICY = Category.ICY.getName();
	public static String JUNGLE = Category.JUNGLE.getName();
	public static String THEEND = Category.THEEND.getName();
	public static String FOREST = Category.FOREST.getName();
	public static String MESA = Category.MESA.getName();
	public static String MUSHROOM = Category.MUSHROOM.getName();
//	public static String MOUNTAIN = Category.MOUNTAIN.getName();
	public static String NETHER = Category.NETHER.getName();
	public static String OCEAN = Category.OCEAN.getName();
	public static String PLAINS = Category.PLAINS.getName();
	public static String RIVER = Category.RIVER.getName();
	public static String SAVANNA = Category.SAVANNA.getName();
	public static String SWAMP = Category.SWAMP.getName();
	public static String TAIGA = Category.TAIGA.getName();
//	public static String UNDERGROUND = Category.UNDERGROUND.getName();
	
	public static void debugMsg (int level, BlockPos pos, String dMsg) {

		if (MyConfig.getDebugLevel() > level-1) {
			LOGGER.info("L"+level+" ("+pos.getX()+","+pos.getY()+","+pos.getZ()+"): " + dMsg);
		}
		
	}

	public static void debugMsg (int level, String dMsg) {

		if (MyConfig.getDebugLevel() > level-1) {
			LOGGER.info("L"+level + ":" + dMsg);
		}
		
	}
	
	public static String getBiomeCategory(Biome testBiome) {

		return testBiome.getBiomeCategory().getName();

	}
	
	private static RegistryKey<Biome> getBiomeKey(IWorld level, BlockPos pos) {
	ResourceLocation biomeNameResourceKey = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
			.getKey(level.getBiome(pos));
	return RegistryKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);
}
	
	public static boolean isInBubbleColumn(IWorld world, BlockPos pos) {
		return world.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}
	
	public static boolean isOcean(IWorld level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.OCEAN) {
			return true;
		}

		RegistryKey<Biome> biomeKey = getBiomeKey(level,pos);
		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.OCEAN)) {
			return true;
		}

		return false;

	}
	
	public static <T extends Entity> boolean isOverCrowded(IWorld level, Class<T> entityClass, BlockPos pos, int crowdValue) {
        if (level.getEntitiesOfClass(entityClass,
                new AxisAlignedBB(pos.north(20).west(20).above(6), pos.south(20).east(20).below(6))).size() > crowdValue)
            return true;
        return false;
    }
	
	
	public static boolean isSpawnRateThrottled(IWorld level, int throttleChance) {
		if (level.getRandom().nextInt(100) < throttleChance) {
			return true;
		}
		return false;
	}
	
    // support for any color, optionally bold text.
	public static void sendBoldChat(PlayerEntity p, String chatMessage, Color color) {
		StringTextComponent component = new StringTextComponent (chatMessage);

		component.getStyle().withBold(true);
		component.getStyle().withColor(color);
		
		p.sendMessage(component, p.getUUID());
	}
    
	// support for any color chattext
	public static void sendChat(PlayerEntity p, String chatMessage, Color color) {
		StringTextComponent component = new StringTextComponent (chatMessage);
		component.getStyle().withColor(color);
		p.sendMessage(component, p.getUUID());
	}
	
	public static boolean updateEffect(LivingEntity e, int amplifier,  Effect mobEffect, int duration) {
		EffectInstance ei = e.getEffect(mobEffect);
		if (amplifier == 10) {
			amplifier = 20;  // player "plaid" speed.
		}
		if (ei != null) {
			if (amplifier > ei.getAmplifier()) {
				e.removeEffect(mobEffect);
			} 
			if (amplifier == ei.getAmplifier() && ei.getDuration() > 10) {
				return false;
			}
			if (ei.getDuration() > 10) {
				return false;
			}
			e.removeEffect(mobEffect);			
		}
		e.addEffect(new EffectInstance(mobEffect, duration, amplifier, true, true));
		return true;
	}


}
