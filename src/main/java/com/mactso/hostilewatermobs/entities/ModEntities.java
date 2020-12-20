package com.mactso.hostilewatermobs.entities;

import java.util.List;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.renderer.RiverGuardianRenderer;
import com.mactso.hostilewatermobs.client.renderer.SlipperyBiterRenderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {

	public static final EntityType<RiverGuardianEntity> RIVER_GUARDIAN = register("river_guardian", EntityType.Builder.create(RiverGuardianEntity::new, EntityClassification.MONSTER).size(0.85F, 0.85F).trackingRange(5));
	public static final EntityType<SlipperyBiterEntity> SLIPPERY_BITER = register("slippery_biter", EntityType.Builder.create(SlipperyBiterEntity::new, EntityClassification.MONSTER).size(1.05F, 1.05F).trackingRange(21));

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder)
	{
		System.out.println(Main.MODID);
		System.out.println(name);
		String key = Main.MODID + ":" + name;
		EntityType<T> type = builder.build(key);
		type.setRegistryName(key);
		return type;
	}

	public static void register(IForgeRegistry<EntityType<?>> forgeRegistry)
	{
		forgeRegistry.registerAll(RIVER_GUARDIAN);
		EntitySpawnPlacementRegistry.register(RIVER_GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RiverGuardianEntity::canSpawn);
		GlobalEntityTypeAttributes.put(RIVER_GUARDIAN, RiverGuardianEntity.registerAttributes().create());

		forgeRegistry.registerAll(SLIPPERY_BITER);
		EntitySpawnPlacementRegistry.register(SLIPPERY_BITER, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlipperyBiterEntity::canSpawn);
		GlobalEntityTypeAttributes.put(SLIPPERY_BITER, SlipperyBiterEntity.registerAttributes().create());
	}

	@OnlyIn(Dist.CLIENT)
	public static void register(EntityRendererManager renderManager)
	{
		renderManager.register(RIVER_GUARDIAN, new RiverGuardianRenderer(renderManager));
		renderManager.register(SLIPPERY_BITER, new SlipperyBiterRenderer(renderManager));
				
	}

	public static void getBiomeSpawnData(List<Spawners> spawns, Biome.Category biomeCategory)
	{
		int weight;
		int min;
		int max;

		if (biomeCategory == Biome.Category.NETHER) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight =5, min=1, max=3));
		} else
		if (biomeCategory == Biome.Category.RIVER) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 100, min=1, max=1));
		} else
		if (biomeCategory == Biome.Category.SWAMP) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 50, min=1, max=1));
		} else
		if (biomeCategory == Biome.Category.OCEAN) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 50, min=1, max=1));
		} else {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 10, min=1, max=1 ));
		}
		
		if (biomeCategory == Biome.Category.SWAMP) {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 50, min=1, max=1));
		} else
		if (biomeCategory == Biome.Category.OCEAN) {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 50, min=1, max=1));
		} else {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 20, min=1, max=1 ));
		}

	}
	
	
	
	public static void getFeatureSpawnData( List<Spawners> spawns, Structure structure)
	{
		int weight;
		int min;
		int max;

		if (structure == Structure.OCEAN_RUIN) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 20, min = 1, max = 1));
		} else 
		if (structure == Structure.SHIPWRECK) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 15, min = 1, max = 1));
		} else 
		if (structure == Structure.BURIED_TREASURE) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 15, min = 1, max = 1));
		} else 
		if (structure == Structure.SWAMP_HUT) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 5, min = 1, max = 1));
		} else 
		if (structure == Structure.RUINED_PORTAL) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 30, min = 1, max = 1));
		}

		if (structure == Structure.OCEAN_RUIN) {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 20, min = 1, max = 1));
		} else 
		if (structure == Structure.SHIPWRECK) {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 15, min = 1, max = 1));
		} else 
		if (structure == Structure.BURIED_TREASURE) {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 15, min = 1, max = 1));
		} else 
		if (structure == Structure.SWAMP_HUT) {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 5, min = 1, max = 1));
		} else 
		if (structure == Structure.RUINED_PORTAL) {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 30, min = 1, max = 1));
		}
		
	}
}
