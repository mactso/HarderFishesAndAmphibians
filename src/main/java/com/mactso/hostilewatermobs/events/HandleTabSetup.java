package com.mactso.hostilewatermobs.events;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.item.ModItems;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class HandleTabSetup {
	@SubscribeEvent
	public static void handleTabSetup (BuildCreativeModeTabContentsEvent event)
    {

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
        	event.accept(ModItems.GURTY_SPAWN_EGG);
        	event.accept(ModItems.WATERSNAKE_SPAWN_EGG);
        	event.accept(ModItems.RIVER_GUARDIAN_SPAWN_EGG);
        	event.accept(ModItems.SLIPPERY_BITER_SPAWN_EGG);
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
        	event.accept(ModItems.GURTY_GLAND);
        	event.accept(ModItems.GURTY_GUTS);
        	event.accept(ModItems.WATERSNAKE_GLAND);
        	event.accept(ModItems.RIVER_GUARDIAN_GLAND);
        	event.accept(ModItems.SLIPPERY_BITER_GLAND);
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        	event.accept(ModItems.WATERSNAKE_SCALES);
        	event.accept(ModItems.RIVER_GUARDIAN_SCALES);
        	event.accept(ModItems.SLIPPERY_BITER_SCALES);
        }

    }

}


