// 16.2+ harder farther
package com.mactso.hostilewatermobs;

import com.mactso.hostilewatermobs.block.ModBlocks;
import com.mactso.hostilewatermobs.client.model.GurtyModel;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import com.mactso.hostilewatermobs.client.model.WaterSnakeModel;
import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.entities.ModEntities;
import com.mactso.hostilewatermobs.item.ModItems;
import com.mactso.hostilewatermobs.item.crafting.HostileWaterMobsRecipe;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.SpawnData;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("hostilewatermobs")
public class Main {

	    public static final String MODID = "hostilewatermobs"; 
	    
	    public Main()
	    {
	    	System.out.println(MODID + ": Registering Mod.");
	  		FMLJavaModLoadingContext.get().getModEventBus().register(this);
 	        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );
 			MinecraftForge.EVENT_BUS.register(SpawnData.class);
 	        //   	        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
	    }
	    
		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {
				System.out.println(MODID + ": Registering Handlers");
//				MinecraftForge.EVENT_BUS.register(new SpawnerBreakEvent ());
//				MinecraftForge.EVENT_BUS.register(new SpawnEventHandler());
//				MinecraftForge.EVENT_BUS.register(new MonsterDropEventHandler());
//				MinecraftForge.EVENT_BUS.register(new ExperienceDropEventHandler());
//				MinecraftForge.EVENT_BUS.register(new ChunkEvent());
//				CapabilityChunkLastMobDeathTime.register();
				//				MinecraftForge.EVENT_BUS.register(new MyEntityPlaceEvent());
		}   

		
		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public void registerLayerDefinitions(final RegisterLayerDefinitions event)
		{
			event.registerLayerDefinition(GurtyModel.LAYER_LOCATION, ()-> GurtyModel.createBodyLayer());
			event.registerLayerDefinition(SlipperyBiterModel.LAYER_LOCATION, ()-> SlipperyBiterModel.createBodyLayer());
			event.registerLayerDefinition(WaterSnakeModel.LAYER_LOCATION, ()-> WaterSnakeModel.createBodyLayer());
		}
		
	
	    
	    
	    
	    
		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public void setupClient(final FMLClientSetupEvent event)
		{
			event.enqueueWork(() -> ModBlocks.setRenderLayer());
		}
		
		
		@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	    public static class ModEvents
	    {

			
			@SubscribeEvent
			public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
			{
				System.out.println("hostilewatermobs: Registering Blocks.");
	    		ModBlocks.register(event.getRegistry());
			}
			
			@SubscribeEvent
	    	public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
	    	{
				System.out.println("hostilewatermobs: Registering Items.");
	    		ModItems.register(event.getRegistry());
	    	}

			
			
	        @SubscribeEvent
	        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event)
	        {
				System.out.println("hostilewatermobs: Registering Entities.");
	        	ModEntities.register(event.getRegistry());
	        }

	        @SubscribeEvent
	        public static void onRenderers(final RegisterRenderers event)
	        {
				System.out.println("hostilewatermobs: Registering Entity Renderers.");
	        	ModEntities.registerEntityRenderers(event);
	        }
	        

	        @SubscribeEvent
	        public static void onAttribute(final EntityAttributeCreationEvent event)
	        {
	        	ModEntities.onAttribute(event);
	        }
	        
	        
	        @SubscribeEvent
	        public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event)
	        {
				System.out.println("hostilewatermobs: Registering Sounds.");
	        	ModSounds.register(event.getRegistry());
	        }
	        
	        @SubscribeEvent
	        public static void onRecipeRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event)
	        {
				System.out.println("hostilewatermobs: Registering Recipes");
	        	event.getRegistry().register(HostileWaterMobsRecipe.CRAFTING_HOSTILEWATERMOBS);
	        }
	        
	    }	
		
	    @Mod.EventBusSubscriber()
	    public static class ForgeEvents
	    {
//	        @SubscribeEvent
//	        public static void onServerStarting(FMLServerStartingEvent event)
//	        {
//	        	ModEntities.addSpawnData();
//	        }
//
//	        @SubscribeEvent
//	        public static void onServerStopping(FMLServerStoppingEvent event)
//	        {
//	        	ModEntities.removeSpawnData();
//	        }
	    }

}
