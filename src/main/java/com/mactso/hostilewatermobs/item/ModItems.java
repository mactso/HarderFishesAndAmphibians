package com.mactso.hostilewatermobs.item;
import com.mactso.hostilewatermobs.entities.ModEntities;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

	public static final Item RIVER_GUARDIAN_SPAWN_EGG = new SpawnEggItem(ModEntities.RIVER_GUARDIAN, 0x799ABA, 0xBF4A0B, new Properties().group(ItemGroup.MISC)).setRegistryName("river_guardian_spawn_egg");

	public static void register(IForgeRegistry<Item> forgeRegistry)
	{
		forgeRegistry.registerAll(RIVER_GUARDIAN_SPAWN_EGG);
	}

	
}
