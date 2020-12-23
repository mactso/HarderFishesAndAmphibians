package com.mactso.hostilewatermobs.item;
import com.mactso.hostilewatermobs.entities.ModEntities;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {
	;
	public static final Item RIVER_GUARDIAN_SPAWN_EGG = new SpawnEggItem(ModEntities.RIVER_GUARDIAN, 0x799ABA, 0xBF4A0B, new Properties().group(ItemGroup.MISC)).setRegistryName("river_guardian_spawn_egg");
	public static final Item RIVER_GUARDIAN_SCALES = new Item((new Item.Properties()).group(ItemGroup.MATERIALS)).setRegistryName("river_guardian_scales");
	 
	public static final Item SLIPPERY_BITER_SPAWN_EGG = new SpawnEggItem(ModEntities.SLIPPERY_BITER, 0x794ABA, 0xBF440B, new Properties().group(ItemGroup.MISC)).setRegistryName("slipperybiter_spawn_egg");
	public static final Item SLIPPERY_BITER_GLAND = new SlipperyBiterGland ((new Item.Properties().food(new Food.Builder().hunger(3).saturation(1.0F).fastToEat().setAlwaysEdible().build())).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("slipperybiter_gland");
	public static final Item SLIPPERY_BITER_SCALES = new Item((new Item.Properties()).group(ItemGroup.MATERIALS)).setRegistryName("slipperybiter_scales");
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{
		forgeRegistry.registerAll(RIVER_GUARDIAN_SPAWN_EGG);
		forgeRegistry.registerAll(RIVER_GUARDIAN_SCALES);
		forgeRegistry.registerAll(SLIPPERY_BITER_SPAWN_EGG);
		forgeRegistry.registerAll(SLIPPERY_BITER_GLAND);
		forgeRegistry.registerAll(SLIPPERY_BITER_SCALES);
	}

}
