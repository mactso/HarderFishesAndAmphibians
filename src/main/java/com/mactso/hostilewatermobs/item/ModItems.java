package com.mactso.hostilewatermobs.item;
import com.mactso.hostilewatermobs.block.ModBlocks;
import com.mactso.hostilewatermobs.entities.ModEntities;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

	// note eggs changing to ForgeSpawnEggItem with a suppliler
	public static final Item RIVER_GUARDIAN_SPAWN_EGG = new ForgeSpawnEggItem(() -> ModEntities.RIVER_GUARDIAN, 0x799ABA, 0xBF4A0B, new Properties().rarity(Rarity.UNCOMMON));
	public static final Item RIVER_GUARDIAN_SCALES = new Item((new Item.Properties()).rarity(Rarity.UNCOMMON));
	public static final Item RIVER_GUARDIAN_GLAND = new RiverGuardianGland ((new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).stacksTo(16));
	
	public static final Item SLIPPERY_BITER_SPAWN_EGG = new ForgeSpawnEggItem(() -> ModEntities.SLIPPERY_BITER, 0x794ABA, 0xBF440B, new Properties().rarity(Rarity.UNCOMMON));
	public static final Item SLIPPERY_BITER_GLAND = new SlipperyBiterGland ((new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).rarity(Rarity.UNCOMMON).stacksTo(16));
	public static final Item SLIPPERY_BITER_SCALES = new Item((new Item.Properties()).rarity(Rarity.UNCOMMON));
	
	public static final Item GURTY_SPAWN_EGG = new ForgeSpawnEggItem(() -> ModEntities.GURTY, 0x1FD41B, 0x791A2A,  new Properties().rarity(Rarity.UNCOMMON));
	public static final Item GURTY_GUTS = new Item((new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).rarity(Rarity.UNCOMMON).stacksTo(64));
	public static final Item GURTY_GLAND = new GurtyGland ((new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).rarity(Rarity.UNCOMMON).stacksTo(16));

	public static final Item WATERSNAKE_SPAWN_EGG = new ForgeSpawnEggItem(() ->ModEntities.WATER_SNAKE, 0x49AA3A, 0xBF442B, new Properties().rarity(Rarity.UNCOMMON));
	public static final Item WATERSNAKE_SCALES = new Item((new Item.Properties()).rarity(Rarity.UNCOMMON));
	public static final Item WATERSNAKE_GLAND = new WaterSnakeGland ((new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(1.0F).fast().alwaysEat().build())).stacksTo(16));

	public static final Item NEST_ITEM = new BlockItem(ModBlocks.NEST_BLOCK, new Properties().rarity(Rarity.UNCOMMON));


	public static void register(IForgeRegistry<Item> forgeRegistry)

	{
		
		forgeRegistry.register("riverguardian_gland",RIVER_GUARDIAN_GLAND);		
		forgeRegistry.register("riverguardian_spawn_egg",RIVER_GUARDIAN_SPAWN_EGG);
		forgeRegistry.register("riverguardian_scales",RIVER_GUARDIAN_SCALES);

		forgeRegistry.register("slipperybiter_spawn_egg",SLIPPERY_BITER_SPAWN_EGG);
		forgeRegistry.register("slipperybiter_gland",SLIPPERY_BITER_GLAND);
		forgeRegistry.register("slipperybiter_scales",SLIPPERY_BITER_SCALES);

		forgeRegistry.register("gurty_spawn_egg",GURTY_SPAWN_EGG);
		forgeRegistry.register("gurty_guts",GURTY_GUTS);
		forgeRegistry.register("gurty_gland",GURTY_GLAND);

		forgeRegistry.register("watersnake_spawn_egg",WATERSNAKE_SPAWN_EGG);
		forgeRegistry.register("watersnake_scales",WATERSNAKE_SCALES);
		forgeRegistry.register("watersnake_gland",WATERSNAKE_GLAND);

		forgeRegistry.register("nest_item",NEST_ITEM);
		
	}

}
