package com.mactso.hostilewatermobs.util;

import java.util.ArrayList;

// from package com.lupicus.nasty.util;

import java.util.List;

import com.mactso.hostilewatermobs.entities.ModEntities;

import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpawnData
{
	
	@SubscribeEvent (priority=EventPriority.LOWEST)
	public static void onBiomeLow (BiomeLoadingEvent event)
	{
		
		addSpawnsToBiomes(event);
	}

//	@SubscribeEvent (priority=EventPriority.HIGHEST)
//	public static void onBiomeHigh (BiomeLoadingEvent event)
//	{
//		
//		addSpawnsToBiomes(event);
//	}

	private static void addSpawnsToBiomes(BiomeLoadingEvent event) {
		MobSpawnInfoBuilder builder = event.getSpawns();
		List<Spawners> list = new ArrayList<>();
		ModEntities.getBiomeSpawnData( list, event);
		for (int i = 0; i < list.size(); ++i)
		{
			Spawners spawner = list.get(i);
			builder.addSpawn(spawner.type.getCategory(), spawner).addMobCharge(list.get(i).type, 0.08, 2.0);
		}
	}
	
	@SubscribeEvent (priority=EventPriority.HIGHEST)	
	public static void onStructureHigh (StructureSpawnListGatherEvent event)
	{

		addSpawnsToStructures(event);
	}

//	@SubscribeEvent (priority=EventPriority.LOWEST)	
//	public static void onStructureLow (StructureSpawnListGatherEvent event)
//	{
//
//		addSpawnsToStructures(event);
//	}
	
	private static void addSpawnsToStructures(StructureSpawnListGatherEvent event) {
		List<Spawners> list = new ArrayList<>();
		ModEntities.getFeatureSpawnData(list, event.getStructure());
		for (int i = 0; i < list.size(); ++i)
		{
			Spawners spawner = list.get(i);
			event.addEntitySpawn(spawner.type.getCategory(), spawner);
		}
	}
}
