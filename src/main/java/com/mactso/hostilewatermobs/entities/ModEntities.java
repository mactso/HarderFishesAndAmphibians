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
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {

	public static final EntityType<RiverGuardianEntity> RIVER_GUARDIAN = register("river_guardian", EntityType.Builder
			.of(RiverGuardianEntity::new, EntityClassification.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(5));
	public static final EntityType<SlipperyBiterEntity> SLIPPERY_BITER = register("slipperybiter", EntityType.Builder
			.of(SlipperyBiterEntity::new, EntityClassification.MONSTER).sized(0.9F, 0.7F).clientTrackingRange(21));
	public static final EntityType<GurtyEntity> GURTY = register("gurty", EntityType.Builder
			.of(GurtyEntity::new, EntityClassification.MONSTER).sized(1.1F, 1.0F).clientTrackingRange(21));

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
		GlobalEntityTypeAttributes.put(RIVER_GUARDIAN, RiverGuardianEntity.registerAttributes().build());

		forgeRegistry.registerAll(SLIPPERY_BITER);
		EntitySpawnPlacementRegistry.register(SLIPPERY_BITER, EntitySpawnPlacementRegistry.PlacementType.IN_WATER,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlipperyBiterEntity::canSpawn);
		GlobalEntityTypeAttributes.put(SLIPPERY_BITER, SlipperyBiterEntity.registerAttributes().build());

		forgeRegistry.registerAll(GURTY);
		EntitySpawnPlacementRegistry.register(GURTY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GurtyEntity::canSpawn);
		GlobalEntityTypeAttributes.put(GURTY, GurtyEntity.registerAttributes().build());

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
		int rgSC = MyConfig.getRiverGuardianSpawnChance();
		int gSC = MyConfig.getGurtySpawnChance();
		int sbSC = MyConfig.getSlipperyBiterSpawnChance();


		Category biomeCategory = event.getCategory();
		MobSpawnInfoBuilder builder = event.getSpawns();
		
		String threadname = Thread.currentThread().getName();
		if (threadname.equals("Render thread")) {
//			return;
		} else {
			min = 1;
		}
		
		if (biomeCategory == Biome.Category.NETHER) {

			List<Spawners> wlist = builder.getSpawner(EntityClassification.MONSTER);
			
			boolean zombiePiglinSpawner = false;
			boolean ghastSpawner = false;

			// more efficient but less generic.
			// safer when deleting if go from end with i=size() i--; wlist.size()-1;
			for (int i = 0; i < wlist.size(); i++) {
				Spawners s = wlist.get(i);
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
					spawns.add(new Spawners(EntityType.ZOMBIFIED_PIGLIN, weight = MyConfig.getZombifiedPiglinSpawnBoost(),
						min = 1, max = 3));
				}
			}
			if (!ghastSpawner) {
				if (MyConfig.getGhastSpawnBoost() > 0) {
					spawns.add(new Spawners(EntityType.GHAST, weight = MyConfig.getGhastSpawnBoost(), min = 1,
							max = 1));
				}
			}
			
			spawns.add(new Spawners(RIVER_GUARDIAN, weight = 5, min = 1, max = 3));

		} else if (biomeCategory == Biome.Category.RIVER) {
			if (rgSC > 0) 
				spawns.add(new Spawners(RIVER_GUARDIAN, weight = rgSC+(rgSC/2)+1, min = 1, max = 1));
			if (sbSC > 0)
				spawns.add(new Spawners(SLIPPERY_BITER, weight = rgSC+(rgSC/3)+1, min = 1, max = 1));
			if (gSC > 0) 
				spawns.add(new Spawners(GURTY, weight = (gSC*9/10)+1, min = 1, max = 1));
			spawns.add(new Spawners(EntityType.COD, weight = MyConfig.getCodSpawnBoost()/3, min = 1, max = 2));
			spawns.add(new Spawners(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost()/5, min = 1, max = 2));
			spawns.add(new Spawners(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost()/2, min = 1, max = 4));
		} else if (biomeCategory == Biome.Category.SWAMP) {
			spawns.add(new Spawners(EntityType.COD, weight = MyConfig.getCodSpawnBoost()/2, min = 1, max = 2));
			if (rgSC > 0) 
				spawns.add(new Spawners(RIVER_GUARDIAN, weight = (rgSC*2/3)+1, min = 1, max = 1));
			if (sbSC > 0) 
				spawns.add(new Spawners(SLIPPERY_BITER, weight = (sbSC*2/3)+1, min = 1, max = 1));
			if (gSC > 0) 
				spawns.add(new Spawners(GURTY, weight = gSC, min = 1, max = 3));
		} else if (biomeCategory == Biome.Category.OCEAN) {
			if (rgSC > 0) 
				spawns.add(new Spawners(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
			if (sbSC > 0) 
				spawns.add(new Spawners(SLIPPERY_BITER, weight = sbSC, min = 1, max = 3));
			if (gSC > 0) 
				spawns.add(new Spawners(GURTY, weight = (gSC/2)+1, min = 1, max = 1));
			spawns.add(new Spawners(EntityType.COD, weight = MyConfig.getCodSpawnBoost(), min = 2, max = 4));
			spawns.add(new Spawners(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost(), min = 2, max = 4));
			spawns.add(new Spawners(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost(), min = 1, max = 4));
			spawns.add(new Spawners(EntityType.DOLPHIN, weight = MyConfig.getDolphinSpawnboost(), min = 1, max = 2));
		} else if (biomeCategory == Biome.Category.BEACH) {
			if (gSC > 0) 			
				spawns.add(new Spawners(GURTY, weight = (gSC/2)+1, min = 1, max = 3));
		} else {
			if (rgSC > 0) 
				spawns.add(new Spawners(RIVER_GUARDIAN, weight = (rgSC/8)+1, min = 1, max = 1));
			if (sbSC > 0) 
				spawns.add(new Spawners(SLIPPERY_BITER, weight = (sbSC/8)+1, min = 1, max = 2));
			if (gSC > 0) 
				spawns.add(new Spawners(GURTY, weight = (gSC/8)+1, min = 1, max = 1));
		}
	}

	public static void getFeatureSpawnData(List<Spawners> spawns, Structure<?> structure) {
		int weight;
		int min;
		int max;
		int rgSC = MyConfig.getRiverGuardianSpawnChance();
		int gSC = MyConfig.getGurtySpawnChance();
		int sbSC = MyConfig.getSlipperyBiterSpawnChance();
		String nameSpace = structure.getRegistryName().getNamespace();
		
				
		if (nameSpace.equals("minecraft")) {
			if (structure == Structure.OCEAN_RUIN) {
				if (rgSC > 0) 
					spawns.add(new Spawners(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new Spawners(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
			} else if (structure == Structure.SHIPWRECK) {
				if (rgSC > 0) 
					spawns.add(new Spawners(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new Spawners(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
			} else if (structure == Structure.BURIED_TREASURE) {
				if (rgSC > 0) 
					spawns.add(new Spawners(RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new Spawners(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
				if (gSC > 0) 
					spawns.add(new Spawners(GURTY, (gSC/4)+1, min = 1, max = 3));
			} else if (structure == Structure.SWAMP_HUT) {
				if (rgSC > 0) 
					spawns.add(new Spawners(RIVER_GUARDIAN, weight = (rgSC/10)+1, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new Spawners(SLIPPERY_BITER, (sbSC/10)+1, min = 1, max = 1));
				if (gSC > 0) 
					spawns.add(new Spawners(GURTY, weight = (gSC/10)+1, min = 1, max = 3));
			} else if (structure == Structure.RUINED_PORTAL) {
				if (rgSC > 0) 
					spawns.add(new Spawners(RIVER_GUARDIAN, weight = (rgSC/2)+1, min = 1, max = 1));
				if (sbSC > 0) 
					spawns.add(new Spawners(SLIPPERY_BITER, weight = (sbSC/3)+1, min = 1, max = 1));
				if (gSC > 0) 
					spawns.add(new Spawners(GURTY, weight = (gSC/4)+1, min = 1, max = 3));
			}
		} else {
			if (MyConfig.getModStructureBoost() > 0) {
				weight = MyConfig.getModStructureBoost();
				spawns.add(new Spawners(EntityType.SKELETON, weight , min = 1, max = 3));
				spawns.add(new Spawners(EntityType.ZOMBIE, 1 + weight/2, min = 1, max = 3));
				spawns.add(new Spawners(EntityType.WITCH, 1+ weight/6, min = 1, max = 3));
			}
		}
	}
}
