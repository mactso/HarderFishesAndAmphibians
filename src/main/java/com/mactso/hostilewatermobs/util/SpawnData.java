package com.mactso.hostilewatermobs.util;

import java.util.ArrayList;

// from package com.lupicus.nasty.util;

import java.util.List;

import com.mactso.hostilewatermobs.entities.ModEntities;

import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpawnData
{
	
	@SubscribeEvent
	public static void onBiome(BiomeLoadingEvent event)
	{
		
		MobSpawnInfoBuilder builder = event.getSpawns();
		List<Spawners> list = new ArrayList<>();
		ModEntities.getBiomeSpawnData( list, event);
		for (int i = 0; i < list.size(); ++i)
		{
			Spawners spawner = list.get(i);
			builder.withSpawner(spawner.type.getClassification(), spawner).withSpawnCost(list.get(i).type, 0.08, 2.0);
		}
	}
	
	@SubscribeEvent
	public static void onStructure(StructureSpawnListGatherEvent event)
	{

		List<Spawners> list = new ArrayList<>();
		ModEntities.getFeatureSpawnData(list, event.getStructure());
		for (int i = 0; i < list.size(); ++i)
		{
			Spawners spawner = list.get(i);
			event.addEntitySpawn(spawner.type.getClassification(), spawner);
		}
	}
}
