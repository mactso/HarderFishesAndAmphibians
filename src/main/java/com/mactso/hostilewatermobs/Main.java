// 16.2+ harder farther
package com.mactso.hostilewatermobs;

import org.jetbrains.annotations.NotNull;

import com.mactso.hostilewatermobs.block.ModBlocks;
import com.mactso.hostilewatermobs.client.model.GurtyModel;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import com.mactso.hostilewatermobs.client.model.WaterSnakeModel;
import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.entities.ModEntities;
import com.mactso.hostilewatermobs.item.ModItems;
//import com.mactso.hostilewatermobs.item.crafting.HostileWaterMobsRecipe;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.ModBiomeModifier;
import com.mactso.hostilewatermobs.utility.ModStructureModifier;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("hostilewatermobs")
public class Main {

	public static final String MODID = "hostilewatermobs";

	public Main() {
		Utility.debugMsg(0,MODID + ": Registering Mod.");
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
	}

	@SubscribeEvent
	public void preInit(final FMLCommonSetupEvent event) {
	}


	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModEvents {

		@SubscribeEvent
		public static void onRegister(final RegisterEvent event) {

			@NotNull
			ResourceKey<? extends Registry<?>> key = event.getRegistryKey();
			if (key.equals(ForgeRegistries.Keys.BLOCKS))
				ModBlocks.register(event.getForgeRegistry());
			else if (key.equals(ForgeRegistries.Keys.ITEMS))
				ModItems.register(event.getForgeRegistry());
			else if (key.equals(ForgeRegistries.Keys.SOUND_EVENTS))
				ModSounds.register(event.getForgeRegistry());
			else if (key.equals(ForgeRegistries.Keys.ENTITY_TYPES))
				ModEntities.register(event.getForgeRegistry());
//			else if (key.equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
//				event.getForgeRegistry().register(HostileWaterMobsRecipe.NAME, HostileWaterMobsRecipe.SERIALIZER);
			else if (key.equals(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS))
				ModStructureModifier.register(event.getForgeRegistry());
			else if (key.equals(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS))
				ModBiomeModifier.register(event.getForgeRegistry());

		}

		@SubscribeEvent
		public static void onRenderers(final RegisterRenderers event) {
			System.out.println("hostilewatermobs: Registering Entity Renderers.");
			ModEntities.registerEntityRenderers(event);
		}

		@SubscribeEvent
		public static void onAttribute(final EntityAttributeCreationEvent event) {
			ModEntities.onAttribute(event);
		}
		
	}



	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void registerLayerDefinitions(final RegisterLayerDefinitions event) {
		event.registerLayerDefinition(GurtyModel.LAYER_LOCATION, () -> GurtyModel.createBodyLayer());
		event.registerLayerDefinition(SlipperyBiterModel.LAYER_LOCATION, () -> SlipperyBiterModel.createBodyLayer());
		event.registerLayerDefinition(WaterSnakeModel.LAYER_LOCATION, () -> WaterSnakeModel.createBodyLayer());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void setupClient(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> ModBlocks.setRenderLayer());
	}

	
}
