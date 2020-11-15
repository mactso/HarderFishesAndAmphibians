package com.mactso.hostilewatermobs.sound;

import com.mactso.hostilewatermobs.Main;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds
{
	
	public static final SoundEvent RIVER_GUARDIAN_HURT = create("river_guardian.hurt");
	public static final SoundEvent RIVER_GUARDIAN_LAND_HURT = create("river_guardian.land_hurt");
	public static final SoundEvent RIVER_GUARDIAN_DEATH = create("river_guardian.death");
	public static final SoundEvent RIVER_GUARDIAN_DEATH_LAND = create("river_guardian.death_land");
	public static final SoundEvent RIVER_GUARDIAN_AMBIENT = create("river_guardian.ambient");
	public static final SoundEvent RIVER_GUARDIAN_FLOP= create("river_guardian.flop");
	
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
		registry.register(RIVER_GUARDIAN_LAND_HURT);
		registry.register(RIVER_GUARDIAN_DEATH);
		registry.register(RIVER_GUARDIAN_DEATH_LAND);
		registry.register(RIVER_GUARDIAN_AMBIENT);
		registry.register(RIVER_GUARDIAN_FLOP);
	}
}