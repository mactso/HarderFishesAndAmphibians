package com.mactso.hostilewatermobs.entities;

import java.util.List;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.renderer.GurtyRenderer;
import com.mactso.hostilewatermobs.client.renderer.RiverGuardianRenderer;
import com.mactso.hostilewatermobs.client.renderer.SlipperyBiterRenderer;
import com.mactso.hostilewatermobs.config.MyConfig;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {

	public static final EntityType<RiverGuardianEntity> RIVER_GUARDIAN = register("river_guardian", EntityType.Builder
			.create(RiverGuardianEntity::new, EntityClassification.MONSTER).size(0.85F, 0.85F).trackingRange(5));
	public static final EntityType<SlipperyBiterEntity> SLIPPERY_BITER = register("slipperybiter", EntityType.Builder
			.create(SlipperyBiterEntity::new, EntityClassification.MONSTER).size(0.9F, 0.7F).trackingRange(21));
	public static final EntityType<GurtyEntity> GURTY = register("gurty", EntityType.Builder
			.create(GurtyEntity::new, EntityClassification.MONSTER).size(1.1F, 1.0F).trackingRange(21));

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		System.out.println(Main.MODID);
		System.out.println(name);
		String key = Main.MODID + ":" + name;
		EntityType<T> type = builder.build(key);
		type.setRegistryName(key);
		return type;
	}

	public static void register(IForgeRegistry<EntityType<?>> forgeRegistry) {
		forgeRegistry.registerAll(RIVER_GUARDIAN);
		EntitySpawnPlacementRegistry.register(RIVER_GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RiverGuardianEntity::canSpawn);
		GlobalEntityTypeAttributes.put(RIVER_GUARDIAN, RiverGuardianEntity.registerAttributes().create());

		forgeRegistry.registerAll(SLIPPERY_BITER);
		EntitySpawnPlacementRegistry.register(SLIPPERY_BITER, EntitySpawnPlacementRegistry.PlacementType.IN_WATER,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlipperyBiterEntity::canSpawn);
		GlobalEntityTypeAttributes.put(SLIPPERY_BITER, SlipperyBiterEntity.registerAttributes().create());

		forgeRegistry.registerAll(GURTY);
		EntitySpawnPlacementRegistry.register(GURTY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GurtyEntity::canSpawn);
		GlobalEntityTypeAttributes.put(GURTY, GurtyEntity.registerAttributes().create());

	}

	@OnlyIn(Dist.CLIENT)
	public static void register(EntityRendererManager renderManager) {
		renderManager.register(RIVER_GUARDIAN, new RiverGuardianRenderer(renderManager));
		renderManager.register(SLIPPERY_BITER, new SlipperyBiterRenderer(renderManager));
		renderManager.register(GURTY, new GurtyRenderer(renderManager));

	}

	public static void getBiomeSpawnData(List<Spawners> spawns, BiomeLoadingEvent event) {
		int weight;
		int min;
		int max;

		Category biomeCategory = event.getCategory();
		
		if (biomeCategory == Biome.Category.NETHER) {

			boolean zombiePiglinSpawner = false;
			boolean ghastSpawner = false;

			// more efficient but less generic.
			for (int i = 0; i < spawns.size(); i++) {
				Spawners s = spawns.get(i);
				if (s.type == EntityType.ZOMBIFIED_PIGLIN) {
					zombiePiglinSpawner = true;
				}
				if (s.type == EntityType.GHAST) {
					ghastSpawner = true;
				}

			}

			if (!zombiePiglinSpawner) {
				spawns.add(new Spawners(EntityType.ZOMBIFIED_PIGLIN, weight = MyConfig.getZombifiedPiglinSpawnBoost(),
						min = 1, max = 3));
			}
			if (!ghastSpawner) {
				spawns.add(new Spawners(EntityType.GHAST, weight = MyConfig.getGhastSpawnBoost(), min = 1,
						max = 1));
			}
			
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 5, min = 1, max = 3));

		} else if (biomeCategory == Biome.Category.RIVER) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 80, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 20, min = 1, max = 1));
			spawns.add(new Spawners(GURTY, weight = 45, min = 1, max = 1));
			spawns.add(new Spawners(EntityType.COD, weight = MyConfig.getCodSpawnBoost()/3, min = 1, max = 2));
			spawns.add(new Spawners(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost()/5, min = 1, max = 2));
			spawns.add(new Spawners(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost()/2, min = 1, max = 4));
		} else if (biomeCategory == Biome.Category.SWAMP) {
			spawns.add(new Spawners(EntityType.COD, weight = MyConfig.getCodSpawnBoost()/2, min = 1, max = 2));
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 35, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 35, min = 1, max = 1));
			spawns.add(new Spawners(GURTY, weight = 50, min = 2, max = 3));
		} else if (biomeCategory == Biome.Category.OCEAN) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 50, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 50, min = 1, max = 3));
			spawns.add(new Spawners(GURTY, weight = 25, min = 1, max = 1));
			spawns.add(new Spawners(EntityType.COD, weight = MyConfig.getCodSpawnBoost(), min = 2, max = 4));
			spawns.add(new Spawners(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost(), min = 2, max = 4));
			spawns.add(new Spawners(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost(), min = 1, max = 4));
			spawns.add(new Spawners(EntityType.DOLPHIN, weight = MyConfig.getDolphinSpawnboost(), min = 1, max = 2));
		} else if (biomeCategory == Biome.Category.BEACH) {
			spawns.add(new Spawners(GURTY, weight = 35, min = 1, max = 3));
		} else {
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 20, min = 1, max = 2));
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 10, min = 1, max = 1));
			spawns.add(new Spawners(GURTY, weight = 15, min = 1, max = 1));
		}

	}

	public static void getFeatureSpawnData(List<Spawners> spawns, Structure<?> structure) {
		int weight;
		int min;
		int max;

		if (structure == Structure.OCEAN_RUIN) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 20, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 20, min = 1, max = 1));
		} else if (structure == Structure.SHIPWRECK) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 15, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 15, min = 1, max = 1));
		} else if (structure == Structure.BURIED_TREASURE) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 15, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 15, min = 1, max = 1));
			spawns.add(new Spawners(GURTY, weight = 15, min = 1, max = 3));
		} else if (structure == Structure.SWAMP_HUT) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 5, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 5, min = 1, max = 1));
			spawns.add(new Spawners(GURTY, weight = 5, min = 1, max = 3));
		} else if (structure == Structure.RUINED_PORTAL) {
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 30, min = 1, max = 1));
			spawns.add(new Spawners(SLIPPERY_BITER, weight = 30, min = 1, max = 1));
			spawns.add(new Spawners(GURTY, weight = 15, min = 1, max = 3));

		}

	}
}
