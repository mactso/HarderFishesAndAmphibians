package com.mactso.hostilewatermobs.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hostilewatermobs.config.MyConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

public class Utility {
	final static int TWO_SECONDS = 40;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static String NONE = BiomeCategory.NONE.getName();
	public static String BEACH = BiomeCategory.BEACH.getName();
	public static String BADLANDS = BiomeCategory.MESA.getName();
	public static String DESERT = BiomeCategory.DESERT.getName();
	public static String EXTREME_HILLS = BiomeCategory.EXTREME_HILLS.getName();
	public static String ICY = BiomeCategory.ICY.getName();
	public static String JUNGLE = BiomeCategory.JUNGLE.getName();
	public static String THEEND = BiomeCategory.THEEND.getName();
	public static String FOREST = BiomeCategory.FOREST.getName();
	public static String MESA = BiomeCategory.MESA.getName();
	public static String MUSHROOM = BiomeCategory.MUSHROOM.getName();
	public static String MOUNTAIN = BiomeCategory.MOUNTAIN.getName();
	public static String NETHER = BiomeCategory.NETHER.getName();
	public static String OCEAN = BiomeCategory.OCEAN.getName();
	public static String PLAINS = BiomeCategory.PLAINS.getName();
	public static String RIVER = BiomeCategory.RIVER.getName();
	public static String SAVANNA = BiomeCategory.SAVANNA.getName();
	public static String SWAMP = BiomeCategory.SWAMP.getName();
	public static String TAIGA = BiomeCategory.TAIGA.getName();
	public static String UNDERGROUND = BiomeCategory.UNDERGROUND.getName();
	
	
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
	
	private static ResourceKey<Biome> getBiomeKey(LevelAccessor level, BlockPos pos) {
	ResourceLocation biomeNameResourceKey = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
			.getKey(level.getBiome(pos));
	return ResourceKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);
}
	
	public static boolean isInBubbleColumn(LevelAccessor level, BlockPos pos) {
		return level.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}
	
	public static boolean isOcean(LevelAccessor level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.OCEAN) {
			return true;
		}

		ResourceKey<Biome> biomeKey = getBiomeKey(level,pos);
		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.OCEAN)) {
			return true;
		}

		return false;

	}

	
	public static <T extends Entity> boolean isOverCrowded(LevelAccessor level, Class<T> entityClass, BlockPos pos, int crowdValue) {
        if (level.getEntitiesOfClass(entityClass,
                new AABB(pos.north(20).west(20).above(6), pos.south(20).east(20).below(6))).size() > crowdValue)
            return true;
        return false;
    }
	
	
	public static boolean isSpawnRateThrottled(LevelAccessor level, int throttleChance) {
		if (level.getRandom().nextInt(100) < throttleChance) {
			return true;
		}
		return false;
	}
	
	public static void sendBoldChat(Player p, String chatMessage, ChatFormatting textColor) {

		TextComponent component = new TextComponent (chatMessage);
		component.setStyle(component.getStyle().withBold(true));
		component.setStyle(component.getStyle().withColor(ChatFormatting.DARK_GREEN));
		p.sendMessage(component, p.getUUID());

	}	
    
	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor) {

		TextComponent component = new TextComponent (chatMessage);
		component.setStyle(component.getStyle().withColor(ChatFormatting.GREEN));
		p.sendMessage(component, p.getUUID());

	}
	
	public static boolean updateEffect(LivingEntity e, int amplifier,  MobEffect mobEffect, int duration) {
		MobEffectInstance ei = e.getEffect(mobEffect);
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
		e.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, true, true));
		return true;
	}
	
	public static boolean populateEntityType(EntityType<?> et, ServerLevel level, BlockPos savePos, int range,
			int modifier) {
		boolean isBaby = false;
		return populateEntityType(et, level, savePos, range, modifier, isBaby);
	}

	public static boolean populateEntityType(EntityType<?> et, ServerLevel level, BlockPos savePos, int range,
			int modifier, boolean isBaby) {
		boolean persistant = false;
		return populateEntityType(et, level, savePos, range, modifier, persistant, isBaby);
	}
	
	public static boolean populateEntityType(EntityType<?> et, ServerLevel level, BlockPos savePos, int range,
			int modifier, boolean persistant, boolean isBaby) {
		int numZP;
		Mob e;
		numZP = level.random.nextInt(range) - modifier;
		if (numZP < 0)
			return false;
		for (int i = 0; i <= numZP; i++) {
			if (et == EntityType.PHANTOM) {
				e = (Mob) et.spawn(level, null, null, null, savePos.north(2).west(2), MobSpawnType.SPAWNER, true, true);
			} else {
				e = (Mob) et.spawn(level, null, null, null, savePos.north(2).west(2), MobSpawnType.NATURAL, true, true);
			}
			if (persistant) {
				e.setPersistenceRequired();
			}
			if (et == EntityType.ZOMBIFIED_PIGLIN) {
				e.setAggressive(true);
			}
			e.setBaby(isBaby);
		}
		return true;
	}
	
	public static boolean isOutside(BlockPos pos, ServerLevel serverLevel) {
		return serverLevel.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, pos) == pos;
	}

	public static Item getItemFromString (String name)
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

}
