package com.mactso.hostilewatermobs.sound;

import com.mactso.hostilewatermobs.Main;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds
{
	
	public static final SoundEvent RIVER_GUARDIAN_HURT = create("river_guardian.hurt");
	public static final SoundEvent RIVER_GUARDIAN_HURT_LAND = create("river_guardian.hurt_land");
	public static final SoundEvent RIVER_GUARDIAN_DEATH = create("river_guardian.death");
	public static final SoundEvent RIVER_GUARDIAN_DEATH_LAND = create("river_guardian.death_land");
	public static final SoundEvent RIVER_GUARDIAN_AMBIENT = create("river_guardian.ambient");
	public static final SoundEvent RIVER_GUARDIAN_FLOP= create("river_guardian.flop");

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
		return ret;
	}

	public static void register(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(RIVER_GUARDIAN_HURT.getLocation(),RIVER_GUARDIAN_HURT);
		registry.register(RIVER_GUARDIAN_HURT_LAND.getLocation(),RIVER_GUARDIAN_HURT_LAND);
		registry.register(RIVER_GUARDIAN_DEATH.getLocation(),RIVER_GUARDIAN_DEATH);
		registry.register(RIVER_GUARDIAN_DEATH_LAND.getLocation(),RIVER_GUARDIAN_DEATH_LAND);
		registry.register(RIVER_GUARDIAN_AMBIENT.getLocation(),RIVER_GUARDIAN_AMBIENT);
		registry.register(RIVER_GUARDIAN_FLOP.getLocation(),RIVER_GUARDIAN_FLOP);

		registry.register(SLIPPERY_BITER_AMBIENT.getLocation(),SLIPPERY_BITER_AMBIENT);
		registry.register(SLIPPERY_BITER_FLOP.getLocation(),SLIPPERY_BITER_FLOP);
		registry.register(SLIPPERY_BITER_HURT.getLocation(),SLIPPERY_BITER_HURT);
		registry.register(SLIPPERY_BITER_DEATH.getLocation(),SLIPPERY_BITER_DEATH);

		registry.register(GURTY_AMBIENT.getLocation(),GURTY_AMBIENT);
		registry.register(GURTY_ANGRY.getLocation(),GURTY_ANGRY);
		registry.register(GURTY_HURT.getLocation(),GURTY_HURT);
		registry.register(GURTY_STEP.getLocation(),GURTY_STEP);
		registry.register(GURTY_DEATH.getLocation(),GURTY_DEATH);

		registry.register(WATER_SNAKE_AMBIENT.getLocation(),WATER_SNAKE_AMBIENT);
		registry.register(WATER_SNAKE_ANGRY.getLocation(),WATER_SNAKE_ANGRY);
		registry.register(WATER_SNAKE_HURT.getLocation(),WATER_SNAKE_HURT);
		registry.register(WATER_SNAKE_STEP.getLocation(),WATER_SNAKE_STEP);
		registry.register(WATER_SNAKE_DEATH.getLocation(),WATER_SNAKE_DEATH);	
	}
	

}
