package com.mactso.hostilewatermobs.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hostilewatermobs.config.MyConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

public class Utility {
	
	final static int TWO_SECONDS = 40;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static String NONE = "none";
	public static String BEACH = "beach";
	public static String BADLANDS = "badlands";
	public static String DESERT = "desert";
	public static String EXTREME_HILLS = "extreme_hills";
	public static String ICY = "icy";
	public static String JUNGLE = "jungle";
	public static String THEEND = "the_end";
	public static String FOREST = "forest";
	public static String MESA = "mesa";
	public static String MUSHROOM = "mushroom";
	public static String MOUNTAIN = "mountain";
	public static String NETHER = "nether";
	public static String OCEAN = "ocean";
	public static String PLAINS = "plains";
	public static String RIVER = "river";
	public static String SAVANNA = "savanna";
	public static String SWAMP = "swamp";
	public static String TAIGA = "taiga";
	public static String UNDERGROUND = "underground";
	
	
	
	public static void debugMsg (int level, String dMsg) {

		if (MyConfig.getDebugLevel() > level-1) {
			LOGGER.info("L"+level + ":" + dMsg);
		}
		
	}

	public static void debugMsg (int level, BlockPos pos, String dMsg) {

		if (MyConfig.getDebugLevel() > level-1) {
			LOGGER.info("L"+level+" ("+pos.getX()+","+pos.getY()+","+pos.getZ()+"): " + dMsg);
		}
		
	}

	public static void sendBoldChat(Player p, String chatMessage, ChatFormatting textColor) {
		MutableComponent component = Component.literal(chatMessage);
		component.setStyle(component.getStyle().withBold(true));
		component.setStyle(component.getStyle().withColor(textColor));
		p.sendSystemMessage(component);

	}

	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor) {

		MutableComponent component = Component.literal(chatMessage);
		component.setStyle(component.getStyle().withColor(textColor));
		p.sendSystemMessage(component);

	}

	public static boolean isInBubbleColumn(LevelAccessor level, BlockPos pos) {
		return level.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}
	
	public static boolean isOcean(LevelAccessor level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.OCEAN) {
			return true;
		}

		if (level.getBiome(pos).is(BiomeTags.IS_OCEAN)) {
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
				e = (Mob) et.spawn(level, savePos.north(2).west(2), MobSpawnType.SPAWNER);
			} else {
				e = (Mob) et.spawn(level, savePos.north(2).west(2), MobSpawnType.NATURAL);
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

	
	public static String getBiomeCategory(Holder<Biome> testBiome) {
		
	if (testBiome.is(BiomeTags.HAS_VILLAGE_DESERT))
		return Utility.DESERT;
	if (testBiome.is(BiomeTags.IS_FOREST))
		return Utility.FOREST;
	if (testBiome.is(BiomeTags.IS_BEACH))
		return Utility.BEACH;
	if (testBiome.is(BiomeTags.HAS_VILLAGE_SNOWY))
		return Utility.ICY;		
	if (testBiome.is(BiomeTags.IS_JUNGLE))
		return Utility.JUNGLE;		
	if (testBiome.is(BiomeTags.IS_OCEAN))
		return Utility.OCEAN;		
	if (testBiome.is(BiomeTags.IS_DEEP_OCEAN))
		return Utility.OCEAN;		
	if (testBiome.is(BiomeTags.HAS_VILLAGE_PLAINS))
		return Utility.PLAINS;		
	if (testBiome.is(BiomeTags.IS_RIVER))
		return Utility.RIVER;		
	if (testBiome.is(BiomeTags.HAS_VILLAGE_SAVANNA))
		return Utility.SAVANNA;		
	if (testBiome.is(BiomeTags.HAS_SWAMP_HUT))
		return Utility.SWAMP;		
	if (testBiome.is(BiomeTags.HAS_RUINED_PORTAL_SWAMP))
		return Utility.SWAMP;		
	if (testBiome.is(BiomeTags.IS_TAIGA))
		return Utility.TAIGA;		
	if (testBiome.is(BiomeTags.IS_BADLANDS))
		return Utility.BADLANDS;		
	if (testBiome.is(BiomeTags.IS_MOUNTAIN))
		return Utility.EXTREME_HILLS;		
	if (testBiome.is(BiomeTags.IS_NETHER))
		return Utility.NETHER;		
	
	return NONE;

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
