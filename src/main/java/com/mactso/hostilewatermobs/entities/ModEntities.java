package com.mactso.hostilewatermobs.entities;

import java.util.List;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.renderer.RiverGuardianRenderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {

	public static final EntityType<RiverGuardianEntity> RIVER_GUARDIAN = register("river_guardian", EntityType.Builder.create(RiverGuardianEntity::new, EntityClassification.MONSTER).size(0.85F, 0.85F).trackingRange(5));

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
	}

	@OnlyIn(Dist.CLIENT)
	public static void register(EntityRendererManager renderManager)
	{
		renderManager.register(RIVER_GUARDIAN, new RiverGuardianRenderer(renderManager));
		
	}

	public static void getBiomeSpawnData(List<EntityType<?>> mobs, List<Spawners> spawns)
	{
		int weight = 100;
		int min = 1;
		int max = 2;
		mobs.add(EntityType.SKELETON);
		spawns.add(new Spawners(RIVER_GUARDIAN, weight, 1, 2));
	}
	
	public static void getFeatureSpawnData(List<EntityType<?>> mobs, List<Spawners> spawns)
	{
		int weight = 100;
		int min = 1;
		int max = 2;
		mobs.add(EntityType.SKELETON);
		spawns.add(new Spawners(RIVER_GUARDIAN, weight, 1, 2));

	}

}
