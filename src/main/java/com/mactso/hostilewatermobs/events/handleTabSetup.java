package com.mactso.hostilewatermobs.events;


import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.block.ModBlocks;
import com.mactso.hostilewatermobs.item.ModItems;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class handleTabSetup {

	// TODO - rename this to HandleTabSetup next time in here.
	@SubscribeEvent
	public static void HandleTabSetup (CreativeModeTabEvent.BuildContents event)
    {
		
		if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
			event.accept(ModItems.GURTY_GUTS);
			event.accept(ModItems.RIVER_GUARDIAN_SCALES);
			event.accept(ModItems.SLIPPERY_BITER_SCALES);
			event.accept(ModItems.WATERSNAKE_SCALES);
		} else if (event.getTab() == CreativeModeTabs.SPAWN_EGGS) {
			event.accept(ModItems.GURTY_SPAWN_EGG);
			event.accept(ModItems.RIVER_GUARDIAN_SPAWN_EGG);
			event.accept(ModItems.SLIPPERY_BITER_SPAWN_EGG);
			event.accept(ModItems.WATERSNAKE_SPAWN_EGG);
		} else if (event.getTab() == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(ModItems.GURTY_GLAND);
			event.accept(ModItems.RIVER_GUARDIAN_GLAND);
			event.accept(ModItems.SLIPPERY_BITER_GLAND);
			event.accept(ModItems.WATERSNAKE_GLAND);
		} else if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(ModBlocks.NEST_BLOCK);
		}
		
    }
}
