package com.mactso.hostilewatermobs.item;
import com.mactso.hostilewatermobs.block.ModBlocks;
import com.mactso.hostilewatermobs.entities.ModEntities;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {
	
	public static final Item RIVER_GUARDIAN_SPAWN_EGG = new SpawnEggItem(ModEntities.RIVER_GUARDIAN, 0x799ABA, 0xBF4A0B, new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("riverguardian_spawn_egg");
	public static final Item RIVER_GUARDIAN_SCALES = new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)).setRegistryName("riverguardian_scales");
	public static final Item RIVER_GUARDIAN_GLAND = new RiverGuardianGland ((new Item.Properties().food(new Food.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).tab(ItemGroup.TAB_FOOD).stacksTo(16)).setRegistryName("riverguardian_gland");
	
	public static final Item SLIPPERY_BITER_SPAWN_EGG = new SpawnEggItem(ModEntities.SLIPPERY_BITER, 0x794ABA, 0xBF440B, new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("slipperybiter_spawn_egg");
	public static final Item SLIPPERY_BITER_SCALES = new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)).setRegistryName("slipperybiter_scales");
	public static final Item SLIPPERY_BITER_GLAND = new SlipperyBiterGland ((new Item.Properties().food(new Food.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).tab(ItemGroup.TAB_FOOD).stacksTo(16)).setRegistryName("slipperybiter_gland");
	
	public static final Item GURTY_SPAWN_EGG = new SpawnEggItem(ModEntities.GURTY, 0x1FD41B, 0x791A2A,  new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("gurty_spawn_egg");
	public static final Item GURTY_GUTS = new Item((new Item.Properties().food(new Food.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).tab(ItemGroup.TAB_FOOD)).setRegistryName("gurty_guts");
	public static final Item GURTY_GLAND = new GurtyGland ((new Item.Properties().food(new Food.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).tab(ItemGroup.TAB_FOOD).stacksTo(16)).setRegistryName("gurty_gland");

	public static final Item WATERSNAKE_SPAWN_EGG = new SpawnEggItem(ModEntities.WATER_SNAKE, 0x49AA3A, 0xBF442B, new Properties().tab(ItemGroup.TAB_MISC)).setRegistryName("watersnake_spawn_egg");
	public static final Item WATERSNAKE_SCALES = new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)).setRegistryName("watersnake_scales");
	public static final Item WATERSNAKE_GLAND = new WaterSnakeGland ((new Item.Properties().food(new Food.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).tab(ItemGroup.TAB_FOOD).stacksTo(16)).setRegistryName("watersnake_gland");

	public static final Item NEST_ITEM = new BlockItem(ModBlocks.NEST_BLOCK, new Properties().tab(ItemGroup.TAB_DECORATIONS)).setRegistryName("nest_item");


	public static void register(IForgeRegistry<Item> forgeRegistry)

	{
		
		forgeRegistry.register(RIVER_GUARDIAN_GLAND);
		forgeRegistry.register(RIVER_GUARDIAN_SPAWN_EGG);
		forgeRegistry.register(RIVER_GUARDIAN_SCALES);

		forgeRegistry.register(SLIPPERY_BITER_GLAND);
		forgeRegistry.register(SLIPPERY_BITER_SPAWN_EGG);
		forgeRegistry.register(SLIPPERY_BITER_SCALES);
		
		forgeRegistry.register(GURTY_GLAND);
		forgeRegistry.register(GURTY_SPAWN_EGG);
		forgeRegistry.register(GURTY_GUTS);

		forgeRegistry.register(WATERSNAKE_GLAND);
		forgeRegistry.register(WATERSNAKE_SPAWN_EGG);
		forgeRegistry.register(WATERSNAKE_SCALES);
	
		forgeRegistry.register(NEST_ITEM);		

	}

}
