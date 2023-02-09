package com.mactso.hostilewatermobs.sound;

import com.mactso.hostilewatermobs.Main;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds
{
	
	public static final SoundEvent RIVER_GUARDIAN_HURT = create("riverguardian.hurt");
	public static final SoundEvent RIVER_GUARDIAN_HURT_LAND = create("riverguardian.hurt_land");
	public static final SoundEvent RIVER_GUARDIAN_DEATH = create("riverguardian.death");
	public static final SoundEvent RIVER_GUARDIAN_DEATH_LAND = create("riverguardian.death_land");
	public static final SoundEvent RIVER_GUARDIAN_AMBIENT = create("riverguardian.ambient");
	public static final SoundEvent RIVER_GUARDIAN_FLOP= create("riverguardian.flop");

	public static final SoundEvent SLIPPERY_BITER_HURT = create("slipperybiter.hurt");
	public static final SoundEvent SLIPPERY_BITER_DEATH = create("slipperybiter.death");
	public static final SoundEvent SLIPPERY_BITER_AMBIENT = create("slipperybiter.ambient");
	public static final SoundEvent SLIPPERY_BITER_FLOP = create("slipperybiter.flop");

	public static final SoundEvent GURTY_AMBIENT = create("gurty.ambient");
	public static final SoundEvent GURTY_ANGRY = create("gurty.angry");
	public static final SoundEvent GURTY_HURT = create("gurty.hurt");
	public static final SoundEvent GURTY_DEATH = create("gurty.death");
	public static final SoundEvent GURTY_STEP = create("gurty.step");
	
	public static final SoundEvent WATER_SNAKE_AMBIENT = create("watersnake.ambient");
	public static final SoundEvent WATER_SNAKE_ANGRY = create("watersnake.angry");
	public static final SoundEvent WATER_SNAKE_HURT = create("watersnake.hurt");
	public static final SoundEvent WATER_SNAKE_DEATH = create("watersnake.death");
	public static final SoundEvent WATER_SNAKE_STEP = create("watersnake.step");
	
private static SoundEvent create(String key)
	{
		ResourceLocation res = new ResourceLocation(Main.MODID, key);
		SoundEvent ret = new SoundEvent(res);
		ret.setRegistryName(res);
		return ret;
	}

	public static void register(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(RIVER_GUARDIAN_HURT);
		registry.register(RIVER_GUARDIAN_HURT_LAND);
		registry.register(RIVER_GUARDIAN_DEATH);
		registry.register(RIVER_GUARDIAN_DEATH_LAND);
		registry.register(RIVER_GUARDIAN_AMBIENT);
		registry.register(RIVER_GUARDIAN_FLOP);

		registry.register(SLIPPERY_BITER_AMBIENT);
		registry.register(SLIPPERY_BITER_FLOP);
		registry.register(SLIPPERY_BITER_HURT);
		registry.register(SLIPPERY_BITER_DEATH);

		registry.register(GURTY_AMBIENT);
		registry.register(GURTY_ANGRY);
		registry.register(GURTY_HURT);
		registry.register(GURTY_STEP);
		registry.register(GURTY_DEATH);

		registry.register(WATER_SNAKE_AMBIENT);
		registry.register(WATER_SNAKE_ANGRY);
		registry.register(WATER_SNAKE_HURT);
		registry.register(WATER_SNAKE_STEP);
		registry.register(WATER_SNAKE_DEATH);	
	}
}
