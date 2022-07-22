package com.mactso.hostilewatermobs.entities;

import java.util.List;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.renderer.WaterSnakeRenderer;
import com.mactso.hostilewatermobs.client.renderer.GurtyRenderer;
import com.mactso.hostilewatermobs.client.renderer.RiverGuardianRenderer;
import com.mactso.hostilewatermobs.client.renderer.SlipperyBiterRenderer;
import com.mactso.hostilewatermobs.config.MyConfig;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {

	public static final EntityType<RiverGuardianEntity> RIVER_GUARDIAN = register("river_guardian", EntityType.Builder
			.of(RiverGuardianEntity::new, MobCategory.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(5));
	public static final EntityType<SlipperyBiterEntity> SLIPPERY_BITER = register("slipperybiter", EntityType.Builder
			.of(SlipperyBiterEntity::new, MobCategory.MONSTER).sized(0.9F, 0.7F).clientTrackingRange(21));
	public static final EntityType<GurtyEntity> GURTY = register("gurty", EntityType.Builder
			.of(GurtyEntity::new, MobCategory.MONSTER).sized(1.1F, 1.0F).clientTrackingRange(21));
	public static final EntityType<WaterSnakeEntity> WATER_SNAKE = register("watersnake", EntityType.Builder
			.of(WaterSnakeEntity::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(21));

	
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
		SpawnPlacements.register(RIVER_GUARDIAN, SpawnPlacements.Type.IN_WATER,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RiverGuardianEntity::canSpawn);
		DefaultAttributes.put(RIVER_GUARDIAN, RiverGuardianEntity.registerAttributes().build());

		forgeRegistry.registerAll(SLIPPERY_BITER);
		SpawnPlacements.register(SLIPPERY_BITER, SpawnPlacements.Type.IN_WATER,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SlipperyBiterEntity::canSpawn);
		DefaultAttributes.put(SLIPPERY_BITER, SlipperyBiterEntity.registerAttributes().build());

		forgeRegistry.registerAll(GURTY);
		SpawnPlacements.register(GURTY, SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GurtyEntity::canSpawn);
		DefaultAttributes.put(GURTY, GurtyEntity.registerAttributes().build());

		forgeRegistry.registerAll(WATER_SNAKE);
		SpawnPlacements.register(WATER_SNAKE, SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterSnakeEntity::canSpawn);
		DefaultAttributes.put(WATER_SNAKE, WaterSnakeEntity.registerAttributes().build());
	}

	@OnlyIn(Dist.CLIENT)
	public static void register(EntityRenderDispatcher renderManager) {
		renderManager.register(RIVER_GUARDIAN, new RiverGuardianRenderer(renderManager));
		renderManager.register(SLIPPERY_BITER, new SlipperyBiterRenderer(renderManager));
		renderManager.register(GURTY, new GurtyRenderer(renderManager));
		renderManager.register(WATER_SNAKE, new WaterSnakeRenderer(renderManager));

	}

	public static void getBiomeSpawnData(List<SpawnerData> spawns, BiomeLoadingEvent event) {
		int weight;
		int min;
		int max;
		int rgSC = MyConfig.getRiverGuardianSpawnChance();
		int gSC = MyConfig.getGurtySpawnChance();
		int sbSC = MyConfig.getSlipperyBiterSpawnChance();
		int wsSC = MyConfig.getWatersnakeSpawnChance();

		BiomeCategory biomeCategory = event.getCategory();
		MobSpawnSettingsBuilder builder = event.getSpawns();
		
		String threadname = Thread.currentThread().getName();
		if (threadname.equals("Render thread")) {
//			return;
		} else {
			min = 1;
		}
		
		if (biomeCategory == Biome.BiomeCategory.NETHER) {

			List<SpawnerData> wlist = builder.getSpawner(MobCategory.MONSTER);
			
			boolean zombiePiglinSpawner = false;
			boolean ghastSpawner = false;

			// more efficient but less generic.
			// safer when deleting if go from end with i=size() i--; wlist.size()-1;
			for (int i = 0; i < wlist.size(); i++) {
				SpawnerData s = wlist.get(i);
				// remove spawner for mob
				// add spawner to list to remove later.

				if (s.type == EntityType.ZOMBIFIED_PIGLIN) {
					if (s.weight >= MyConfig.getZombifiedPiglinSpawnBoost()) {
						zombiePiglinSpawner = true;
					}
				}
				if (s.type == EntityType.GHAST) {
					if (s.weight >= MyConfig.getGhastSpawnBoost()) {
						ghastSpawner = true;
					}
				}
			}
			
			if (!zombiePiglinSpawner) {
				if (MyConfig.getZombifiedPiglinSpawnBoost() > 0) {
					spawns.add(new SpawnerData(EntityType.ZOMBIFIED_PIGLIN, weight = MyConfig.getZombifiedPiglinSpawnBoost(),
						min = 1, max = 3));
				}
			}
			if (!ghastSpawner) {
				if (MyConfig.getGhastSpawnBoost() > 0) {
					spawns.add(new SpawnerData(EntityType.GHAST, weight = MyConfig.getGhastSpawnBoost(), min = 1,
							max = 1));
				}
			}
			
			spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = 5, min = 1, max = 3));

		} else if (biomeCategory == Biome.BiomeCategory.RIVER) {
			if (rgSC > 0) 
				spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = rgSC+(rgSC/2)+1, min = 1, max = 1));
			if (sbSC > 0)
				spawns.add(new SpawnerData(SLIPPERY_BITER, weight = rgSC+(rgSC/3)+1, min = 1, max = 1));
			if (wsSC > 0)
				spawns.add(new SpawnerData(WATER_SNAKE, weight = wsSC+(wsSC/3)+1, min = 1, max = 1));
			if (gSC > 0) 
				spawns.add(new SpawnerData(GURTY, weight = gSC, min = 1, max = 1));
			spawns.add(new SpawnerData(EntityType.COD, weight = MyConfig.getCodSpawnBoost()/3, min = 1, max = 2));
			spawns.add(new SpawnerData(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost()/5, min = 1, max = 2));
			spawns.add(new SpawnerData(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost()/2, min = 1, max = 2));
		} else if (biomeCategory == Biome.BiomeCategory.SWAMP) {
			spawns.add(new SpawnerData(EntityType.COD, weight = MyConfig.getCodSpawnBoost()/2, min = 1, max = 2));
			if (rgSC > 0) 
				spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = (rgSC*2/3)+1, min = 1, max = 1));
			if (sbSC > 0) 
				spawns.add(new SpawnerData(SLIPPERY_BITER, weight = (sbSC*2/3)+1, min = 1, max = 1));
			if (gSC > 0) 
				spawns.add(new SpawnerData(GURTY, weight = (gSC*110)/100, min = 1, max = 3));
			if (wsSC > 0)
				spawns.add(new SpawnerData(WATER_SNAKE, weight = wsSC+(wsSC/3)+1, min = 1, max = 1));
		} else if (biomeCategory == Biome.BiomeCategory.OCEAN) {
			if (rgSC > 0) 
				spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
			if (sbSC > 0) 
				spawns.add(new SpawnerData(SLIPPERY_BITER, weight = sbSC, min = 1, max = 3));
			if (gSC > 0) 
				spawns.add(new SpawnerData(GURTY, weight = (gSC/2), min = 1, max = 1));
			spawns.add(new SpawnerData(EntityType.COD, weight = MyConfig.getCodSpawnBoost(), min = 2, max = 4));
			spawns.add(new SpawnerData(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost(), min = 2, max = 4));
			spawns.add(new SpawnerData(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost(), min = 1, max = 4));
			spawns.add(new SpawnerData(EntityType.DOLPHIN, weight = MyConfig.getDolphinSpawnboost(), min = 1, max = 2));
		} else if (biomeCategory == Biome.BiomeCategory.BEACH) {
			if (gSC > 0) 			
				spawns.add(new SpawnerData(GURTY, weight = gSC, min = 1, max = 3));
			if (wsSC > 0)
				spawns.add(new SpawnerData(WATER_SNAKE, weight = wsSC+(wsSC/3)+1, min = 1, max = 1));
		} else {
			if (rgSC > 0) 
				spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = (rgSC/8)+1, min = 1, max = 1));
			if (sbSC > 0) 
				spawns.add(new SpawnerData(SLIPPERY_BITER, weight = (sbSC/8)+1, min = 1, max = 2));
			if (gSC > 0) 
				spawns.add(new SpawnerData(GURTY, weight = (gSC/8), min = 1, max = 1));
			if (wsSC > 0)
				spawns.add(new SpawnerData(WATER_SNAKE, weight = 1, min = 1, max = 1));
		}
	}

	public static void getFeatureSpawnData(List<SpawnerData> spawns, StructureFeature<?> structure) {
		int weight;
		int min;
		int max;
		int rgSC = MyConfig.getRiverGuardianSpawnChance();
		int gSC = MyConfig.getGurtySpawnChance();
		int sbSC = MyConfig.getSlipperyBiterSpawnChance();
		String nameSpace = structure.getRegistryName().getNamespace();
		
				
		if (nameSpace.equals("minecraft")) {
			if (structure == StructureFeature.OCEAN_RUIN) {
				if (rgSC > 0) 
					spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new SpawnerData(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
			} else if (structure == StructureFeature.SHIPWRECK) {
				if (rgSC > 0) 
					spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new SpawnerData(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
			} else if (structure == StructureFeature.BURIED_TREASURE) {
				if (rgSC > 0) 
					spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new SpawnerData(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
				if (gSC > 0) 
					spawns.add(new SpawnerData(GURTY, (gSC/4)+1, min = 1, max = 3));
			} else if (structure == StructureFeature.SWAMP_HUT) {
				if (rgSC > 0) 
					spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = (rgSC/10)+1, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new SpawnerData(SLIPPERY_BITER, (sbSC/10)+1, min = 1, max = 1));
				if (gSC > 0) 
					spawns.add(new SpawnerData(GURTY, weight = (gSC/10)+1, min = 1, max = 3));
			} else if (structure == StructureFeature.RUINED_PORTAL) {
				if (rgSC > 0) 
					spawns.add(new SpawnerData(RIVER_GUARDIAN, weight = (rgSC/2)+1, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new SpawnerData(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
				if (gSC > 0) 
					spawns.add(new SpawnerData(GURTY, weight = (gSC/4)+1, min = 1, max = 3));
			}
		} else {
			if (MyConfig.getModStructureBoost() > 0) {
				weight = MyConfig.getModStructureBoost();
				spawns.add(new SpawnerData(EntityType.SKELETON, weight , min = 1, max = 3));
				spawns.add(new SpawnerData(EntityType.ZOMBIE, 1 + weight/2, min = 1, max = 3));
				spawns.add(new SpawnerData(EntityType.WITCH, 1+ weight/6, min = 1, max = 3));
			}
		}
	}
}
