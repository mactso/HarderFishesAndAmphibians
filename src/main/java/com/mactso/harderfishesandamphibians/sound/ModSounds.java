package com.mactso.harderfishesandamphibians.sound;

import com.mactso.harderfishesandamphibians.Main;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds
{
	
	public static final SoundEvent RIVER_GUARDIAN_HURT = create("river_guardian_hurt");

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
	}
}
