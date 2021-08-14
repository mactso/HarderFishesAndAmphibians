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
	public static final Item RIVER_GUARDIAN_SPAWN_EGG = new SpawnEggItem(ModEntities.RIVER_GUARDIAN, 0x799ABA, 0xBF4A0B, new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("river_guardian_spawn_egg");
	public static final Item RIVER_GUARDIAN_SCALES = new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)).setRegistryName("river_guardian_scales");
	
	public static final Item SLIPPERY_BITER_SPAWN_EGG = new SpawnEggItem(ModEntities.SLIPPERY_BITER, 0x794ABA, 0xBF440B, new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("slipperybiter_spawn_egg");
	public static final Item SLIPPERY_BITER_GLAND = new SlipperyBiterGland ((new Item.Properties().food(new Food.Builder().nutrition(3).saturationMod(1.0F).fast().alwaysEat().build())).tab(ItemGroup.TAB_FOOD).stacksTo(16)).setRegistryName("slipperybiter_gland");
	public static final Item SLIPPERY_BITER_SCALES = new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)).setRegistryName("slipperybiter_scales");
	
	public static final Item GURTY_SPAWN_EGG = new SpawnEggItem(ModEntities.GURTY, 0x1FD41B, 0x791A2A,  new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("gurty_spawn_egg");
	public static final Item GURTY_GUTS = new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)).setRegistryName("gurty_guts");
	public static final Item GURTY_GLAND = new GurtyGland ((new Item.Properties().food(new Food.Builder().nutrition(3).saturationMod(1.0F).fast().alwaysEat().build())).tab(ItemGroup.TAB_FOOD).stacksTo(16)).setRegistryName("gurty_gland");

	public static final Item WATERSNAKE_SPAWN_EGG = new SpawnEggItem(ModEntities.WATER_SNAKE, 0x49AA3A, 0xBF442B, new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("classicsnake_spawn_egg");
	public static final Item WATERSNAKE_SCALES = new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)).setRegistryName("classicsnake_scales");
	
	public static void register(IForgeRegistry<Item> forgeRegistry)

	{
		
		forgeRegistry.registerAll(RIVER_GUARDIAN_SPAWN_EGG);
		forgeRegistry.registerAll(RIVER_GUARDIAN_SCALES);
		forgeRegistry.registerAll(SLIPPERY_BITER_SPAWN_EGG);
		forgeRegistry.registerAll(SLIPPERY_BITER_GLAND);
		forgeRegistry.registerAll(SLIPPERY_BITER_SCALES);
		forgeRegistry.registerAll(GURTY_SPAWN_EGG);
		forgeRegistry.registerAll(GURTY_GUTS);
		forgeRegistry.registerAll(GURTY_GLAND);
		forgeRegistry.registerAll(WATERSNAKE_SPAWN_EGG);
		forgeRegistry.registerAll(WATERSNAKE_SCALES);
		
	}

}
